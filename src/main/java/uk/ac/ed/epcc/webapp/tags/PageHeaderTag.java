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

/** Include a standard header fragment.
 * 
 * A dynamic include is used to allow the content to be overridden by the top level application.
 * 
 * This is essentially a jsp:include with a proeprty configurable target
 * The main motivation for a customtag is that authoring tools such as eclipse will report
 * errors if a fragment that does not exist is included. Even if it does exist they frequently fail to resolve
 * absolute paths correctly in web-fragment projects
 * 
 * 
 * @author Stephen Booth
 *
 */
public class PageHeaderTag extends TagSupport implements Tag {

	@Override
	public int doStartTag() throws JspException {
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		HttpServletResponse response = (HttpServletResponse) page.getResponse();
		
		try {
			AppContext conn = ErrorFilter.retrieveAppContext(request, response);
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
			String frag = conn.getInitParameter("service.std_header", "/page_header.jsp");
			if( frag != null && ! frag.isEmpty() && ! frag.contentEquals("none")) {
				try {
					page.include(frag);
				}catch(Exception e1) {
					log.error("Error including page "+frag, e1);
				}
			}
		}catch(Exception e){
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

}
