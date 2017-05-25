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
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
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

import org.apache.commons.codec.binary.Base64;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;
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
	/**
	 * 
	 */
	private static final String LOGOUT_REMOVE_COOKIE_PREFIX = "logout.remove_cookie.";
	public static final String BASIC_AUTH_REALM_PARAM="basic_auth.realm";
	public static final Feature NEED_CERTIFICATE_FEATURE = new Feature("need-certificate", true,"try additional mechanisms to retrieve certificate DN as web-name");
	public static final String PARAMS_KEY_NAME = "Params";
	public static final String ARG_TERRMINATOR = "-";
	
	Pattern auth_patt = Pattern.compile("\\s*Basic\\s+(\\S*)");
	
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

	public static final Feature EXTERNAL_AUTH_VIA_lOGIN_FEATURE = new Feature("external_auth.use_login",false,"Mandatory external auth with only login.jsp protected externally");

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
		String res, tmp;

		
		res = req.getServletPath();
		tmp = req.getPathInfo();
		if (tmp != null) {
			res = res + "/" + tmp;
		}
		tmp = req.getQueryString();
		if (tmp != null) {
			res = res + "?" + tmp;
		}
		return res;

	}
	/** request page when ServletAppContext was created. 
	 * This uses a cached request object because the request URL will be
	 * re-written if the request is forwarded to a different servlet/jsp.
	 * 
	 * @return String URL
	 */
    public String encodePage(){
    	if( req != null && req instanceof HttpServletRequest){
    		return encodePage((HttpServletRequest) req);
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
			return ((HttpServletResponse)res).encodeURL(web_path+url);
		}else{
			return web_path+url;
		}
	}
	/**
	 * Forward request to a different page.
	 * 
	 * @param url
	 *            String page to forward to
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forward(String url) throws ServletException, IOException {
		conn.getService(LoggerService.class).getLogger(getClass()).debug("forwarding to "+url);
		if( max_forward--  == 0 ){
			error("too many calls to forward url="+url);
			if( res instanceof HttpServletResponse){
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			return;
		}
		if( Pattern.matches("^/[a-zA-Z/_-]*(?:\\.[a-zA-Z]+)?$",url)){
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
		// look for cached value
		h = (Map<String,Object>) req.getAttribute(PARAMS_KEY_NAME);
		if (h == null) {
			h = makeParams((HttpServletRequest) req);

			req.setAttribute(PARAMS_KEY_NAME, h);
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
    	LinkedList<String> h = new LinkedList<String>();
    	if( req instanceof HttpServletRequest){
    	String path = ((HttpServletRequest)req).getPathInfo();
		if (path != null) {
			StringTokenizer st = new StringTokenizer(path, "/", false);
			while( st.hasMoreTokens()) {
				String val = st.nextToken();
				if( val.equals(ARG_TERRMINATOR)){
					return h;
				}
				h.add(val);
			}
		}
    	}
		return h;
    }
	
	/**
	 * get the authenticated name for the current user as provided by the
	 * web-server/container authorisation layer. This will be null unless the
	 * container/web-server has authorisation turned on for this URL.
	 * 
	 * @return String webname
	 */
	public String getWebName() {
		String name=null;
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
		Hashtable<String,Object> h = new Hashtable<String,Object>();
		

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
		AppUserFactory<A> factory = sess.getLoginFactory();
		@SuppressWarnings("unchecked")
		PasswordAuthComposite<A> composite = (PasswordAuthComposite<A>) factory.getComposite(PasswordAuthComposite.class);
		if( composite != null ){
			// Should we attempt basic auth
			String realm = conn.getInitParameter(BASIC_AUTH_REALM_PARAM);
			if( realm != null && realm.trim().length() > 0 && res instanceof HttpServletResponse){
				((HttpServletResponse)res).setHeader("WWW-Authenticate", "Basic realm=\""+realm+"\"");
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		if( getWebName() == null  
				&& (! EXTERNAL_AUTH_VIA_lOGIN_FEATURE.isEnabled(conn)) &&
			( composite == null || EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(getContext()))){
			// We require external auth so must have web-name
			warn("No webname when required");
			message( "access_denied", (Object[])null);
		}else{
			// standard login page supports both custom password login and self-register for external-auth
			String login_page=LoginServlet.getLoginPage(conn);
			redirect(login_page+"?error=session&page="+encodePage());
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
			String name = getWebName();
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

							if( factory.mustRegister(person)){
								// don't populate session this will trigger redirect to
								// the registration page
								return;
							}
							sess.setCurrentPerson(person);
						}	
					}

				}
			}else if (DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn) ){
			}else{
				// only consider basic-auth if no webname found
				// Note we pick up the top level config service so that per-servlet parameters
				// are available
				String realm = conn.getInitParameter(BASIC_AUTH_REALM_PARAM);
				HttpServletRequest request = getRequest();
				if( request != null && realm != null && realm.trim().length() > 0 && ! DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn)) {
					
					String auth = request.getHeader("Authorization");
					AppUserFactory<A> factory = sess.getLoginFactory();
					@SuppressWarnings("unchecked")
					PasswordAuthComposite<A> comp = factory.getComposite(PasswordAuthComposite.class);
					if( auth != null && comp != null ){
						Matcher m = auth_patt.matcher(auth);
						if( m.matches()){
							String base64 = m.group(1);
							String userpass= decode(base64);
							int pos = userpass.indexOf(":");
							if( pos > 0 ){
								String user = userpass.substring(0, pos);
								String pass = userpass.substring(pos+1);
								try {
									
										A person = (A) comp.findByLoginNamePassword(user, pass);
										if( person != null && person.canLogin()){
											sess.setCurrentPerson(person);
										}
								} catch (DataException e) {
									error(e,"Error looking up person");
								}
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
							c.setMaxAge(0);
							((HttpServletResponse)res).addCookie(c);
						}
					}
				}
			}
		}
	}

	protected static String decode(String base64) {
		return new String(Base64.decodeBase64(base64));
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
}