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
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.servlet.DefaultServletService;
import uk.ac.ed.epcc.webapp.servlet.ErrorFilter;
import uk.ac.ed.epcc.webapp.servlet.WebappServlet;

/** A custom {@link Tag} that generates the confirmation form
 * 
 * @author spb
 *
 */

public class ConfirmTag extends TagSupport implements Tag{

	
	@Override
	public int doEndTag() throws JspException {
		PageContext page = pageContext;
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		HttpServletResponse response = (HttpServletResponse) page.getResponse();
		
		try {
			AppContext conn = ErrorFilter.retrieveAppContext(request, response);
			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
			HtmlBuilder block = new HtmlBuilder();
			addContent(request, conn, log, block);
					JspWriter out = page.getOut();
					out.print(block.toString());
		}catch(Exception e){
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	public static void addContent(HttpServletRequest request, AppContext conn, Logger log, HtmlBuilder block) {
		block.open("div");
		block.addClass("block");
		block.attr("role", "main");
		String message_type  = (String) request.getAttribute(WebappServlet.CONFIRM_TYPE);
		Object message_title=null;
		Object message_text=null;
		// only by attribute as we show this with markup
		Object extra_html= request.getAttribute(WebappServlet.EXTRA_HTML);
		String yes_text="yes";
		String no_text="no";
		if(message_type == null) message_type = request.getParameter(WebappServlet.CONFIRM_TYPE);
		Object args[] = (Object[]) request.getAttribute(WebappServlet.ARGS);
		if(args == null) args = new Object[0];

		if( message_type != null ){
			MessageBundleService serv = conn.getService(MessageBundleService.class);
			ResourceBundle mess = serv.getBundle("confirm");
		    yes_text = mess.getString("yes");
		    if( mess.containsKey(message_type+".yes")) {
			  yes_text = mess.getString(message_type+".yes");
		    }
		    no_text = mess.getString("no");
		    if( mess.containsKey(message_type+".no")) {
			  no_text = mess.getString(message_type+".no");
		    }
		  
		  if( message_title == null ){
			  message_title = new PreDefinedContent(conn,mess,message_type + ".title",args);
		  }
		  if( message_text == null ){
			message_text = new PreDefinedContent(conn,mess,message_type + ".text",args);
		  }
		}
			if( message_title == null ){
				message_title="Confirm Request";
			}
			if( message_text == null ){
				message_text="";
			}
			String post_url = (String) request.getAttribute(WebappServlet.CONFIRM_POST_URL);
			if( post_url == null ) post_url = 	request.getParameter(WebappServlet.CONFIRM_POST_URL);
				ContentBuilder h1 = block.getHeading(1);
				h1.addObject(message_title);
				h1.addParent();
				
				if( extra_html != null ){
					block.addObject(extra_html);
				}
				ExtendedXMLBuilder text = block.getText();
				text.addObject(message_text);
				text.appendParent();
				
		
				HtmlBuilder hb = new HtmlBuilder();
				hb.open("form");
				hb.attr("method", "post");
				if( post_url != null ) {
					// submit to self often ok for confirm.
					hb.attr("action", post_url);
				}
				// mark submitted similar to a page_form
				// this is so a servlet can test at a high level if its a re-submit
				// in case it needs to store state in the session.
				hb.open("input");
				hb.attr("type","hidden");
				hb.attr("name","submitted");
				hb.attr("value","true");
				hb.close();
				hb.clean("\n");
				
				Map<String,Object> h = null;
				// look for cached value if we have it may lose the parameters as part of a forward
				h = (Map<String,Object>) request.getAttribute(DefaultServletService.PARAMS_KEY_NAME);
				if( h != null ){
					for(String key : h.keySet()){
						hb.open("input");
						hb.attr("type","hidden");
						hb.attr("name",key);
						hb.attr("value",h.get(key).toString());
						hb.close();
						hb.clean("\n");
					}
				}else{
					for(Enumeration<?> e=request.getParameterNames();e.hasMoreElements();){
						String key=(String) e.nextElement();
						String values[] = request.getParameterValues(key);
						if( values != null && values.length > 0 ){
							for(int ii=0;ii<values.length;ii++){
								hb.open("input");
								hb.attr("type","hidden");
								hb.attr("name",key);
								hb.attr("value",values[ii]);
								hb.close();
								hb.clean("\n");

							}
						}else{
							hb.open("input");
							hb.attr("type","hidden");
							hb.attr("name",key);
							hb.attr("value",request.getParameter(key));
							hb.close();
							hb.clean("\n");

						}
					}
				}
				hb.open("input");
				hb.addClass("input_button");
				hb.attr("type", "submit");
				hb.attr("name", WebappServlet.CONFIRM_YES);
				hb.attr("value",yes_text);
				hb.close();
				
				hb.open("input");
				hb.addClass("input_button");
				hb.attr("type", "submit");
				hb.attr("name", WebappServlet.CONFIRM_NO);
				hb.attr("value",no_text);
				hb.close();
				
				hb.close();
				block.addObject(hb);
				block.close();
	}

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	

}