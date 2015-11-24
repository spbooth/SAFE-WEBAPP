// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.servlet;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;

/** A {@link SessionServlet} that will look for user/password parameters in the url/post parameters to
 * perform login if the session is un-authenticated
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: APISessionServlet.java,v 1.6 2015/10/26 10:07:02 spb Exp $")
public abstract class APISessionServlet extends SessionServlet {

	public static Feature CHECK_API_FAILS= new Feature("check_api_fails",true,"Check password fails on API interfaces");
	@Override
	public ServletSessionService getSessionService(AppContext conn){

		ServletSessionService sess = super.getSessionService(conn);

		if( sess != null &&  ! sess.haveCurrentUser()){
			Map<String,Object> params = conn.getService(ServletService.class).getParams();
			AppUserFactory fac = sess.getLoginFactory();
			PasswordAuthComposite<?> composite = (PasswordAuthComposite<?>) fac.getComposite(PasswordAuthComposite.class);
			if( composite != null ){
				String user = null;
				Object person = params.get("person");
				if( person != null ){
					user=person.toString();
				}
				String password = null;
				Object pass_obj = params.get("password");
				if(pass_obj != null ){
					password = pass_obj.toString();
				}
				if( user != null && password != null ){
					try {
						
						
							AppUser p =composite.findByLoginNamePassword(user, password,CHECK_API_FAILS.isEnabled(conn));
							if( p != null && p.canLogin()){
								sess.setCurrentPerson(p);
							}
					} catch (DataException e) {
						conn.getService(LoggerService.class).getLogger(getClass()).error("Error locating person", e);
					}
				}
			}
		}
		return sess;
	}

	

	
	
	
}
