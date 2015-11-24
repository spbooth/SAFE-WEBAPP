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
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.html.HTMLCreationForm;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * Register first time visitors
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RegisterServlet.java,v 1.29 2015/10/26 10:07:02 spb Exp $")
@WebServlet(name="RegisterServlet", urlPatterns={"/SignupServlet/*","/RegisterServlet/*"})
public class RegisterServlet extends WebappServlet {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		
		SessionService serv = conn.getService(SessionService.class);
		if( serv == null ){
			serv = new ServletSessionService(conn);
			conn.setService(serv);
		}
		AppUserFactory fac =  serv.getLoginFactory();
		if( fac == null || ! AppUserFactory.ALLOW_SIGNUPS.isEnabled(conn)){
			message(conn,req,res,"disabled_feature");
			return;
		}
		FormCreator signupFormCreator =fac.getSignupFormCreator(conn.getService(ServletService.class).getWebName());
		
		if( signupFormCreator != null ){
			HTMLCreationForm person_form = new HTMLCreationForm("Signup",signupFormCreator);

			FormResult result = null;
			try {
				result = (FormResult) person_form.parseCreationForm(req);
				if (result == null) {
					HTMLForm.doFormError(conn, req, res);
					return;
				}
				handleFormResult(conn, req, res, result);
				return;
			} catch (Exception e) {
				conn.error(e, "Error registering new user ");
			}
		}
		message(conn, req, res, "invalid_input");
	}

}