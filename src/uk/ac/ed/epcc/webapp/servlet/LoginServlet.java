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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.ReRegisterComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;

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
	
	/**
	 * 
	 */
	private static final String LOGOUT_URL_PARAM = "logout.url";
	/**
	 * 
	 */
	private static final String MAIN_PAGE_PARAM = "main.page";
	/**
	 * 
	 */
	private static final String LOGIN_PAGE_PARAM = "login.page";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static final Feature REPORT_ACCOUNT_NOT_FOUND = new Feature("login.report_account_not_found",true,"Users are explicitly informed if resetting an account hat is not found");
	public static final Feature RESET_PASSWORD_PAGE = new Feature("login.reset_password_page",false,"Use a separate reset password page");
	/**
	 * 
	 */
	public LoginServlet() {
		super();
	}

	private void doLogout(AppContext conn, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		SessionService<T> serv = conn.getService(SessionService.class);
		serv.logOut();
		if( serv.haveCurrentUser()){
			// must be a SU operation go to main page
			res.sendRedirect(res.encodeRedirectURL(req.getContextPath() + getMainPage(conn)));
			return;
		}
		res.sendRedirect(res.encodeRedirectURL(conn.getInitParameter(LOGOUT_URL_PARAM, req.getContextPath() + getLoginPage(conn))));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException,
			java.io.IOException {
		String logout = req.getParameter("logout");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		Logger log = getLogger(conn);
		SessionService<T> serv = conn.getService(SessionService.class);
		if( serv == null ){
			serv = new ServletSessionService(conn);
			conn.setService(serv);
		}
		try {
			if (logout != null) {
				log.debug("requesting logout");
				doLogout(conn, req, res);
				return;
			}
			if( DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn)){
				message(conn,req,res,"disabled_feature_error");
				return;
			}
			AppUserFactory<T> person_fac = serv.getLoginFactory();
			PasswordAuthComposite<T> password_auth = person_fac.getComposite(PasswordAuthComposite.class);
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
						
						password_auth.newPassword(user);
					}catch(Throwable t){
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
				req.setAttribute("page_name", "the Login Page");
				req.setAttribute("page_url", getLoginPage(conn)+"?username="
						+ encodeCGI(username));
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
					log.debug("Go for conn.login");
					serv.setCurrentPerson(person);
                    log.debug("done login");
					if (password_auth.doWelcome(person)) {
						log.debug("Doing welcome page");
						// Ok, got a first time visit from a new user - send
						// them to the welcome page
						res.sendRedirect(res.encodeRedirectURL(req
								.getContextPath()
								+ getWelcomePage(conn)));
						return;
					}

					// Go to the logged in page
					// (we may have another page that should be accessed)
					String other_page = req.getParameter("page");
					if (other_page != null) {
						// res.setContentType("text/html");
						// PrintWriter out = res.getWriter();
						// out.println("page = " + req.getServletPath());
						String dest = res.encodeRedirectURL(req
								.getContextPath()
								+ other_page);
						log.info("LoginServlet redirect to " + dest);

						res.sendRedirect(dest);
					} else {
						log.debug("redirect to main");
						res.sendRedirect(res.encodeRedirectURL(req
								.getContextPath()
								+ getMainPage(conn)));
					}
					return;
				}
			}
		} catch (Exception e) {
			if( conn != null ){
				conn.error(e,"Error in LoginServlet");
			}
			throw new ServletException(e);
		}

		log.info("login failed for " + username);
		// Go to the login page again - perhaps with polite message
		// Don't use the ServletService method as the explicit login page
		// may not be the default implementation. (may try Basic Auth for example)
		res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
				+ getLoginPage(conn)+"?error=login"));
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