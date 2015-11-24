// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.tags;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.WebappServlet;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;

/** A custom {@link Tag} that inserts the navigation menu
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class NavigationTag extends TagSupport implements Tag {


	@Override
	public int doStartTag() throws JspException {
	
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		JspWriter out = page.getOut();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        try{
        	AppContext conn = ErrorFilter.retrieveAppContext(request);
        	if( conn != null ){
        		// If no appContext then skip nav bar
        		NavigationMenuService serv = conn.getService(NavigationMenuService.class);
        		if( serv != null ){
        			HtmlBuilder hb = new HtmlBuilder();
        			serv.getNavigation(request, hb);
        			out.print(hb.toString());
        		}
        	}
        	return EVAL_PAGE;
        } catch (Exception e) {
        	throw new JspException("Exception making navigation menu", e);
        }
		
	}

	

}
