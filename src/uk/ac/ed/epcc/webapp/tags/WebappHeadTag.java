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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;

/**
 * @author spb
 *
 */
public class WebappHeadTag extends TagSupport implements Tag {
	public static final Preference SCRIPT_FORMS_FEATURE = new Preference("script.forms",true,"Augment unsupported html5 inputs using javascript");
	/**
	 * 
	 */
	private static final String REQUEST_SCRIPT_ATTR = "request_script";
	/**
	 * 
	 */
	private static final String REQUEST_CSS_ATTR = "request_css";
	/** request attribute denoting a page containing a form
	 * 
	 */
	public static final String FORM_PAGE_ATTR = "form_page";
	private String extra_css;
	 
	public void setextraCSS(String extra){
		extra_css=extra;
	}

	@Override
	public int doStartTag() throws JspException {
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		Set<String> request_css = (Set<String>) request.getAttribute(REQUEST_CSS_ATTR);
		Set<String> request_script = (Set<String>) request.getAttribute(REQUEST_SCRIPT_ATTR);
		JspWriter out = page.getOut();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        Set<String> scripts = new HashSet<String>(); // don't emit the same script twice 
        try{
        	AppContext conn = ErrorFilter.retrieveAppContext(request,response);
        	if( conn != null ){
        		// If no appContext then skip nav bar
        		String template_path = request.getContextPath()+conn.getInitParameter("template.path","");	
        		
        		String favicon = conn.getInitParameter("favicon");
        		if( favicon != null && ! favicon.isEmpty()){
        			doIcon(out, response, template_path, conn.getInitParameter("favicon.type", "image/png"), favicon);
        		}
        		doCSS(out, response, template_path,"default css","webapp.css");
        		if( NavigationMenuService.NAVIGATION_MENU_FEATURE.isEnabled(conn)){
        			doCSS(out, response, template_path,null,"nav_menu.css");
        		}
        		if(SCRIPT_FORMS_FEATURE.isEnabled(conn) && request.getAttribute(FORM_PAGE_ATTR) != null){
        			doCSS(out, response, template_path,null,"${jquery-ui.css}");
        		}
        		doCSS(out, response, template_path,null,"extra.css");
        	
        		if(extra_css != null && extra_css.trim().length() > 0){ 
        			for( String css : extra_css.split(",")){
        				doCSS(out, response, template_path, null,css);		 
        			}
        		}
        		
        		if( NavigationMenuService.NAVIGATION_MENU_JS_FEATURE.isEnabled(conn)){
        			doScript(scripts,out,request,response,"${jquery.script}");
        			doScript(scripts,out, request,response, "/scripts/menubar.js");
        		}
        		if(SCRIPT_FORMS_FEATURE.isEnabled(conn)&& request.getAttribute(FORM_PAGE_ATTR) != null){
        			doScript(scripts,out,request,response,"${jquery.script}");
        			doScript(scripts,out,request,response,"${jquery-ui.script}");
        			doScript(scripts,out,request,response,"/js/modernizr-custom.js");
        			doScript(scripts,out,request,response,"/js/fixinputs.js");
        		}
        		if( request_script != null  ){
        			// allow escaped commas in in-line scripts
        			for(String script : request_script){
        				doScript(scripts,out,request,response,script);
        			}
        		}
        		if(request_css != null ){ 
        			for( String css : request_css){
        				doCSS(out, response, template_path, null,css);		 
        			}
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
		if( file.startsWith("//")){
			out.print(file);
		}else{
			out.print(response.encodeURL(template_path+"/css/"+file));
		}
		out.println("\" rel=\"stylesheet\" type=\"text/css\">");
	}
	protected void doIcon(JspWriter out, HttpServletResponse response, String template_path,String type,String file) throws IOException {
		out.print("<link rel=\"icon\" ");
		if( type != null ){
			out.print("type=\"");
	
			out.print(type);
			out.print("\" ");
		}
		out.print("href=\"");
		out.print(response.encodeURL(template_path+"/"+file));
		out.println("\" >");
	}
	protected void doScript(Set<String> scripts, JspWriter out, HttpServletRequest request, HttpServletResponse response, String location) throws IOException {
		// only include scripts once.
		if( scripts.contains(location)){
			return;
		}
		scripts.add(location);
		if( location.startsWith("/")){
			// script file
			out.print("<script type=\"text/javascript\" charset=\"utf8\" src=\"");
			if( location.startsWith("//")){
				// remote script file
				out.print(location);
			}else {
				// local script file
				out.print(response.encodeURL(request.getContextPath()+location));		
			}
			out.println("\"></script>");
		}else{
			// in-line script
			out.print("<script type=\"text/javascript\" charset=\"utf8\" >");
			out.print(location);
			out.println("</script>");
		}
	}
	
	public static void addScript(AppContext conn,HttpServletRequest request, Set<String> script){
		if( script == null){
			return;
		}
		for(String s : script){
			addScript(conn, request, s);
		}
	}
	/** Add a script to be included by the head tag.
	 * 
	 * must be called before the head tag.
	 * parameters are expanded. If the value starts with a / it is a url.
	 * If it starts with // it is a remote url.
	 * 
	 * This method is to include scripts from a java fragment.
	 * 
	 * @param conn
	 * @param request
	 * @param script
	 */
	public static void addScript(AppContext conn,HttpServletRequest request, String script){
		Set<String> scripts = (Set<String>) request.getAttribute(REQUEST_SCRIPT_ATTR);
		if( scripts == null ){
			scripts=new LinkedHashSet<String>();
			request.setAttribute(REQUEST_SCRIPT_ATTR, scripts);
		}
		scripts.add(conn.expandText(script));
	}
	public static void addCss(AppContext conn,HttpServletRequest request, Set<String> css){
		if( css == null){
			return;
		}
		for(String c : css){
			addCss(conn,request,c);
		}
	}
	public static void addCss(AppContext conn,HttpServletRequest request, String css){
		Set<String> stylesheets = (Set<String>) request.getAttribute(REQUEST_CSS_ATTR);
		if( stylesheets == null ){
			stylesheets=new LinkedHashSet<String>();
			request.setAttribute(REQUEST_CSS_ATTR, stylesheets);
		}
		stylesheets.add(conn.expandText(css));
	}
}
