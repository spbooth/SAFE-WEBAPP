//| Copyright - The University of Edinburgh 2017                            |
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

import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;

/** Tag to insert message from content bundle using a {@link PreDefinedContent}
 * @author spb
 *
 */
public class WebappMessage extends TagSupport implements Tag {

	private String message=null;
	private String bundle=null;
	private boolean optional=false;
	
	/**
	 * 
	 */
	public WebappMessage() {
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * @param bundle the bundle to set
	 */
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public void setOptional(String optional) {
		try {
			this.optional=Boolean.parseBoolean(optional);
		}catch(Exception t) {
			this.optional=false;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		JspWriter out = page.getOut();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        try{
        	AppContext conn = ErrorFilter.retrieveAppContext(request,response);
        	if( conn != null ){
        		PreDefinedContent content = new PreDefinedContent(conn,bundle, message);
        		HtmlBuilder hb = new HtmlBuilder();
        		content.addContent((SimpleXMLBuilder) hb);
        		out.print(hb.toString());
        	}
        	return EVAL_PAGE;
        }catch(MissingResourceException r) {
        	if( ! optional) {
        		throw new JspException("Undefined content "+message,r);
        	}
        	return EVAL_PAGE;
        } catch (Exception e) {
        	throw new JspException("Exception making pre-defined content", e);
        }
	}

}
