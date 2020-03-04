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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory.EmailChangeRequest;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Servlet to handle Email Change requests.
 * This handles both the request form and the verify link so it needs to do explicit
 * authorisation checks in the first case.
 * @author spb
 *
 */

@WebServlet(name="EmailChangeRequestServlet",urlPatterns="/EmailChangeRequestServlet/*")
public class EmailChangeRequestServlet extends SessionServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn,SessionService service) throws ServletException, IOException {
		try{
			EmailChangeRequestFactory<?> fac = new EmailChangeRequestFactory(service.getLoginFactory());
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
			try {
				log.debug("path is "+req.getPathInfo());
				// should be request link
				String tag = req.getPathInfo();
				if( tag == null || tag.isEmpty()) {
					message(conn, req, res, "invalid_input");
					return;
				}
				if( tag.startsWith("/", 0)){
					tag= tag.substring(1);
				}
				int i = tag.indexOf("/");
				if( i > 0){
					tag = tag.substring(0, i);
				}
				EmailChangeRequest request = fac.findByTag(tag);
				if( request != null ){
					if( request.expired()) {
						message(conn,req,res,"request_expired");
						request.delete();
						return;
					}
					AppUser target_user = request.getUser();
					if( service.isCurrentPerson(target_user)) { // could make this a relationship
						String old = target_user.getEmail();
						String email=request.getEmail();
						request.complete();
						if( old.equalsIgnoreCase(email)) {
							message(conn, req, res, "email_verification_request_successful",email);
						}else {
							message(conn, req, res, "email_change_request_successful",email);
						}
						return;
					}else {
						getLogger(conn).warn("Email change request for "+target_user.getName()+" complete attempted by "+service.getName());
					}
				}
				message(conn, req, res, "email_change_request_denied");
				return;
			}finally {
				fac.purge();
			}
		}catch(Exception e){
			getLogger(conn).error("Error in EmailChangeRequest form",e);
			message(conn,req,res,"invalid_input");
			return;
		}
	}



}