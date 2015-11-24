// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.tags;

import javax.servlet.ServletRequest;
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class BasicSessionTag extends TagSupport implements Tag {

	
	@Override
	public int doEndTag() throws JspException {
		try{
		PageContext page = pageContext;
		ServletRequest request = page.getRequest();
		AppContext conn = ErrorFilter.makeContext(page.getServletContext(), request, page.getResponse());
	    if( conn == null ){
	    	// have to do this explicitly as normal method needs AppContesx
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
