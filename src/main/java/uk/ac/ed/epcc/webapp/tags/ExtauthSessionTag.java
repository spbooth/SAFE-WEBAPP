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
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.WebappServlet;

/** A custom {@link Tag} that ensures that a current session exists for the user viewing the page
 * 
 * @author spb
 *
 */

public class ExtauthSessionTag extends TagSupport implements Tag {

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
	    	// have to do this explicitly as normal method needs AppContesx
	    	request.setAttribute(WebappServlet.MESSAGE_TYPE_ATTR, "internal_error");
	    	request.setAttribute(WebappServlet.MESSAGE_EXTRA_ATTR, "No AppContext");
	    	page.forward(WebappServlet.MESSAGES_JSP_URL);
	    	return SKIP_PAGE;
	    }
	   ServletService servlet_service = conn.getService(ServletService.class);
	    
	 	String username = null;
	    if( conn != null ){
	    	 username = conn.getService(ServletService.class).getWebName();
	    }
		if(username == null || username.trim().length() == 0) {
			WebappServlet.messageWithArgs(conn, request, response, "access_denied", null);
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