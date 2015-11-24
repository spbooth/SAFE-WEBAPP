// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.html.PageHTMLForm;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory.PasswordChangeRequest;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Servlet to handle web password resets.
 *
 * If a {@link PasswordChangeRequest} matching the servlet-path is found (and is valid)
 * the user is prompted for a new password.
 * If this is a legal password then the password is changed and the user is logged in.
 *
 *
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PasswordChangeRequestServlet.java,v 1.3 2015/11/18 07:44:28 spb Exp $")
@WebServlet(name="PasswordChangeRequestServlet",urlPatterns="/PasswordChangeRequestServlet/*")
public class PasswordChangeRequestServlet<A extends AppUser> extends WebappServlet {

	/**
	 * @author spb
	 *
	 */
	private static final class PasswordValidator implements FormValidator {
		/**
		 * @param conn
		 */
		public PasswordValidator(AppContext conn) {
			super();
			this.conn = conn;
		}
		private final AppContext conn;
		@Override
		public void validate(Form f) throws ValidateException {
			String pass1 = (String) f.get(PASSWORD1);
			String pass2 = (String) f.get(PASSWORD2);
			if( ! pass1.equals(pass2)){
				throw new ValidateException("Passwords don't match");
			}
			int minPasswordLength = UserServlet.minPasswordLength(conn);
			if( pass1.length() < minPasswordLength){
				throw new ValidateException("Password too short, must be at least "+minPasswordLength);
			}
		}
	}

	/**
	 * 
	 */
	private static final String PASSWORD2 = "password2";
	/**
	 * 
	 */
	private static final String PASSWORD1 = "password1";

	public static  PageHTMLForm getForm(AppContext conn){
		PageHTMLForm form = new PageHTMLForm(conn);
		form.addInput(PASSWORD1, "New password", new PasswordInput());
		form.addInput(PASSWORD2, "New password (Again)", new PasswordInput());
		form.addValidator(new PasswordValidator(conn));
		return form;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		try{
		SessionService<A> service = conn.getService(SessionService.class);
		ServletService serv = conn.getService(ServletService.class);
		PasswordChangeRequestFactory<A> fac = new PasswordChangeRequestFactory(service.getLoginFactory());
		Logger log = conn.getService(LoggerService.class).getLogger(getClass());
		
			log.debug("path is "+req.getPathInfo());
			// should be request link
			String tag = req.getPathInfo();
			if( tag.startsWith("/", 0)){
				tag= tag.substring(1);
			}
			int i = tag.indexOf("/");
			if( i > 0){
				tag = tag.substring(0, i);
			}
			PasswordChangeRequestFactory<A>.PasswordChangeRequest request = fac.findByTag(tag);
			if( request != null ){
				PageHTMLForm form = getForm(conn);
				if ( ! form.hasSubmitted(req) || ! form.parsePost(req)){
					req.setAttribute("Form", form);
					serv.forward("/scripts/password_change_request.jsp");
					return;
				}
				String new_password = (String) form.get(PASSWORD1);
				PasswordAuthComposite<A> comp = (PasswordAuthComposite<A>) service.getLoginFactory().getComposite(PasswordAuthComposite.class);
				A user = request.getUser();
				if( comp != null && comp.canResetPassword(user) && user.canLogin()){
					comp.setPassword(user, new_password);
					user.commit();
					service.setCurrentPerson(user);
					request.delete();
					if (comp.doWelcome(user)) {
						log.debug("Doing welcome page");
						// Ok, got a first time visit from a new user - send
						// them to the welcome page
						res.sendRedirect(res.encodeRedirectURL(req
								.getContextPath()
								+ LoginServlet.getWelcomePage(conn)));
						return;
					}else{
						log.debug("redirect to main");
						res.sendRedirect(res.encodeRedirectURL(req
								.getContextPath()
								+ LoginServlet.getMainPage(conn)));
						return;
					}
				}
				
			}
			message(conn, req, res, "password_change_request_denied");
			return;
		}catch(Exception e){
			conn.error(e,"Error in EmailChangeRequest form");
			message(conn,req,res,"invalid_input");
			return;
		}
	}
	
}