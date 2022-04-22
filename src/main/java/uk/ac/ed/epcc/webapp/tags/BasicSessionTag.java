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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.WebappServlet;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A custom {@link Tag} that ensures that a current session exists for the user viewing the page
 * 
 * @author spb
 *
 */

public class BasicSessionTag extends TagSupport implements Tag {

	private String role=null;
	
	public void setrole(String role) {
		this.role=role;
	}
	@Override
	public int doEndTag() throws JspException {
		try{
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		HttpServletResponse response = (HttpServletResponse) page.getResponse();
		AppContext conn = ErrorFilter.retrieveAppContext(request, response);
				
	    if( conn == null ){
	    	// have to do this explicitly as normal method needs AppContext
	    	request.setAttribute(WebappServlet.MESSAGE_TYPE_ATTR, "internal_error");
	    	request.setAttribute(WebappServlet.MESSAGE_EXTRA_ATTR, "No AppContext");
	    	page.forward(WebappServlet.MESSAGES_JSP_URL);
	    	return SKIP_PAGE;
	    }
	    
		/* Retrieve database connection and person currently logged in - 
	     * if an error is returned, return to login page. 
	     */
	    ServletSessionService<?> session_service = (ServletSessionService) conn.getService(SessionService.class);
	    ServletService servlet_service = conn.getService(ServletService.class);
		if(session_service == null || ! session_service.haveCurrentUser()) {
	             String page_request=servlet_service.encodePage();
	             Logger log =conn.getService(LoggerService.class).getLogger(AppContext.class);
	             log.info("existing person/session not found "+page_request);
	             if( session_service == null ){
	            	 log.debug("Session service is null");
	             }else{
	            	 log.debug("Service "+session_service.getClass().getCanonicalName()+" haveCurrentUser="+session_service.haveCurrentUser());
	             }
	             servlet_service.requestAuthentication(session_service); 
	             return SKIP_PAGE;
		}
		if( role != null ) {
	    	if( ! session_service.hasRoleFromList(role.split("\\s*,\\s*"))) {
	    		WebappServlet.messageWithArgs(conn, request, response, "access_denied", null);
	    		return SKIP_PAGE;
	    	}
	    }
		pageContext.setAttribute("session_service", session_service);
		return EVAL_PAGE;
		}catch(Exception e){
			throw new JspException(e);
		}
	}

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	

}