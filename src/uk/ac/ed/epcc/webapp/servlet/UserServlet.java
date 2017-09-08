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
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.forms.DataObjectUpdateFormFactory;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.PasswordUpdateFormBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;


/** A servlet to allow a registered user to manage their identity.
 * 
 * @author spb
 *
 */
@WebServlet(name="UserServlet" , urlPatterns="/UserServlet/*")
public class UserServlet<T extends AppUser> extends SessionServlet {

	
	/**
	 * 
	 */
	protected static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
	/**
	 * 
	 */
	protected static final String TOGGLE_PRIV = "TOGGLE_PRIV";
	/**
	 * 
	 */
	protected static final String MODIFY_PERSON = "MODIFY_PERSON";
	
	protected static final String RESET_NAVIGATION_MENU ="RESET_NAVIGATION_MENU";
	public static final Feature USER_SELF_UPDATE_FEATURE = new Feature("user.self.update",true,"users can update their own details");
	public static final Feature USER_CHANGE_PASSWORD_FEATURE = new Feature("user.change.password",true,"users can change their password");
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
		PasswordAuthComposite<T> password_comp = fac.getComposite(PasswordAuthComposite.class);
		if ( action.equals(MODIFY_PERSON)){
			if( ! USER_SELF_UPDATE_FEATURE.isEnabled(conn)){
				message(conn, req, res, "disabled_feature_error");
				return;
			}
			
			doUpdateUser(fac, person,req,res,conn);
			return;
		}else if( password_comp != null && USER_CHANGE_PASSWORD_FEATURE.isEnabled(conn) && action.equals(CHANGE_PASSWORD)){
			doChangePassword(req,res,conn, password_comp,person);
			return;
		}else if (TOGGLE_PRIV.equals(action)) {
			doToggleRole(conn,req, res, params,sess);
			return;
		}else if( RESET_NAVIGATION_MENU.equals(action)){
			resetNavigationMenu(conn, req, res, params, sess);
			return;
		}
		message(conn, req, res, "invalid_input");
		return;
	}
	
	protected void resetNavigationMenu(AppContext conn,HttpServletRequest req,
			HttpServletResponse res, Map<String,Object> params, SessionService sess) throws IOException {
		NavigationMenuService nav = conn.getService(NavigationMenuService.class);
		if( nav != null ){
			// rebuild menu on role toggle.
			nav.resetMenu();
		}
		
		returnCaller(req, res, params);
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
		sess.toggleRole(role);
		
		NavigationMenuService nav = conn.getService(NavigationMenuService.class);
		if( nav != null ){
			// rebuild menu on role toggle.
			nav.resetMenu();
		}
		
		returnCaller(req, res, params);
		return; // Should have handled error display already
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @throws IOException
	 */
	protected void returnCaller(HttpServletRequest req, HttpServletResponse res, Map<String, Object> params)
			throws IOException {
		String page=(String) params.get("page");
		if( empty(page)){
			page="/main.jsp";
		}
		res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
				+ page));
	}
	public boolean empty(String arg){
		return arg == null || arg.trim().length() == 0 ;
	}
	protected <T extends AppUser> boolean doUpdateUser(AppUserFactory<T> fac, T person, HttpServletRequest req, HttpServletResponse res,
            AppContext conn) throws Exception {
			
	        HTMLForm f = new HTMLForm(conn);
	        fac.buildUpdateForm(f, person);
	        boolean ok  = f.parsePost(req);
	        if( !ok){
	            HTMLForm.doFormError(conn,req,res);
	            return false;
	        }
	        
	        // ignore def result
	        f.doAction(DataObjectUpdateFormFactory.UPDATE);
	        person.markDetailsUpdated();
	        person.commit();
	       
	        res.sendRedirect(res.encodeRedirectURL(req.getContextPath()
	        		+ conn.getInitParameter("person-update.redirect","/personal_update.jsp")));
			// Everything went ok.
			return true;
		}
        
       
 /**
  *  doChangePassword Method. Allows users to change their SAF passwords
	 * 
	 * @param req
	 *            Request instance received from person_update.jsp.
	 * @param res
	 *            Response instance to be returned to person_update.jsp.
	 * @param conn
	 *            Connection to MySQL database for update queries.
	 * @param person
	 *            Current Person logged into the Web service.
	 * @return True if request processed without error, false if not.
	 * @throws ServletException 
	 * @throws DataException 
	 * @throws IOException 
	 */

	protected  boolean doChangePassword(HttpServletRequest req, HttpServletResponse res,
			AppContext conn, PasswordAuthComposite<T> composite, T person) throws ServletException,
	DataException, IOException {

		HTMLForm f = new HTMLForm(conn);
		PasswordUpdateFormBuilder fac = new PasswordUpdateFormBuilder(composite, true);
		fac.buildForm(f,person,conn);

		boolean ok = f.parsePost(req);

		if( !ok){
			HTMLForm.doFormError(conn,req,res);
			return false;
		}
		Map<String,Object> params = conn.getService(ServletService.class).getParams();
		try {
			FormResult result =  f.doAction(params);
			handleFormResult(conn, req, res, result);
		} catch (Exception e) {
			getLogger(conn).error("Error processing form", e);
			message(conn, req, res, "internal_error");
		}
		return true;
	}


}