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

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.html.HTMLCreationForm;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

/**
 * Register first time visitors
 * 
 * @author spb
 * 
 */

@WebServlet(name="RegisterServlet", urlPatterns={"/SignupServlet/*","/RegisterServlet/*"})
@MultipartConfig
public class RegisterServlet extends WebappServlet {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Feature ALLOW_SIGNUPS = new Feature("allow_signup",true,"Allow new users to sign up to the service via a signup form");

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		
		SessionService serv = conn.getService(SessionService.class);
		if( serv == null ){
			serv = new ServletSessionService(conn);
			conn.setService(serv);
		}
		if( serv.haveCurrentUser()){
			// this servlet for should not be visited with a populated session
			message(conn,req,res,"access_denied");
			return;
		}
		String webName = conn.getService(ServletService.class).getWebName(req);
		if( webName == null && DefaultServletService.EXTERNAL_AUTH_ONLY_FEATURE.isEnabled(conn) ){
			message(conn,req,res,"access_denied");
			return;
		}
		AppUserFactory fac =  serv.getLoginFactory();
		if( fac == null || ! RegisterServlet.ALLOW_SIGNUPS.isEnabled(conn)){
			message(conn,req,res,"disabled_feature_error");
			return;
		}
		HTMLForm.setFormUrl(req, "/signup.jsp");
		
		String realm=getRealm(conn);
		FormCreator signupFormCreator =fac.getSignupFormCreator();
		
		if( signupFormCreator != null ){
			HTMLCreationForm person_form = new HTMLCreationForm(signupFormCreator);

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
				getLogger(conn).error( "Error registering new user ",e);
			}
		}
		message(conn, req, res, "invalid_input");
	}

	/**
	 * @param conn
	 * @return
	 */
	public static String getRealm(AppContext conn) {
		return conn.getInitParameter(RemoteAuthServlet.REMOTE_AUTH_REALM_PROP, WebNameFinder.WEB_NAME);
	}
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return;
	}

}