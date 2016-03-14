//| Copyright - The University of Edinburgh 2016                            |
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;

/**
 * @author spb
 *
 */
public class WebappHeadTag extends TagSupport implements Tag {
	private String extra_css;
	
	public void setextraCSS(String extra){
		extra_css=extra;
	}

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
        		String template_path = request.getContextPath()+conn.getInitParameter("template.path","");	
        		
        		doCSS(out, response, template_path,"default css","webapp.css");
        		if( NavigationMenuService.NAVIGATION_MENU_FEATURE.isEnabled(conn)){
        			doCSS(out, response, template_path,null,"nav_menu.css");
        		}
        		doCSS(out, response, template_path,null,"extra.css");
        	
        		if(extra_css != null && extra_css.trim().length() > 0){ 
        			for( String css : extra_css.split(",")){
        				doCSS(out, response, template_path, null,css);		 
        			}
        		}
        		if( NavigationMenuService.NAVIGATION_MENU_JS_FEATURE.isEnabled(conn)){
        			doScript(out,request,response,"//code.jquery.com/jquery-1.10.2.min.js");
        			doScript(out, request,response, "/scripts/menubar.js");
        		}
        		
        	}
        	return EVAL_PAGE;
        } catch (Exception e) {
        	throw new JspException("Exception making navigation menu", e);
        }
	}

	/**
	 * @param out
	 * @param response
	 * @param template_path
	 * @throws IOException
	 */
	protected void doCSS(JspWriter out, HttpServletResponse response, String template_path,String title,String file) throws IOException {
		out.print("<link ");
		if( title != null ){
			out.print("title=\"");
	
			out.print(title);
			out.print("\" ");
		}
		out.print("href=\"");
		out.print(response.encodeURL(template_path+"/css/"+file));
		out.println("\" rel=\"stylesheet\" type=\"text/css\">");
	}
	protected void doScript(JspWriter out, HttpServletRequest request, HttpServletResponse response, String location) throws IOException {
		out.print("<script type=\"text/javascript\" charset=\"utf8\" src=\"");
		if( location.startsWith("//")){
		    out.print(location);
		}else{
			out.print(response.encodeURL(request.getContextPath()+location));
		}
		out.println("\"></script>");
	}
	
}
