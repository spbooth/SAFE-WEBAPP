//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.ServletFormResultVisitor;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.RequiredPage;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A custom {@link Tag} that ensures that a current session exists for the user viewing the page
 * This also checks for requried pages.
 * @author spb
 *
 */

public class SessionTag extends BasicSessionTag {

	
	
	@Override
	public int doEndTag() throws JspException {
		int res = super.doEndTag();
		try {
		if( res == EVAL_PAGE) {
			PageContext page = pageContext;
			HttpServletRequest request = (HttpServletRequest) page.getRequest();
			HttpServletResponse response = (HttpServletResponse) page.getResponse();
			HttpSession session = page.getSession();
			AppContext conn = ErrorFilter.retrieveAppContext(request, response);
			SessionService<?> session_service = conn.getService(SessionService.class);
			if( request.getAttribute(RequiredPage.AM_REQUIRED_PAGE_ATTR) == null){
	    		Object skip=session.getAttribute(RequiredPage.REQUIRED_PAGES_ATTR);
	    		if( session_service != null && session_service.haveCurrentUser() && skip==null && ! ((ServletSessionService) session_service).isSU() ){
	    			AppUserFactory<?> fac = session_service.getLoginFactory();
	    			for(RequiredPage p : fac.getRequiredPages() ){
	    				if( p.required(session_service) ){
	    					ServletFormResultVisitor vis = new ServletFormResultVisitor(conn, request, response);
	    					FormResult form_result = p.getPage(session_service);
	    					// we are displaying a required page.
	    					if( session_service.getAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR)==null) {
	    						// only set this once as the required page processing may result in
	    						// page re-displays from a different location
	    						session_service.setAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR, conn.getService(ServletService.class).encodePage());
	    					}
	    					request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	    					request.setAttribute(RequiredPage.AM_REQUIRED_PAGE_ATTR,Boolean.TRUE);
	    					form_result.accept(vis);
	   		        		return SKIP_PAGE;
	    				}
	    			}
	    			// all checks passed cache for session
	    			// remove the cached destination url if set as we mutate the result url
	    			// for some operations if this exists to ensure that required pages can chain properly
	    			session_service.removeAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR);
	    			session.setAttribute(RequiredPage.REQUIRED_PAGES_ATTR,"done");
	    		}
	    	}
		}
		
		}catch(Exception e){
			throw new JspException(e);
		}
		return res;
	}

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	

}