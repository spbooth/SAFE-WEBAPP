// Copyright - The University of Edinburgh 2011
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
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory.EmailChangeRequest;
/** Servlet to handle Email Change requests.
 * This handles both the request form and the verify link so it needs to do explicit
 * authorisation checks in the first case.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: EmailChangeRequestServlet.java,v 1.16 2015/03/02 08:55:12 spb Exp $")
@WebServlet(name="EmailChangeRequestServlet",urlPatterns="/EmailChangeRequestServlet/*")
public class EmailChangeRequestServlet extends WebappServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		try{
		SessionService service = conn.getService(SessionService.class);
		EmailChangeRequestFactory fac = new EmailChangeRequestFactory(service.getLoginFactory());
		Logger log = conn.getService(LoggerService.class).getLogger(getClass());
		String action = req.getParameter("Action");
		log.debug("Action is "+action);
		if( action == null ){
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
			EmailChangeRequest request = fac.findByTag(tag);
			if( request != null ){
				String email=request.getEmail();
				request.complete();
				message(conn, req, res, "email_change_request_successful",email);
				return;
			}else{
				message(conn, req, res, "email_change_request_denied");
				return;
			}
		}else{
			if( service == null || ! service.haveCurrentUser()){
				conn.getService(ServletService.class).requestAuthentication(service);
				return;
			}
			AppUser person = service.getCurrentPerson();
			doPost(req, res, conn, fac,person);
		}
		}catch(Exception e){
			conn.error(e,"Error in EmailChangeRequest form");
			message(conn,req,res,"invalid_input");
			return;
		}
	}

	private void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn, EmailChangeRequestFactory fac,AppUser person) throws Exception {
		HTMLForm f = new HTMLForm(conn);
		fac.MakeRequestForm(person, f);
		if( f.parsePost(req)){
			Map<String,Object> params = conn.getService(ServletService.class).getParams();
			FormResult result= f.doAction(params);
			if( result != null ){
				handleFormResult(conn, req, res, result);
				return;
			}
		}
		HTMLForm.doFormError(conn, req, res);
	}
   
}