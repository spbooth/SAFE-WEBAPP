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
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.servlet.navigation.PageNode;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.twofactor.TwoFactorHandler;


/** A servlet to allow a registered user to manage their identity.
 * 
 * @author spb
 *
 */
@WebServlet(name="UserServlet" , urlPatterns="/UserServlet/*")
public class UserServlet<T extends AppUser> extends SessionServlet {

	public static final Feature REQUIRE_MFA = new Feature("toggle_role.require_mfa", false, "Require MFA authentication to enable toggle role");
	
	
	/**
	 * 
	 */
	protected static final String TOGGLE_PRIV = "TOGGLE_PRIV";
	
	
	protected static final String RESET_NAVIGATION_MENU ="RESET_NAVIGATION_MENU";
	
	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn, SessionService sess) throws Exception {
		ServletService servlet_service = conn.getService(ServletService.class);
		
		Map<String,Object> params = servlet_service.getParams();
		String action = (String) params.get("action");
		
		if( action == null ){
			// Sometimes happens when session times out.
			res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
					+ "/main.jsp"));
			return;
		}
		AppUserFactory<T> fac =  conn.getService(SessionService.class).getLoginFactory();
		T person = (T) sess.getCurrentPerson();
		if (TOGGLE_PRIV.equals(action)) {
			doToggleRole(conn,req, res, params,sess);
			return;
		}else if( RESET_NAVIGATION_MENU.equals(action)){
			resetNavigationMenu(conn, req, res, params, sess);
			return;
		}
		message(conn, req, res, "invalid_input");
		badInputCheck(conn);
		return;
	}
	
	protected void resetNavigationMenu(AppContext conn,HttpServletRequest req,
			HttpServletResponse res, Map<String,Object> params, SessionService sess) throws IOException {
		NavigationMenuService nav = conn.getService(NavigationMenuService.class);
		if( nav != null ){
			// rebuild menu on role toggle.
			nav.resetMenu();
		}
		
		returnCaller(conn,req, res, params);
		return; // Should have handled error display already
	}
	/**
	 * @param req
	 * @param res
	 * @param sess
	 * @throws IOException
	 * @throws ServletException 
	 */
	protected void doToggleRole(AppContext conn,HttpServletRequest req,
			HttpServletResponse res, Map<String,Object> params, SessionService sess) throws IOException, ServletException {
		String role = (String) params.get("role");
		if( sess == null || empty(role) ){
			conn.getService(ServletService.class).requestAuthentication(sess);
			return;
		}
		if( REQUIRE_MFA.isEnabled(sess.getContext())) {
			if( (! sess.hasRole(role)) && (! TwoFactorHandler.usedTwoFactor(sess))){
				message(sess.getContext(), req, res, "mfa_required", role);
				return;
			}
		}
		sess.toggleRole(role);
		
		NavigationMenuService nav = conn.getService(NavigationMenuService.class);
		if( nav != null ){
			// rebuild menu on role toggle.
			nav.resetMenu();
		}
		
		returnCaller(conn,req, res, params);
		return; // Should have handled error display already
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @throws IOException
	 */
	protected void returnCaller(AppContext conn,HttpServletRequest req, HttpServletResponse res, Map<String, Object> params)
			throws IOException {
		ServletService ss = conn.getService(ServletService.class);
		String page=ss.getFilePath();
		if( empty(page)){
			page="/main.jsp";
		}
		res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
				+ page));
	}
	public boolean empty(String arg){
		return arg == null || arg.trim().length() == 0 ;
	}
	
}