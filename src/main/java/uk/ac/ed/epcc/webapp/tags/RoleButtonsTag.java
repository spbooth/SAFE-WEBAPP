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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A custom {@link Tag} that inserts the navigation menu
 * 
 * @author spb
 *
 */

public class RoleButtonsTag extends TagSupport implements Tag {


	@Override
	public int doStartTag() throws JspException {
	
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		JspWriter out = page.getOut();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        try{
        	AppContext conn = ErrorFilter.retrieveAppContext(request,response);
        	SessionService session_service = conn.getService(SessionService.class);
        	for(Iterator it=session_service.getToggleRoles().iterator(); it.hasNext(); ){
        		  String role=(String) it.next();
        		  String role_name=role;
        		  HtmlBuilder role_hb = new HtmlBuilder();
        		  role_hb = (HtmlBuilder)role_hb.getPanel("bar");
        		  boolean has_role= session_service.hasRole(role);
        		  ExtendedXMLBuilder h = (ExtendedXMLBuilder) role_hb.getHeading(3);
        		  h.addClass(has_role ? "has_role" : "missing_role" );
        		  h.clean("You are "+ (has_role? "": " NOT ")+role_name );
        		  h.appendParent();
        		  role_hb.open("form");
        		  role_hb.attr("method", "post");
        		  role_hb.attr("action",response.encodeURL(request.getContextPath()+"/UserServlet"));
        		  role_hb.open("input",new String[][]{{"type","hidden"},{"name","page"},{"value",conn.getService(ServletService.class).encodePage()}});
        		  role_hb.close();
        		  role_hb.open("input",new String[][]{{"type","hidden"},{"name","action"},{"value","TOGGLE_PRIV"}});
        		  role_hb.close();
        		  role_hb.open("input",new String[][]{{"type","hidden"},{"name","role"},{"value",role}});
        		  role_hb.close();
        		  role_hb.open("input",new String[][]{{"class","input_button"},{"type","submit"},{"value"," Toggle "}});
        		  role_hb.close();
        		  role_hb.close();
        		  role_hb=(HtmlBuilder)role_hb.appendParent();
        		  out.print(role_hb.toString());
        	}
        	return EVAL_PAGE;
        } catch (Exception e) {
        	throw new JspException("Exception making navigation menu", e);
        }
		
	}

	

}