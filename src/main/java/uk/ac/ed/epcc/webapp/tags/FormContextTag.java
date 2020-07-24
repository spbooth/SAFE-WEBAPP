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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.servlet.DefaultServletService;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.WebappServlet;

/** A custom {@link Tag} that generates optional before
 * a form that shows when the form has errors
 * 
 * @author spb
 *
 */

public class FormContextTag extends TagSupport implements Tag{

	private boolean inline=false;
	
	public void setinline(boolean val) {
		this.inline=val;
	}
	
	@Override
	public int doEndTag() throws JspException {
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		HttpServletResponse response = (HttpServletResponse) page.getResponse();

		try {
			AppContext conn = ErrorFilter.retrieveAppContext(request, response);
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());

			// Handle any fields missing input.
			Collection<String> missing_fields = HTMLForm.getMissing(request);
			Map<String,String> errors = HTMLForm.getErrors(request);
			String general_error="";
			HtmlBuilder block = new HtmlBuilder();
			if( missing_fields != null &&  missing_fields.size() > 0 ){
				block.br();
				block.open("div");
				block.addClass(inline?"error":"block");
				block.addHeading(2,"Form errors:");
				block.open("A");
				block.attr("name","form_error");
				block.close();
				ExtendedXMLBuilder text = block.getText();
				text.clean("There are "+missing_fields.size()+" required fields missing from this form. These are marked with a ");
				text.open("span");
				text.addClass("warn");
				text.clean("*");
				text.close();
				text.clean(".");
				text.appendParent();

				block.close();


			} 
			if( errors != null && errors.size() > 0 ){ 

				if( errors.containsKey("general") ){
					general_error=(String) errors.get("general");
				}else{
					general_error = "See individual field errors";
				}
				ContentBuilder hb = block.getPanel(inline?"error":"block");
				hb.addHeading(3,"This form contains errors:");
				ExtendedXMLBuilder m = hb.getText();
				m.addClass("warn");
				m.clean(general_error);
				m.appendParent();
				hb.addParent();


				JspWriter out = page.getOut();
				out.print(block.toString());
			}
		}catch(Exception e){
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}



}