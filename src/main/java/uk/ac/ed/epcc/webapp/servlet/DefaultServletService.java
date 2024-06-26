//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.servlet.session.token.BearerTokenService;
import uk.ac.ed.epcc.webapp.servlet.session.token.ErrorCodes;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;
import uk.ac.ed.epcc.webapp.session.twofactor.TwoFactorHandler;
/** A default implementation for {@link ServletService} for a HTTP servlet application.
 * 
 * This class implements the following strategy for extracting a user from a HTTP request
 * 
 * If the the feature {@link DefaultServletService#EXTERNAL_AUTH_ONLY_FEATURE} is defined then
 * web-names will be used to resolve the current user for all URLs (even if not true then a explicit authentication servlet can
 * set session in this way, which is the normal mechanism if external and password authentication is needed). web-names can be taken from the REMOTE_USER or 
 * user certificate DN if present.
 * <p>
 * If the parameter <em>basic_auth.realm</em> is defined then missing authentication will
 * trigger HTTP Basic auth with the specified realm  (obviously the login factory needs to contain a {@link PasswordAuthComposite} for this to succeed.
 <p>
 * Note that as this parameter can be set for specific servlets via serlvet init-parameters. 
 * This can be used to replace the custom login page entirely if set globally but
 * should probably not be done unless this is the only authentication mechanism. 
 * On the other hand it is a good choice for servlets that take form posts
 * and urls where the credentials should be remembered by the browser 
 * <p>
 * Failing the above authentication failure will redirect to the login page.
 * @author spb
 *
 */
public class DefaultServletService implements ServletService{
	
	public static final String BASIC_AUTH_TYPE = "Basic";
	public static final String BEARER_AUTH_TYPE = "Bearer";
	/**
	 * 
	 */
	private static final String BEARER_ERROR_ATTR = "BearerError";
	/**
	 * 
	 */
	private static final String LOGOUT_REMOVE_COOKIE_PREFIX = "logout.remove_cookie.";
	public static final String BASIC_AUTH_REALM_PARAM="basic_auth.realm";
	public static final Feature NEED_CERTIFICATE_FEATURE = new Feature("need-certificate", true,"try additional mechanisms to retrieve certificate DN as web-name");
	public static final String PARAMS_KEY_NAME = "Params";
	Pattern auth_patt =  Pattern.compile("\\s*(\\w+)\\s+([\\w~/\\.\\+\\-]+=*)");
	protected final AppContext conn;
	private final ServletContext ctx;
    private final ServletRequest req; // cached request may be null
    private final ServletResponse res;
	private String web_path="";
    private int max_forward=10; // maximum number of calls to forward per request
	/** Feature to allow external auth logins if a webname is present. As this applies to all locations
	 * only a single default realm can be used.
	 * 
	 */
	public static final Feature ALLOW_EXTERNAL_AUTH_FEATURE = new Feature("allow_external_auth",false,"container level authorisation can be used on any url if a web-name is present");
	/** Feature to require external auth logins for all sessions.
	 * 
	 */
	public static final Feature EXTERNAL_AUTH_ONLY_FEATURE = new Feature("external_auth",false,"Default authorisation is using external container level authorisation on all URLs");

	public static final Feature EXTERNAL_AUTH_VIA_LOGIN_FEATURE = new Feature("external_auth.use_login",false,"Mandatory external auth with only login.jsp protected externally");

	public static final Feature ALLOW_INSECURE = new Feature("session.allow_insecure",false,"Allow insecure connections to use tokens");

	public static final Feature REPORT_URL_MODIFY = new Feature("session.report_url_modify",true,"Report urls with extra spaces to make them un-clickable but readable");
	
	// This should be true. If we have just created a session and forward to the login page it will
	// show the session-id in urls. If we redirect it will be set as a cookie (if cookies enabled(
	public static final Feature REDIRECT_TO_LOGIN_FEATURE = new Feature("login_page.always_redirect",true,"Always use redirect to go to login page");
	public DefaultServletService(AppContext conn,ServletContext ctx, ServletRequest req,
			ServletResponse res) {
		super();
		this.conn=conn;
		this.ctx = ctx;
		this.req = req;
		this.res = res;
		// check the expected charset of parameters etc.
		String ce = req.getCharacterEncoding();
		if( ce == null || ce.trim().length() == 0){
			String default_charset = defaultCharset();
			if( default_charset != null ){
				try {
					req.setCharacterEncoding(default_charset);
				} catch (UnsupportedEncodingException e) {
					error(e,"Problem with default charset");
				}
			}
		}
		if( req instanceof HttpServletRequest){
			web_path=((HttpServletRequest)req).getContextPath();
		}
	}


	public String defaultCharset() {
		return conn.getInitParameter("request.default_charset");
	}

	
	public void cleanup() {
		
	}

	public Class<? super ServletService> getType() {
		return ServletService.class;
	}
	/**
	 * encode the page request as a string for logging purposes
	 * 
	 * @param req
	 * @return String
	 */
	String encodePage(HttpServletRequest req) {
		if( req == null) {
			return "";
		}
		String res, tmp;

		
		res = req.getServletPath();
		if( res == null ) {
			res="";
		}
		tmp = req.getPathInfo();
		if (tmp != null) {
			if( ! tmp.startsWith("/")) {
				res = res + "/" + tmp;
			}else {
				res = res + tmp;
			}
		}
		tmp = req.getQueryString();
		if (tmp != null && ! tmp.isEmpty()) {
			res = res + "?" + tmp;
		}
		return res;

	}
    
	private String page = null;
	/** request page when ServletAppContext was created. 
	 * This uses a cached request object because the request URL will be
	 * re-written if the request is forwarded to a different servlet/jsp.
	 * 
	 * @return String URL
	 */
    public String encodePage(){
    	if( page != null ) {
    		return page;
    	}
    	if( req != null && req instanceof HttpServletRequest){
    		page = encodePage((HttpServletRequest) req);
    		return page;
    	}
    	return "";
    }
	/** Add the context path to a url path
	 * 
	 * @param url
	 * @return String
	 */
	public String encodeURL(String url){
		if( res != null && res instanceof HttpServletResponse ){
			if( getSession(false) != null ) {
				// make sure we don't trigger session creation
				return ((HttpServletResponse)res).encodeURL(web_path+url);
			}
		}
		return web_path+url;
	}
	private static final Pattern  FORWARD_PATT = Pattern.compile("^/[a-zA-Z0-9/_-]*(?:\\.[a-zA-Z0-9]+)?");
	/**
	 * Forward request to a different page.
	 * 
	 * @param url
	 *            String page to forward to
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forward(String url) throws ServletException, IOException {
		if( max_forward--  == 0 ){
			error("too many calls to forward url="+url);
			if( res instanceof HttpServletResponse){
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			return;
		}
		if( FORWARD_PATT.matcher(url).matches()){
		   ctx.getRequestDispatcher(url).forward(req, res);
		   return;
		}else{
			error("Badly formed url in forward url="+url);
			if( res instanceof HttpServletResponse){
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			return;
		}
		
	}
    public void redirect(String url) throws IOException {
    	conn.getService(LoggerService.class).getLogger(getClass()).debug("redirect to "+url);
    	if( req instanceof HttpServletRequest && res instanceof HttpServletResponse){
    		((HttpServletResponse)res).sendRedirect(((HttpServletResponse)res).encodeRedirectURL(((HttpServletRequest)req).getContextPath()+url));
    	}
    }
   
    public void redirect(URI url) throws IOException {
    	conn.getService(LoggerService.class).getLogger(getClass()).debug("redirect to "+url);
    	if( req instanceof HttpServletRequest && res instanceof HttpServletResponse){
    		((HttpServletResponse)res).sendRedirect(url.toASCIIString());
    	}
    }

    public void message(String message, Object ... args) throws IOException, ServletException {
    	if( req instanceof HttpServletRequest && res instanceof HttpServletResponse){
    		WebappServlet.messageWithArgs(getContext(),
    				(HttpServletRequest)req,
    				(HttpServletResponse)res,
    				message,
    				args);
    	}
    }



	

	

	

	/**
	 * Extract a Map of the request parameters from the request. This is made a
	 * method on the ServletAppContext to allow access to logging/configuration
	 * and to allow the application specific customisation.
	 * 
	 * @return Map of parameters
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getParams() {
		Map<String,Object> h = null;
		if( req != null ) {
			// look for cached value
			h = (Map<String,Object>) req.getAttribute(PARAMS_KEY_NAME);
			if (h == null) {
				h = makeParams((HttpServletRequest) req);

				req.setAttribute(PARAMS_KEY_NAME, h);
			}
		}
		return h;
		// Caching causes problems when adding params on a page forward
		//
		//return makeParams(req);
	}
	
	/** Get the ServletPath as a list of strings
	 * A path element of "-" terminates the list. 
	 * This is to allow parameters to be passed on the 
	 * path at the same time as an arguemnt path.
	 * @return LinkedList<String> arguments in order.
	 */
    public LinkedList<String> getArgs(){
    	LinkedList<String> h = new LinkedList<>();
    	if( req != null && req instanceof HttpServletRequest){
    	String path = ((HttpServletRequest)req).getPathInfo();
		if (path != null) {
			StringTokenizer st = new StringTokenizer(path, "/", false);
			while( st.hasMoreTokens()) {
				String val = st.nextToken();
				if( val.equals(ServletService.ARG_TERRMINATOR)){
					return h;
				}
				h.add(val);
			}
		}
    	}
		return h;
    }
    
    public String getFilePath() {
    	
    	LinkedList<String> h = new LinkedList<>();
    	if( req != null && req instanceof HttpServletRequest){
    	String path = ((HttpServletRequest)req).getPathInfo();
    	boolean add = false;
		if (path != null) {
			StringTokenizer st = new StringTokenizer(path, "/", false);
			while( st.hasMoreTokens()) {
				String val = st.nextToken();
				if( val.equals(ServletService.ARG_TERRMINATOR)){
					add=true;
				}else if( add ) {
					h.add(val);
				}
			}
		}
    	}
    	if( h.isEmpty()) {
    		return "";
    	}
    	return "/"+String.join("/", h);
    }
	
	/**
	 * get the authenticated name for the current user as provided by the
	 * web-server/container authorisation layer. This will be null unless the
	 * container/web-server has authorisation turned on for this URL.
	 * 
	 * This method uses the request object cached in the high level filter
	 * 
	 * @return String webname
	 */
	public String getWebName() {
		return getWebName(req);
	}
	/**
	 * get the authenticated name for the current user as provided by the
	 * web-server/container authorisation layer. This will be null unless the
	 * container/web-server has authorisation turned on for this URL.
	 * 
	 * This method takes an explicit {@link ServletRequest} object rather than
	 * the one cached in the service itself. It can therefore be used when a
	 * filter may be in place.
	 * 
	 * 
	 */
	public String getWebName(ServletRequest req) {
		String name=null;
		if( req != null) {
		// If the web.xml defines a certificate based security-constraint then the
		// certificate info will be available from getUserPrincipal
		Principal p = null;
		if( req instanceof HttpServletRequest){
			p =((HttpServletRequest)req).getUserPrincipal();
		}
		if( p == null && NEED_CERTIFICATE_FEATURE.isEnabled(conn) && req.isSecure()){
			// If clientAuth is turned on in the tomcat connector a certificate will always
			// be requested even without a security constraint and this can be obtained as follows
			Object certificateChainAtt = 
					req.getAttribute("javax.servlet.request.X509Certificate");
			if( certificateChainAtt != null ){

				X509Certificate[] certificateChain = (X509Certificate[])certificateChainAtt;
				if( certificateChain != null && certificateChain.length > 0){
					p = certificateChain[0].getSubjectX500Principal();
				}
			}
		}
		if( p != null ){
			name=p.getName();
		}else{
			// probably won't work if getUserPrincipal does not but might as well try
			if( req instanceof HttpServletRequest){
				name=((HttpServletRequest)req).getRemoteUser();
			}
		}
		if (name != null) {
			return name.trim();
		}
		}
		return null;
	}



	
	/**
	 * create a Map of request parameters from scratch. Application specific sub-classes can
	 * override this to customise the behaviour. For example to parse MultiPart
	 * forms in order to support file upload.
	 * 
	 * @param req
	 * @return Map of request parameters
	 */
	public Map<String,Object> makeParams(HttpServletRequest req)  {
		Map<String,Object> h = new LinkedHashMap<String, Object>();
		
		
		String path = req.getPathInfo();
		if (path != null) {
			StringTokenizer st = new StringTokenizer(path, "/", false);
			for (int i = 0; st.hasMoreTokens(); i++) {
				String val = st.nextToken();
				String key, data;
				int id = val.indexOf("=");
				if (id == -1) {
					key = "$" + i;
					data = val;
				} else {
					key = val.substring(0, id).trim();
					data = val.substring(id + 1).trim();
				}
				h.put(key, data);
			}
		}
		// h.putAll(req.getParameterMap());
		
		// normal request parameters are higher precidence than those passed on the path
		for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
			String key =  (String) e.nextElement();
			
			String[] values = req.getParameterValues(key);
			if( values.length == 1){
				h.put(key, values[0]);
			}else{
				// Consider parameters with multiple values and take the first non empty one
				// ignore pure whitespace too. This is really only used by the fall-back
				// for auto-complete inputs. If the UA respects required but does not
				// understand datalist of the JS fixup then a space can be added to the text box.
				for(String data : values){
					if( data != null && ! data.trim().isEmpty() ){
						h.put(key, data);
						break;
					}
				}
			}
		}
		String type = req.getContentType();
		// if this is not one of the already handled form types
		// map the request body to a StreamData
		if( ! h.containsKey(ServletService.DEFAULT_PAYLOAD_PARAM) && type != null && ! type.contains("x-www-form-urlencoded") && !  type.startsWith("multipart")) {
			h.put(ServletService.DEFAULT_PAYLOAD_PARAM,new RequestMimeStreamData(getContext(), req));
		}
		return h;
	}



	

	
	public HttpSession getSession(){
		// By default don't create a session unless it already exists
		// This is to prevent a cookie being created unless the user has
		// actively logged in.
		// Note that unless you set session=false in a jsp page directive
		// jsp pages will create a session
		return getSession(false);
	}
	public HttpSession getSession(boolean make_session){
		if( req instanceof HttpServletRequest){
			return ((HttpServletRequest)req).getSession(make_session);
		}else{
			return null;
		}
	}
	public HttpServletRequest getRequest(){
		if( req instanceof HttpServletRequest){
			return ((HttpServletRequest)req);
		}else{
			return null;
		}
	}
	/** Is the specified mime-type supported by the client.
	 * 
	 * @param type
	 * @return boolean true if mime type supported
	 */
	public boolean supportsMime(String type){
		if(req instanceof HttpServletRequest){
		  String accept = ((HttpServletRequest)req).getHeader("Accept");
	      if( accept != null ){
	      	return accept.toLowerCase().contains(type);
	      }
		}
	    return false;
	}


	public AppContext getContext() {
		return conn;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#requestAuthorization()
	 */
	public <A extends AppUser> void requestAuthentication(SessionService<A> sess) throws IOException, ServletException {
		if( isComitted()) {
			// Can't do anything
			return;
		}
		
		BearerTokenService bearer = getContext().getService(BearerTokenService.class);
		int code = HttpServletResponse.SC_UNAUTHORIZED;
		if( bearer != null ) {
			// only request if secure conneciton
			if(  bearer.request() && res instanceof HttpServletResponse && req.isSecure()) {
				StringBuilder header =new StringBuilder();
				header.append(BEARER_AUTH_TYPE);
				String token_realm = bearer.getRealm();
				if( token_realm != null &&  ! token_realm.isEmpty()) {
					header.append(" realm=\"");
					header.append(token_realm);
					header.append("\"");
				}
			
				Set<String> scopes = bearer.requestedScopes();
				if( scopes != null && ! scopes.isEmpty()) {
					header.append(", scope=\"");
					header.append(String.join(" ", scopes));
					header.append("\"");
				}
				ErrorCodes token_error = bearer.getError();
				if( token_error != null ) {
					header.append(", error=\"");
					header.append(token_error.toString());
					header.append("\"");
					code=token_error.getCode();
				}
				((HttpServletResponse)res).setHeader("WWW-Authenticate", header.toString());
				((HttpServletResponse)res).sendError(code);
				return;
				
				
			}
		}
		if( sess == null) {
			// jsut send code
			((HttpServletResponse)res).sendError(code);
			return;
		}
		AppUserFactory<A> factory = sess.getLoginFactory();
		@SuppressWarnings("unchecked")
		PasswordAuthComposite<A> composite = (PasswordAuthComposite<A>) factory.getComposite(PasswordAuthComposite.class);
		if( composite != null ){
			// Should we request basic auth
			String realm = conn.getInitParameter(BASIC_AUTH_REALM_PARAM);
			if( realm != null && realm.trim().length() > 0 && res instanceof HttpServletResponse){
				((HttpServletResponse)res).setHeader("WWW-Authenticate", "Basic realm=\""+realm+"\"");
				((HttpServletResponse)res).sendError(code);
				return;
			}
		}
		
		boolean external_via_login = EXTERNAL_AUTH_VIA_LOGIN_FEATURE.isEnabled(conn);
		if( getWebName() == null  
				&& (! external_via_login) &&
			( composite == null || EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(getContext()))){
			// We require external auth so must have web-name
			warn("No webname when required");
			message( "access_denied", (Object[])null);
		}else{
			String page = encodePage();
			requestLogin(sess, page);
		}
	}


	public <A extends AppUser> void requestLogin(SessionService<A> sess, String username, String page)
			throws IOException, ServletException {
		// Need to remember page and redirect to login
		if( page !=null&& ! page.isEmpty()) {
			LoginServlet.setSavedResult(sess,  new RedirectResult(page));
		}
		if( req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
			// IF we want more than one rule then configure a RequestLoginPluginList
			RequestLoginPlugin plugin = getContext().makeObject(RequestLoginPlugin.class, "request_login_plugin");
			if( plugin != null) {
				FormResult result = plugin.requestLogin((HttpServletRequest)req, (HttpServletResponse) res);
				if( result != null) {
					try {
						result.accept(new ServletFormResultVisitor(getContext(),(HttpServletRequest) req, (HttpServletResponse) res));
						return;
					} catch (Exception e) {
						error(e,"Error implementing login request from plug-in");
					}

				}
			}
		}
		// standard login page supports both custom password login and self-register for external-auth
		// If built_in login is off we might change the login page to an external auth servlet url.
		String login_page=LoginServlet.getLoginPage(getContext());
		if( username != null && ! username.isEmpty()) {
			try {
				sess.getLoginFactory().validateNameFormat(username);
				login_page = login_page+"?username="+username;
			}catch(Exception e) {
				
			}
		}
				
		if( EXTERNAL_AUTH_VIA_LOGIN_FEATURE.isEnabled(getContext()) || REDIRECT_TO_LOGIN_FEATURE.isEnabled(getContext()) || ! LoginServlet.BUILT_IN_LOGIN.isEnabled(getContext())) {
			// A non encoding redirect to make sure there is no session in the url
			// We may well be creating a session cookie at this point (we need to remember the requested page)
			// but might still get re-write if no cookie seen in request.
			// risk is a non cookie browser will lose the destination location but
			// that is an edge case and tolerable where session leakage is a risk for everyone.
			if( req instanceof HttpServletRequest && res instanceof HttpServletResponse){
	    		((HttpServletResponse)res).sendRedirect(((HttpServletRequest)req).getContextPath()+login_page);
	    	}else {
	    		error("Unexpected request/response type "+req.getClass().getCanonicalName()+" "+res.getClass().getCanonicalName());
	    	}
		}else {
			forward(login_page);
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#populateSession(uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public <A extends AppUser>void populateSession(SessionService<A> sess) {
		try{
			if( res.isCommitted()){
				// Can't make a servlet session 
				return;
			}
			AppContext conn = getContext();
			Logger log = null;
			LoggerService ls = conn.getService(LoggerService.class);
			if( ls != null ) {
				log = ls.getLogger(getClass());
			}
			String name = getWebName();
			// This is for on-the-fly remote authentication at any url
			// don't do this if the authentication flow is expensive
			// use RemoteAuthServlet (with similar code to this)
			// for a single auth point. Also allows multiple mechanisms in the
			// same application
			if( name != null && (DefaultServletService.ALLOW_EXTERNAL_AUTH_FEATURE.isEnabled(conn) || DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn))){
				// If there is a web-name we don't consider password login
				AppUserFactory<A> factory = sess.getLoginFactory();
				String remote_auth_realm = conn.getInitParameter(RemoteAuthServlet.REMOTE_AUTH_REALM_PROP, WebNameFinder.WEB_NAME);
				AppUserNameFinder<A, ?> finder = factory.getRealmFinder(remote_auth_realm);
				
				if( finder != null ){

					A person = finder.findFromString(name);
					if( person == null ){
						// See if we should auto-create users.
						if( factory.autoCreate()){
							person = factory.makeUser();
							if( person != null ){
								finder.setName(person, name);
								person.commit();
							}
						}
					}
					if( person != null ){
						if( person.canLogin()){
							finder.verified(person); // record sucessful authentication
							for(RemoteAuthListener l : ((AppUserFactory<?>)person.getFactory()).getComposites(RemoteAuthListener.class)){
								l.authenticated(remote_auth_realm,person);
							}
							person.commit();
							person.historyUpdate();
							if( factory.mustRegister(person)){
								// don't populate session this will trigger redirect to
								// the registration page
								return;
							}
							// If we want to support 2-factor with external login 
							// we should do this via a required page.
							sess.setCurrentPerson(person);
							sess.setAuthenticationType(remote_auth_realm);
							CurrentTimeService time = getContext().getService(CurrentTimeService.class);
							if( time != null) {
								sess.setAuthenticationTime(time.getCurrentTime());
							}
						}	
					}

				}
			}else if (DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn) ){
				// name must be null
				return;
			}
			HttpServletRequest request = getRequest();
			if(request != null && ! DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn) ) {
				// only consider authorisation headers if no webname found
				// Note we pick up the top level config service so that per-servlet parameters
				// are available
				String auth = request.getHeader("Authorization");
				if( log != null ) {
					log.debug(()->"Authorization header: "+auth);
				}
				if( auth != null ) {
					Matcher m = auth_patt.matcher(auth);
					if( m.matches()) {
						String type = m.group(1);
						String cred = m.group(2);
						if( type.equalsIgnoreCase(BEARER_AUTH_TYPE)) {
							BearerTokenService bearer = getContext().getService(BearerTokenService.class);
							if( bearer != null ) {
								if( ! (request.isSecure() || ALLOW_INSECURE.isEnabled(getContext()))){
									if( log != null) {
										log.error("Bearer token sent via insecure connection");
									}
									error("Bearer token from insecure connection");
									return;
								}
								if( log != null) {
									log.debug("Processing bearer token "+cred);
								}
								// Let the bearer token service do everything
								// this is to allow it to implement anonymous role only sessions
								bearer.processToken(sess, cred);
								return;
							}else {
								if( log != null ) {
								 log.debug("No BearerTokenService");
								}
							}
						}else if( type.equalsIgnoreCase(BASIC_AUTH_TYPE)) {
							AppUserFactory<A> factory = sess.getLoginFactory();
							@SuppressWarnings("unchecked")
							PasswordAuthComposite<A> comp = factory.getComposite(PasswordAuthComposite.class);
							String userpass= decode(cred);
							int pos = userpass.indexOf(":");
							if( pos > 0 ){
								String user = userpass.substring(0, pos);
								String pass = userpass.substring(pos+1);
								try {

									A person = (A) comp.findByLoginNamePassword(user, pass);
									
									if( person != null && person.canLogin()){
										TwoFactorHandler<A> hand = new TwoFactorHandler<>(sess);
										// This will do the login UNLESS MFA required
										if( hand.doLogin(person, type, null) != null) {
											log.warn("Basic auth used with MFA configured "+person.getIdentifier());
										}
									}else {
										//TODO handle bad authentication
										// could remember error in response and return forbidden in requestAuthentication
										if( person != null && log != null) {
											log.warn("Forbidden login "+person.getIdentifier());
										}
									}
								} catch (DataException e) {
									error(e,"Error looking up person");
								}
							}
						}else {
							if( log != null ) {
								log.debug("Unrecognised authentication type "+type);
							}
						}
					}
				}
			}
		}catch(Exception e){
			error(e,"Error populating sesion from request");
		}
	}

	/**invalidate the servlet session and optionally remove the session cookie.
	 *
	 * 
	 * 
	 * @param remove_cookie should cookie be removed
	 * 
	 */
	public void logout(boolean remove_cookie){
		HttpSession sess = getSession();
		if( sess != null){
			sess.invalidate();
		}
		if( remove_cookie){
			HttpServletRequest request = getRequest();
			if( request != null ){
				Cookie[] cookies = request.getCookies();
				if( cookies != null && cookies.length > 0){
					for( Cookie c : cookies){
						if( c.getName().equalsIgnoreCase("JSESSIONID") || getContext().getBooleanParameter(LOGOUT_REMOVE_COOKIE_PREFIX+c.getName(), false)){
							Cookie c2 = (Cookie) c.clone();
							//c2.setHttpOnly(true); // for owasp scan
							c2.setMaxAge(0); // This should request a delete
							if( c2.getPath() == null ){
								String contextPath = request.getContextPath();
								if( contextPath == null || contextPath.isEmpty()) {
									contextPath="/";
								}
								c2.setPath(contextPath); // browser did not include path. This will only work if path matched exactly
							}
							c2.setValue("");
							((HttpServletResponse)res).addCookie(c2);
						}
					}
				}
			}
		}
	}

	public void addCookie(Cookie c){
		if( ! isComitted()){
			((HttpServletResponse)res).addCookie(c);
		}
	}
	protected static String decode(String base64) {
		return new String(Base64.getDecoder().decode(base64));
	}
	/**
	 * Report an application error.
	 * Needs to handle the possiblity of the LoggerService not being present as
	 * we can't make it a pre-requisite here
	 * 
	 * @param errors
	 *            Text of error.
	 */
	
	final void error(String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors);
			}
		}
	}
	final void warn(String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.warn(errors);
			}
		}
	}
	final void error(Throwable t,String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors,t);
			}
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#isComitted()
	 */
	@Override
	public boolean isComitted() {
		return res.isCommitted();
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#addErrorProps(java.util.Map)
	 */
	@Override
	public void addErrorProps(Map props) {
		HttpServletRequest req = getRequest();
		if (req != null ) {
			
			String url = null;
			StringBuffer buf =  getRequestURI(req);
			if( buf != null ){
				url = buf.toString();
			}
			if( url != null && url.contains("password")){
				url="redacted";
			}
			if( url != null ){
				props.put("request_url", url);
			}
			Date req_start = (Date) req.getAttribute(ErrorFilter.REQUEST_START);
			if( req_start != null) {
				props.put("request_date", req_start);
			}
			// Get the user-agent info
			Vector<String> headers = new Vector<>();
			for (Enumeration enumeration = req.getHeaderNames(); enumeration
			.hasMoreElements();) {
				String header = (String) enumeration.nextElement();
				if( ! header.contains("cookie") && ! header.contains("authorization")){
					// don't log security sensative info
					headers.add("    " + header + " = '" + req.getHeader(header)
							+ "'\n");
				}
			}
			props.put("headers", headers);
			props.put("request_method", req.getMethod());

			StringBuilder service_list = new StringBuilder();
			for(AppContextService s : getContext().getServices()){
				service_list.append("   ");
				service_list.append(s.getType().getSimpleName());
				service_list.append(": ");
				service_list.append(s.getClass().getCanonicalName());
				service_list.append("\n");
			}
			props.put("services", service_list.toString());
			// Show IP Address of current remote client
			String ip_address = null;

			ip_address = req.getRemoteAddr();

			if (ip_address != null) {
				props.put("ip_address", ip_address);
			}

			// And show all parameters
			StringBuilder psb = new StringBuilder();

			for (Enumeration param_names = req.getParameterNames(); param_names
			.hasMoreElements();) {
				String name = (String) param_names.nextElement();
				if( ! name.equalsIgnoreCase("password")){
					psb.append("  ");
					psb.append(name); 

					String val = req.getParameter(name);
					if( val.length() < 512){
						psb.append(" = ");
						psb.append(val);
					}else{
						psb.append(" - long parameter");
					}
					psb.append("\n");
				}
			}

			if (psb.length() > 0) {
				props.put("parameters", psb.toString());
			}
		}
		
	}


	/** Intercept method to get the requestURI as a StringBuffer
	 * 
	 * Though {@link HttpServletRequest} has a method to do this
	 * this allows us to override this to avoid email URL re-write
	 * (e.g. MS "safe-links") we want this value to be readable but
	 * not necessarily clickable
	 * 
	 * @param req
	 * @return
	 */
	private StringBuffer getRequestURI(HttpServletRequest req) {
		if( REPORT_URL_MODIFY.isEnabled(getContext())) {
			StringBuffer sb = new StringBuffer();
			sb.append(req.getScheme());
			sb.append(":/ /"); // extra space
			sb.append(req.getServerName());
			sb.append(req.getContextPath());
			sb.append("/");
			sb.append(encodePage());
			
			return sb;
		}
		return req.getRequestURL();
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#noCache()
	 */
	@Override
	public void noCache() {
		if( res instanceof HttpServletResponse){
			HttpServletResponse h = (HttpServletResponse) res;
			h.setHeader("Cache-Control", "no-store");
			h.addHeader("Pragma", "no-cache");
		}
		
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#setRequestAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setRequestAttribute(String name, Object value) {
		getRequest().setAttribute(name, value);
		
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#getRequestAttribute(java.lang.String)
	 */
	@Override
	public Object getRequestAttribute(String name) {
		return getRequest().getAttribute(name);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.ServletService#getAttributeNames()
	 */
	@Override
	public Iterable<String> getAttributeNames() {
		Set<String> names = new HashSet<>();
		for(Enumeration<String> e=getRequest().getAttributeNames(); e.hasMoreElements(); ) {
			names.add(e.nextElement());
		}
		return names;
	}


	@Override
	public void setTimeout(int seconds) {
		HttpSession sess = getSession();
		if( sess != null ) {
			sess.setMaxInactiveInterval(seconds);
		}
		
	}


	@Override
	public void sendError(int code, String message) throws IOException {
		getRequest().setAttribute(ERROR_MSG_ATTR, message);
		if( res != null && res instanceof HttpServletResponse ){
			((HttpServletResponse)res).sendError(code, message);
		}
	}
}