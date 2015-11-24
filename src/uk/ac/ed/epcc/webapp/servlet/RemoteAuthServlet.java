// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

/**
 * This servlet is to support authentication from the container. This servlet
 * should be configured with server level authentication and the user redirects
 * through this servlet to either login based on the remote username or to
 * change their remote username
 *<p>
 *The property <b>remote_auth.realm</b> (which may be set as a servlet parameter) defines the name realm used by the servlet.
 *Multiple types of external authentication can be supported by including the servlet at multiple paths, one for each realm.
 *
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RemoteAuthServlet.java,v 1.26 2015/10/26 10:07:01 spb Exp $")

public class RemoteAuthServlet extends WebappServlet {

	/**
	 * 
	 */
	public static final String REMOTE_AUTH_REALM_PROP = "remote_auth.realm";


	/**
	 * 
	 */
	public static final String SERVICE_WEB_LOGIN_UPDATE_TEXT = "service.web_login.update-text";


	/**
	 * 
	 */
	public static final String REGISTER_IDENTITY_DEFAULT_TEXT = "Register identity";


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res,
			AppContext conn) throws ServletException, IOException {
		String web_name = conn.getService(ServletService.class).getWebName();
		try {
			if (web_name == null || web_name.length() == 0) {
				conn.error("missing web_name");
				// we must have a remote user name
				message(conn, req, res, "invalid_input");
				return;
			}
			AppUser person = null;

			SessionService session_service = conn.getService(SessionService.class);
			person = session_service.getCurrentPerson();
			String remote_auth_realm = conn.getInitParameter(REMOTE_AUTH_REALM_PROP, WebNameFinder.WEB_NAME);
			AppUserFactory<?> fac = session_service.getLoginFactory();
			AppUserNameFinder parser = fac.getRealmFinder(remote_auth_realm);
			if (person == null) {
				// attempt a login
				
				
					person = parser.findFromString(web_name);
					if (person == null) {
							String register_text = session_service.getContext().getInitParameter(SERVICE_WEB_LOGIN_UPDATE_TEXT,REGISTER_IDENTITY_DEFAULT_TEXT);
							message(conn, req, res, "unknown_web_login", register_text);
							return;
					}
					session_service.setCurrentPerson(person);
				
			} else {
				parser.setName(person, web_name);
				try {
					person.commit();
				} catch (DataFault e) {
					getLogger(conn).error("error in RemoteAuthServlet",e);
					throw new ServletException(e);
				}

			}
			// use re-direct rather than forward as this is a login
			String redirect_url = res.encodeRedirectURL(req.getContextPath()
					+ LoginServlet.getMainPage(conn));
			res.sendRedirect(redirect_url);
		} catch (Exception e) {
			conn.error(e, "general error in RemoteAuthServlet");
			if (e instanceof ServletException) {
				throw (ServletException) e;
			}
			if (e instanceof IOException) {
				throw (IOException) e;
			}
		}
	}
	

}