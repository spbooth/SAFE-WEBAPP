// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
@uk.ac.ed.epcc.webapp.Version("$Id: BecomeUserServlet.java,v 1.8 2014/09/15 14:30:34 spb Exp $")


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