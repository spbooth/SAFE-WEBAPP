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

public class NavigationTag extends TagSupport implements Tag {


	@Override
	public int doStartTag() throws JspException {
	
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		JspWriter out = page.getOut();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        try{
        	AppContext conn = ErrorFilter.retrieveAppContext(request,response);
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