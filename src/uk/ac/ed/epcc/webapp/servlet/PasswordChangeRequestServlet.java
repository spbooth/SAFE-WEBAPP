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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.html.PageHTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.EmailNameFinder;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory.PasswordChangeRequest;
import uk.ac.ed.epcc.webapp.session.PasswordUpdateFormBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Servlet to handle web password resets.
 *
 * If a {@link PasswordChangeRequest} matching the servlet-path is found (and is valid)
 * the user is prompted for a new password.
 * If this is a legal password then the password is changed and the user is logged in.
 *
 *
 * @author spb
 * @param <A> type of AppUser
 *
 */

@WebServlet(name="PasswordChangeRequestServlet",urlPatterns="/PasswordChangeRequestServlet/*")
public class PasswordChangeRequestServlet<A extends AppUser> extends WebappServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		try{
		@SuppressWarnings("unchecked")
		SessionService<A> service = conn.getService(SessionService.class);
		ServletService serv = conn.getService(ServletService.class);
		PasswordChangeRequestFactory<A> fac = new PasswordChangeRequestFactory<>(service.getLoginFactory());
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
				@SuppressWarnings("unchecked")
				PasswordAuthComposite<A> comp = (PasswordAuthComposite<A>) service.getLoginFactory().getComposite(PasswordAuthComposite.class);
				A user = request.getUser();
				PasswordUpdateFormBuilder<A> builder = new PasswordUpdateFormBuilder<>(comp, false);
				PageHTMLForm form = new PageHTMLForm(conn);
				builder.buildForm(form, user, conn);
				
				if ( ! form.hasSubmitted(req) || ! form.parsePost(req)){
					req.setAttribute("Form", form);
					req.setAttribute("policy", builder.getPasswordPolicy());
					serv.forward("/scripts/password_change_request.jsp");
					return;
				}
				
				
				Map<String,Object> params = conn.getService(ServletService.class).getParams();
				try {
					FormResult result =  form.doAction(params); // this sets the password and logs-in
					request.delete();
					AppUserNameFinder finder = user.getFactory().getRealmFinder(EmailNameFinder.EMAIL);
					if( finder != null ) {
						finder.verified(user); // email address verified by reset link
					}
					handleFormResult(conn, req, res, result);
				} catch (Exception e) {
					getLogger(conn).error("Error processing form", e);
					message(conn, req, res, "internal_error");
				}
				return;
				
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