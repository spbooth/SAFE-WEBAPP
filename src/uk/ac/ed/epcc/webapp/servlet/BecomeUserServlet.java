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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;



public class BecomeUserServlet extends SessionServlet {

	
	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn, SessionService service) throws Exception {
		// Do another security check, just to be sure
		String target_person = req.getParameter("person_id");
		if (target_person!=null && target_person.trim().length() >0) {
			Integer personid = Integer.valueOf(target_person);
			
			doSU(req, res, conn, (ServletSessionService) service, personid);
			return;
		} else {
			message(conn,req,res,"invalid_argument");
			return;
		}
	}
	private <A extends AppUser> void doSU(HttpServletRequest req, HttpServletResponse res,AppContext conn,ServletSessionService<A> service,Integer id) throws IOException, ServletException{
		A person = service.getLoginFactory().find(id);
		if( person != null && service.canSU(person)){
			service.su(person);
			res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
					+ "/main.jsp"));
			return;
		}else{
			message(conn,req,res,"access_denied");
		}
	}

}