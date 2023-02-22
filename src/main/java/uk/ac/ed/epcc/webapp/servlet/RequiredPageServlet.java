package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.session.*;
/** A servlet that implements the required page logic before
 * navigating to a saved destination.
 * Normally this is not necessary as the saved location will implement required pages
 * but this can be useful for login flows that navigate to locations that don't. for example a chained
 * remote login that will return to a servlet. Optionally this also handles a manadatory
 * redirect on login used to prevent session-ids being shown in URLs when a session is first created
 * 
 * @author Stephen Booth
 *
 */

@WebServlet(name="RequiredPageServlet",urlPatterns = {RequiredPageServlet.URL})
public class RequiredPageServlet extends SessionServlet {

	public static final String URL="/RequiredPages";
	
	public static final String RP_ATTR="RequiredPageServlet_next";
	public static final Feature COOKIE_TEST = new Feature("login.cookie_test_redirect",true,"Use double redirect on login to check for cookie support and avoid url rewriting");

	public RequiredPageServlet() {
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn, SessionService person)
			throws Exception {
		AppUserFactory<?> login = person.getLoginFactory();
		for(RequiredPage p : login.getRequiredPages() ){
			if( p.required(person) ){
				FormResult form_result = p.getPage(person);
				// we are displaying a required page.
				if( form_result != null) {
					if( person.getAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR)==null) {
						// only set this once as the required page processing may result in
						// page re-displays from a different location
						person.setAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR, URL);
					}
					req.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
					req.setAttribute(RequiredPage.AM_REQUIRED_PAGE_ATTR,Boolean.TRUE);
					handleFormResult(conn, req, res, form_result);
					return;
				}else {
					getLogger(conn).error("Null FormResult from required page");
				}
	        		
			}
		}
		// all checks passed cache for session
		// remove the cached destination url if set as we mutate the result url
		// for some operations if this exists to ensure that required pages can chain properly
		person.removeAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR);
		req.getSession().setAttribute(RequiredPage.REQUIRED_PAGES_ATTR,"done");
		
		SerializableFormResult next_page = (SerializableFormResult) person.getAttribute(RP_ATTR);
		person.removeAttribute(RP_ATTR);
		
		if( next_page == null) {
			next_page = new RedirectResult(LoginServlet.getMainPage(conn));
		}
		handleFormResult(conn, req, res, next_page);

	}

	
	public static <A extends AppUser> SerializableFormResult getNext(SessionService<A> person, SerializableFormResult next) {
		boolean use=COOKIE_TEST.isEnabled(person.getContext());
		if(! use ) {
			// still use if a required page needed
			AppUserFactory<A> fac = person.getLoginFactory();
			for(RequiredPage<A> page : fac.getRequiredPages()) {
				if( page.required(person)) {
					use=true;
					break;
				}
			}
		}
		if( use ) {
			if( next != null) {
				person.setAttribute(RP_ATTR, next);
			}
			return new RedirectResult(URL);
		}
		return next;
	}
}
