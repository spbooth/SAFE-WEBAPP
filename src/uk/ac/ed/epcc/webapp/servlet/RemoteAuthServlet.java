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
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.Hash;
import uk.ac.ed.epcc.webapp.session.RandomService;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

/**
 * This servlet is to support authentication from the container. This servlet
 * should be configured with server level authentication and the user redirects
 * through this servlet to either login based on the remote username or to
 * change their remote username
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
 * the <i>user-name</i>, <i>the-token</i> and a <i>secret-value</i> shared between the two servers.  
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
	private static final String REMOTE_AUTH_TOKEN_ATTR = "remote_auth_token";


	/** Property name to use for the authentication realm. Note that this is also the
	 * property used by {@link DefaultServletService} and {@link RegisterServlet} if global external authentication is supported.
	 * 
	 */
	public static final String REMOTE_AUTH_REALM_PROP = "remote_auth.realm";


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
		
		if( ! WEB_LOGIN_FEATURE.isEnabled(conn)){
			message(conn,req,res,"disabled_feature");
			return;
		}
		SessionService session_service = conn.getService(SessionService.class);
		ServletService servlet_service = conn.getService(ServletService.class);
		String web_name = servlet_service.getWebName();
		try {
			
			if (empty(web_name)) {
				// Might have bounce-server authentication on this server
				String auth_url = conn.getInitParameter("remote_auth_server.url");
				
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
						// redirect to remote waht about non cookie sessions.
						String redirect_url =auth_url+"?token="+token;
						// Use method on Response as this is not an internal url.
						res.sendRedirect(res.encodeRedirectURL(redirect_url));
						return;
					}
					
					// Must be a response from remote server
					// We expect a hash containing the name (stops a malicious user changing the name) 
					// the session token (stops re-use of responses) and a shared secret (proves the response 
					// is actually from the authentication server
					Hash h = conn.getEnumParameter(Hash.class, "remote_auth_server.hash", Hash.SHA512);
					String auth_secret = conn.getInitParameter("remote_auth_server.secret");
					if( check_token.equals(h.getHash(remote_name+token+auth_secret))){
						// all as expected set the web_name
						web_name=remote_name;
						session_service.removeAttribute(REMOTE_AUTH_TOKEN_ATTR);
					}else{
						message(conn, req, res, "invalid_input");
						return;
					}
				}
				if( empty(web_name)){
					getLogger(conn).error("missing web_name");
					// we must have a remote user name
					message(conn, req, res, "invalid_input");
					return;
				}
			}
			AppUser person = null;

			
			person = session_service.getCurrentPerson();
			String remote_auth_realm = conn.getInitParameter(REMOTE_AUTH_REALM_PROP, WebNameFinder.WEB_NAME);
			AppUserFactory<?> fac = session_service.getLoginFactory();
			AppUserNameFinder parser = fac.getRealmFinder(remote_auth_realm);
			if (person == null) {
				// attempt a login
				
				
					person = parser.findFromString(web_name);
					if (person == null) {
							String register_text = session_service.getContext().getInitParameter(SERVICE_WEB_LOGIN_UPDATE_TEXT,REGISTER_IDENTITY_DEFAULT_TEXT);
							message(conn, req, res, "unknown_web_login", register_text);
							return;
					}
					session_service.setCurrentPerson(person);
				
			} else {
				parser.setName(person, web_name);
				try {
					person.commit();
				} catch (DataFault e) {
					getLogger(conn).error("error in RemoteAuthServlet",e);
					throw new ServletException(e);
				}

			}
			// use re-direct rather than forward as this is a login
			String redirect_url = res.encodeRedirectURL(req.getContextPath()
					+ LoginServlet.getMainPage(conn));
			res.sendRedirect(redirect_url);
		} catch (Exception e) {
			conn.error(e, "general error in RemoteAuthServlet");
			if (e instanceof ServletException) {
				throw (ServletException) e;
			}
			if (e instanceof IOException) {
				throw (IOException) e;
			}
		}
	}



	/**
	 * @param token
	 * @return
	 */
	private boolean empty(String token) {
		
		return token == null || token.trim().length() == 0;
	}
	

}