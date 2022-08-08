//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.Hash;
import uk.ac.ed.epcc.webapp.session.RandomService;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;
import uk.ac.ed.epcc.webapp.session.twofactor.TwoFactorHandler;

/**
 * This servlet is to support authentication from the container. This servlet
 * should be configured with server level authentication and the user redirects
 * through this servlet to either login based on the remote username or to
 * change their remote username if the servlet is visited after logging in.
 *<p>
 *The property <b>remote_auth.realm</b> (which may be set as a servlet parameter) defines the name realm used by the servlet.
 *Multiple types of external authentication can be supported by including the servlet at multiple paths, one for each realm.
 *
 *<p>
 *This servlet also supports the use of a remote authentication server rather than expecting the name to be provided by
 *the container. This should only be needed for methods the container cannot support directly.
 *In this mode the user is redirected to a remote server. A session token will be included as the url parameter <b>token</b>.
 *The server should authenticate the user and (if successful) redirect back to this servlet setting the <b>auth_name</b> parameter to the authenticated
 *user. The response should also contain the <b>check_token</b> parameter which should be a hex encoded hash value calculated from the  concatenation of
 * the <i>user-name</i>, <i>the-token</i> and a <i>secret-value</i> shared between the two servers.  Optionally the server can pre-pend the
 * response with a salt string. In this case the salt should also be returned as a parameter named <b>salt</b>. Adding a salt to the check_token
 * may make it harder for a malicious user to reverse engineer the check_token as the unknown parts of the input vary each call. This should not be required for a 
 * well implemented hash function.  
 *<ul>
 *<li><b>remote_auth_server.url</b> A URL of a remote server the user will be redirected to. </li>
 *<li><b>remote_auth_server.secret</b> The shared secret.</li>
 *<li><b>remote_auth_server.hash</b> The name of the {@link Hash} to use defaults to SHA512</i>
 *<li><b>remote_auth_server.token_len</b> The length of the session token defaults to 64 </li>
 *</ul>
 *
 * @author spb
 * 
 */


public class RemoteAuthServlet extends WebappServlet {

	/**
	 * 
	 */
	private static final String REMOTE_AUTH_ALLOW_LOGIN_PREFIX = "remote_auth.allow_login.";


	/**
	 * 
	 */
	private static final String EXTAUTH_REGISTER_ID_ATTR = "EXTAUTH_REGISTER_ID_ATTR";


	/**
	 * 
	 */
	private static final String REMOTE_AUTH_TOKEN_ATTR = "remote_auth_token";


	/** Property name to use for the authentication realm. Note that this is also the
	 * property used by {@link DefaultServletService} and {@link RegisterServlet} if global external authentication is supported.
	 * 
	 */
	public static final String REMOTE_AUTH_REALM_PROP = "remote_auth.realm";


	private static final String REMOTE_AUTH_NEXT_URL="remote_auth.next_result";
	/**
	 * 
	 */
	public static final String SERVICE_WEB_LOGIN_UPDATE_TEXT = "service.web_login.update-text";


	/**
	 * 
	 */
	public static final String REGISTER_IDENTITY_DEFAULT_TEXT = "Register identity";


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Feature to allow external auth logins as alternative mechanism via specific servlets linked from the
	 * login page. Multiple alternative mechanisms can be supported this way. 
	 * 
	 */
	public static final Feature WEB_LOGIN_FEATURE = new Feature("web_login",false,"container level authorisation can be used as an alternate login method");
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		Logger log = getLogger(conn);
		if( ! WEB_LOGIN_FEATURE.isEnabled(conn)){
			log.debug("web-login is disabled");
			message(conn,req,res,"disabled_feature_error");
			return;
		}
		SessionService session_service = conn.getService(SessionService.class);
		ServletService servlet_service = conn.getService(ServletService.class);
		String web_name = servlet_service.getWebName(req);
		try {
			
			if (empty(web_name)) {
				log.debug("No web-name found");
				// Might have bounce-server authentication on this server
				String auth_url = conn.getExpandedProperty("remote_auth_server.url");
				
				if( auth_url != null && auth_url.trim().length() > 0 ){
					String token = (String) session_service.getAttribute(REMOTE_AUTH_TOKEN_ATTR);
					Map<String,Object> params = servlet_service.getParams();
					String remote_name = (String) params.get("auth_name");
					String check_token = (String) params.get("check_token");
					if( empty(token)  || remote_name == null || check_token == null ){
						// make and remember a new token the token ensures the response 
						// and the original request are from the same httpSession
						// otherwise a valid response will allow perminant access to the account
						RandomService random_serv = conn.getService(RandomService.class);
						token = random_serv.randomString(conn.getIntegerParameter("remote_auth_server.token_len", 64));
						session_service.setAttribute(REMOTE_AUTH_TOKEN_ATTR, token);
						// redirect to remote what about non cookie sessions.
						String redirect_url =auth_url+"?token="+token;
						// Don't encode the url as its a known external url
						res.sendRedirect(redirect_url);
						return;
					}
					
					// Must be a response from remote server
					// We expect a hash containing the name (stops a malicious user changing the name) 
					// the session token (stops re-use of responses) and a shared secret (proves the response 
					// is actually from the authentication server
					Hash h = conn.getEnumParameter(Hash.class, "remote_auth_server.hash", Hash.SHA512);
					String auth_secret = conn.getExpandedProperty("remote_auth_server.secret");
					String salt = (String) params.get("salt");
					if( salt == null){
						salt="";
					}
					if( check_token.equals(h.getHash(salt+remote_name+token+auth_secret))){
						// all as expected set the web_name
						web_name=remote_name;
						session_service.removeAttribute(REMOTE_AUTH_TOKEN_ATTR);
					}else{
						web_name=null;
						message(conn, req, res, "invalid_input");
						return;
					}
				}
				if( empty(web_name)){
					getLogger(conn).warn("missing web_name");
					// we must have a remote user name
					message(conn, req, res, "remote_auth_missing");
					return;
				}
			}
			log.debug("web_name is "+web_name);
			AppUser person = null;

			
			person = getTargetAppUser(session_service);
			String remote_auth_realm = conn.getExpandedProperty(REMOTE_AUTH_REALM_PROP, WebNameFinder.WEB_NAME);
			boolean allow_login = canLogin(conn, remote_auth_realm);
			AppUserFactory<?> fac = session_service.getLoginFactory();
			AppUserNameFinder parser = fac.getRealmFinder(remote_auth_realm);
			if( parser == null) {
				log.error("No realm finder found for "+remote_auth_realm);
				message(conn,req,res,"internal_error","No realm finder found");
				return;
			}
			if (person == null) {
				person = parser.findFromString(web_name);
				if (person == null) {
						String register_text = session_service.getContext().getInitParameter(SERVICE_WEB_LOGIN_UPDATE_TEXT,REGISTER_IDENTITY_DEFAULT_TEXT);
						message(conn, req, res, "unknown_web_login", register_text);
						return;
				}
				if( allow_login ) {
					// Note same flow exists in DefaultServletService for global extauth
					parser.verified(person); // record sucessful authentication
					for(RemoteAuthListener l : ((AppUserFactory<?>)person.getFactory()).getComposites(RemoteAuthListener.class)){
						l.authenticated(remote_auth_realm,person);
					}
					person.commit();
					person.historyUpdate();
				// attempt a login
					TwoFactorHandler handler = new TwoFactorHandler<>(session_service);
					RedirectResult next_page = (RedirectResult) LoginServlet.getSavedResult(session_service);
					LoginServlet.clearSavedResult(session_service);
					
					if( next_page == null) {
						next_page = new RedirectResult(LoginServlet.getMainPage(conn));
					}
					FormResult result = handler.doLogin(person,remote_auth_realm, next_page);
					handleFormResult(conn, req, res, result);
					return;
				}else{
					message(conn, req, res, "remote_auth_binding_only",web_name,person.getIdentifier());
					return;
				}
			} else {
				// binding or registration
				
				// Check for existing binding
				// and replace rather than duplicate
				AppUser existing = parser.findFromString(web_name);
				if( existing != null){
					if( ! existing.equals(person)){
						getLogger(conn).warn("Replacing remote-auth binding "+web_name+" "+existing.getIdentifier()+"->"+person.getIdentifier());
						parser.setName(existing, null);
						existing.commit();
						parser.setName(person, web_name);
					}
				}else{
					parser.setName(person, web_name);
				}
				try {
					person.commit();
					session_service.setAuthenticationType(remote_auth_realm);
					CurrentTimeService time = conn.getService(CurrentTimeService.class);
					if( time != null ) {
						session_service.setAuthenticationTime(time.getCurrentTime());
					}
					SerializableFormResult next = getNextResult(session_service);
					session_service.removeAttribute(REMOTE_AUTH_NEXT_URL);
					if( next == null ) {
						message(conn, req, res, "remote_auth_set",web_name);
						return;
					}else {
						// This is a re-authenticaiton
						parser.verified(person); // record sucessful authentication
						for(RemoteAuthListener l : ((AppUserFactory<?>)person.getFactory()).getComposites(RemoteAuthListener.class)){
							l.authenticated(remote_auth_realm,person);
						}
						person.commit();
						person.historyUpdate();

						handleFormResult(conn, req, res, next);
						return;
					}
				} catch (DataFault e) {
					getLogger(conn).error("error in RemoteAuthServlet",e);
					throw new ServletException(e);
				}

			}
		} catch (Exception e) {
			getLogger(conn).error("general error in RemoteAuthServlet",e);
			if (e instanceof ServletException) {
				throw (ServletException) e;
			}
			if (e instanceof IOException) {
				throw (IOException) e;
			}
		}
	}

	private SerializableFormResult getNextResult(SessionService session_service) {
		return (SerializableFormResult) session_service.getAttribute(REMOTE_AUTH_NEXT_URL);
	}

	/** Set the user that should be registered (and logged in) if the
	 * external authentication succeeds. This is for when a user registers
	 * and id given an opportunity to bind an existing id. If the binding succeeds the
	 * user has a proven id (even though the email is not verified) and can be logged in
	 * if the external auth succeeds.
	 * 
	 * If the session already contains a user this user must match the
	 * supplied arguement.
	 * 
	 * @param conn
	 * @param user
	 * @return true if binding should be offered.
	 */
    public static boolean registerNewUser(AppContext conn, AppUser user){
    	SessionService sess = conn.getService(SessionService.class);
    	if( sess.haveCurrentUser()){
    		return sess.isCurrentPerson(user);
    	}else{
    		sess.setAttribute(EXTAUTH_REGISTER_ID_ATTR, user.getID());
    		return true;
    	}
    }
    
    /** Test if the bind external id should be offered 
     * ie is there an existing session user or a stored newly registered user.
     * 
     * @param conn
     * @return
     */
    public static boolean canRegisterNewUser(AppContext conn){
    	SessionService sess = conn.getService(SessionService.class);
    	return WEB_LOGIN_FEATURE.isEnabled(conn) && 
    			sess != null && 
    			( 
    			sess.haveCurrentUser() || 
    			sess.getAttribute(EXTAUTH_REGISTER_ID_ATTR) != null
    			);
    }
    
    public static boolean canLogin(AppContext conn, String realm) {
    	return conn.getBooleanParameter(REMOTE_AUTH_ALLOW_LOGIN_PREFIX+realm, true);
    }
    private <A extends AppUser> A getTargetAppUser(SessionService<A> sess){
   
    	A person = sess.getCurrentPerson();
    	if( person != null ){
    		return person;
    	}
    	Integer register_id = (Integer) sess.getAttribute(EXTAUTH_REGISTER_ID_ATTR);
    	if( register_id != null){
    		A user = sess.getLoginFactory().find(register_id);
    		// Log the user in if no two factor required
    		// otherwise we will still link the ID but not auto login.
    		TwoFactorHandler<A> handler = new TwoFactorHandler<>(sess);
    		if( ! handler.needAuth(user)) {
    			sess.setCurrentPerson(user);
    		}
			return user;
    	}
    	return null;
    }

    /** set a result to go to after re-authentication/register
     * 
     * @param sess
     * @param next
     */
    public static void setNextResult(SessionService<?> sess, SerializableFormResult next) {
    	sess.setAttribute(REMOTE_AUTH_NEXT_URL, next);
    }
   
	/**
	 * @param token
	 * @return
	 */
	private boolean empty(String token) {
		
		return token == null || token.trim().length() == 0;
	}
	

}