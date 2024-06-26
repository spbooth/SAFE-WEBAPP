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
/*
 * Created on 20-May-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.*;
import uk.ac.ed.epcc.webapp.session.twofactor.TwoFactorHandler;

// import uk.ac.hpcx.HpcxMain;

/**
 * LoginServlet
 * 
 * Handles password based logins.
 * 
 * @author spb
 * @param <T> 
 * 
 */

@WebServlet(name="LoginServlet", urlPatterns="/LoginServlet/*")
public class LoginServlet<T extends AppUser> extends WebappServlet {
	
	/** config parameter for the logout url.
	 * 
	 */
	private static final String LOGOUT_URL_PARAM = "logout.url";
	/** config parameter for the main page url
	 * 
	 */
	private static final String MAIN_PAGE_PARAM = "main.page";
	/** config parameter for the login page url
	 * 
	 */
	private static final String LOGIN_PAGE_PARAM = "login.page";
	
	
	private static final String INITIAL_PAGE_ATTR = "initial_page";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static final Feature REPORT_ACCOUNT_NOT_FOUND = new Feature("login.report_account_not_found",true,"Users are explicitly informed if resetting an account that is not found");
	public static final Feature RESET_PASSWORD_PAGE = new Feature("login.reset_password_page",false,"Use a separate reset password page");
	public static final Feature BUILT_IN_LOGIN = new Feature("login.built_in",true,"Use built-in login page and servlet. If false login.page parameter may be external url");
	/**
	 * 
	 */
	public LoginServlet() {
		super();
	}

	private void doLogout(AppContext conn, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		SessionService<T> serv = conn.getService(SessionService.class);
		String realm = serv.getAuthenticationType();
		serv.logOut();
		if( serv.haveCurrentUser()){
			// must be a SU operation go to main page
			res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + getMainPage(conn)));
			return;
		}
		String url = conn.getInitParameter(LOGOUT_URL_PARAM, req.getContextPath() + getLoginPage(conn));
		
		if( realm != null && ! realm.isEmpty()) {
			url = conn.getInitParameter(LOGOUT_URL_PARAM+"."+realm,url);
		}
		res.sendRedirect(res.encodeRedirectURL(url));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException,
			java.io.IOException {
		if( ! BUILT_IN_LOGIN.isEnabled(conn)) {
			message(conn, req, res, "disabled_feature_error");
			return;
		}
		String logout = req.getParameter("logout");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String authtype = req.getParameter("authtype");
		LoggerService ls = conn.getService(LoggerService.class);
		Logger log = getLogger(conn);
		SessionService<T> serv = conn.getService(SessionService.class);
		if( serv == null ){
			serv = new ServletSessionService(conn);
			conn.setService(serv);
		}
		AppUserFactory<T> person_fac = serv.getLoginFactory();
		try {
			if (logout != null) {
				log.debug("requesting logout");
				doLogout(conn, req, res);
				return;
			}
			SessionService sess = conn.getService(SessionService.class);
			if( sess != null && sess.haveCurrentUser()) {
				// look for a cookie test or remembered page
				SerializableFormResult next_page = (SerializableFormResult) sess.getAttribute(INITIAL_PAGE_ATTR);
				if( next_page != null ) {
					sess.removeAttribute(INITIAL_PAGE_ATTR);
				}else {
					next_page = new RedirectResult(getMainPage(conn));
				}
				// already logged in
				handleFormResult(conn, req, res, next_page);
				return;
			}
			if( DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn)){
				message(conn,req,res,"disabled_feature_error");
				return;
			}
			if( authtype != null ) {
				// we are doing a double redirect through the login server 
				// to establish a container session. This is so we can
				// avoid making a session when showing the login page (cookie consent)
				// as well as avoid showing a page url containing the session id
				String bits[] = authtype.split(":");
				if( bits.length == 2) {
					LoginRedirects red = conn.makeObject(LoginRedirects.class, bits[0]);
					if( red != null) {
						FormResult fr = red.getRedirect(bits[1]);
						if( fr != null) {
							// want to make a session 
							// cookie should get set on the redirect and
							// (unless cookies are disabled) by the time
							// the logged in page is shown (redirect from the external auth server) 
							// there will not be a session encoded in the url
							req.getSession(true);
							handleFormResult(conn, req, res, fr);
							return;
						}
					}
				}
				message(conn,req,res,"invalid_argument");
				return;
			}
			
			PasswordAuthComposite<T> password_auth = person_fac.getComposite(PasswordAuthComposite.class);

			if(  password_auth == null) {
				message(conn,req,res,"disabled_feature_error");
				return;
			}
			
			// User wants a new password sent to them

			if (req.getParameter("email_password") != null ){
				if( username == null || username.trim().length() == 0) {
					message(conn, req, res, "no_email_specified");
					return;
				}
				username=username.trim();
				T user = person_fac
						.findFromString(username);
				// User can supply any of their valid ids not just email.
				if (user != null ) {
					try{
						log.info("new password requested for " + username);
						 // This corresponds to the registered username test above
						if( ! user.canLogin() ){
							if( user.canReregister()){
								// Need to reset the account.
								// The reregister settingonly comes into force if the user is denied login
								// This also bypasses the canResetPassword check. 
								user.reRegister();
								for(ReRegisterComposite<T> c : person_fac.getComposites(ReRegisterComposite.class)){
									c.reRegister(user);
								}
								user.commit();
								assert(password_auth.canResetPassword(user) && user.canLogin()); //Would be odd id the user can't reset password after reregister 
							}else{
								message(conn,req,res,"login_disabled");
								return;
							}
						}else if( ! password_auth.canResetPassword(user)){
							message(conn, req, res, "new_password_failed");
							return;
						}
						Map attr = new HashMap();
						attr.put("user",user.getIdentifier());
						ls.securityEvent("new_password_requested", sess,attr);
						password_auth.newPassword(user);
					}catch(Exception t){
						getLogger(conn).error("Error getting registered user or sending new password",t);
						message(conn,req,res,"internal_error");
					}
				} else {
					if(REPORT_ACCOUNT_NOT_FOUND.isEnabled(conn)){
						message(conn,req,res,"account_not_found",username);
						return;
					}
					// Non matching ids give an error if not an error
					if( ! Emailer.checkAddress(username)){
						message(conn, req, res, "new_password_failed");
						return;
					}
					log.warn(" new password requested for invalid account "
							+ username);
				}
				//Same text for success or fail
				message(conn, req, res, "new_password_emailed", username , Emailer.PASSWORD_RESET_SERVLET.isEnabled(conn)? "password reset link" :"new password");
				return;
			}
			log.info("login requested for " + username);

			// Usual login details check:
			if ((username != null) && (password != null)) {

				// Attempt to look up this username and password combination:
				// Catches the DNFE and reset person to null.
				T person = null;
				try {
					person = password_auth.findByLoginNamePassword(username, password);
				}catch(DataNotFoundException e1){
					log.warn("password check user not found for " + username);
					person = null;
				} catch (DataException e) {
					log.error("password check failed for " + username,e);
					person = null;
				}
				
				if (person != null) {
					// Go to the logged in page
					// (we may have another page that should be accessed)
					FormResult other_page = getSavedResult(sess);
					clearSavedResult(sess);
					
					FormResult next_page=null;
					if (password_auth.doWelcome(person)) {
						next_page = new RedirectResult(getWelcomePage(conn));
					}else if( other_page != null) {
						// if we already have a remembered page use it
						next_page = other_page;
					}else {
						next_page = new RedirectResult(getMainPage(conn));
					}
					TwoFactorHandler<T> handler = new TwoFactorHandler<>(serv);
					next_page =  handler.doLogin(person, "password",(SerializableFormResult) next_page);
					handleFormResult(conn, req, res, next_page);
					return;
				}
			}
		} catch (Exception e) {
			log.error("Exception in LoginServlet", e);
			throw new ServletException(e);
		}

		
		Map attr = new HashMap();
		attr.put("user", username);
		ls.securityEvent("LoginFailed",serv, attr);
		
		
		// Go to the login page again - perhaps with polite message
		// Don't use the ServletService method as the explicit login page
		// may not be the default implementation. (may try Basic Auth for example)
		if( username == null ) {
			res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
					+ getLoginPage(conn)+"?error=login"));
		}else {
			try {
				// need to be a little careful here as usernames are 
				person_fac.validateNameFormat(username);
				res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
						+ getLoginPage(conn)+"?error=login&username="+username));
			}catch(ParseException e) {
				res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
						+ getLoginPage(conn)+"?error=login_name"));
			}
		}
	}

	
	public static void setSavedResult(SessionService sess, SerializableFormResult result) {
		sess.setAttribute(INITIAL_PAGE_ATTR, result);
	}
	public static void clearSavedResult(SessionService sess) {
		sess.removeAttribute(INITIAL_PAGE_ATTR);
	}
	public static SerializableFormResult getSavedResult(SessionService sess) {
		return (SerializableFormResult) sess.getAttribute(INITIAL_PAGE_ATTR);
	}
	public static String getWelcomePage(AppContext conn) {
		return conn.getInitParameter("welcome.page","/welcome.jsp");
	}

	public static String getMainPage(AppContext conn) {
		return conn.getInitParameter(MAIN_PAGE_PARAM,"/main.jsp");
	}

	public static  String getLoginPage(AppContext conn) {
		return conn.getInitParameter(LOGIN_PAGE_PARAM,"/login.jsp");
	}

}