package uk.ac.ed.epcc.webapp.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.email.QueuedMessages;
import uk.ac.ed.epcc.webapp.email.QueuedMessages.QueuedMessage;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;

public class QueuedMessageTag extends TagSupport implements Tag {
	@Override
	public int doStartTag() throws JspException {
	
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		JspWriter out = page.getOut();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        try{
        	AppContext conn = ErrorFilter.retrieveAppContext(request,response);
        	if( conn != null ){
        		QueuedMessages fac = QueuedMessages.getFactory(conn);
        		long count = fac.getQueuedMessageCount();
        		if( count > 0L) {
        			pageContext.setAttribute("QueuedCount", count);
        			return EVAL_BODY_INCLUDE;
        		}
        	}
        	return SKIP_BODY;
        } catch (Exception e) {
        	throw new JspException("Exception making navigation menu", e);
        }
		
	}
}
