//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
//import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** A HTML form intended to be used within pages where the form posts back to the 
 * calling page
 * @author spb
 *
 */

/** A HTML form intended to be used in a jsp page that 
 * self-submits. 
 * 
 * A hidden control is used to determine if the form has been submitted and
 * needs to be validated against external inputs. Otherwise the form validates 
 * against its initial state so these forms should be initialised to a valid state
 * to avoid error markup on the initial show of the form.
 * 
 * @author spb
 *
 */
public class PageHTMLForm extends BaseHTMLForm {
    private boolean has_submitted=false;
    private boolean has_errors=false;
    Set<String> missing;
    Map<String,String> errors;
    Map<String,Object> params;
    // name of control indicating form has been submitted.
    private final String submitted;
    
    public PageHTMLForm(AppContext c){
    	this(c, "submitted");
    }
	public PageHTMLForm(AppContext c,String submitted) {
		super(c);
		missing = new HashSet<>();
		errors = new HashMap<>();
		this.submitted=submitted;
	}
	/** Parse the post parameters needs to be called before the 
	 * Form is shown. State is stored in the Form.
	 * 
	 * @param request
	 * @return true if ok
	 */
	public boolean parsePost(HttpServletRequest request){
		has_submitted = hasSubmitted(request); // check request directly as we have not made 
																   // params yet
		AppContext c =  getContext();
		if( has_submitted ){
			// check the submitted values
		
			params = c.getService(ServletService.class).getParams();
		}else{
			params=null; // this should make the form validate the initial state.
		}
		
		boolean ok = parsePost(errors, params,false);
		if (!ok) {
			c.getService(LoggerService.class).getLogger(getClass()).debug("internal parse failed");
		}
		ok = ok && validate(missing, errors);
		if (!ok) {
			c.getService(LoggerService.class).getLogger(getClass()).debug("internal validate failed");
		}
		has_errors = ! ok;
		
		return ok;
	}
	/** check for a submitted form before parsing the post.
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasSubmitted(HttpServletRequest request) {
		return request.getParameter(submitted) != null;
	}
	/** Get the actual HTML form with markup
	 * 
	 * @return String HTML fragememt
	 */
    public String getHtmlForm(){
    	return getHtmlForm(new HtmlBuilder()).toString();
    }
    public HtmlBuilder getHtmlForm(HtmlBuilder result){
		  getHtmlFieldTable(result,missing, errors, params);
          result.clean('\n');
          result.open("input");
          result.attr("type", "hidden");
          result.attr("name",submitted);
          result.attr("value", "true");
          result.close();
          result.clean('\n');
    	  getActionButtons(result);
    	return result;
    }
	@Override
	public <X extends HtmlPrinter> X getHiddenForm(X result){
		super.getHiddenForm(result);
		if( hasSubmitted()){
			result.open("input");
			result.attr("type", "hidden");
			result.attr("name",submitted);
			result.attr("value", "true");
			result.close();
		}
		return result;
	}
	public boolean hasSubmitted(){
	  return has_submitted;	
	}
	public boolean hasError(){
		return has_errors;
	}
	public String getGeneralError(){
		return errors.get(MapForm.GENERAL_ERROR);
	}
    /** Set an error to be displayed in the form.
     * This will be done automatically for errors found in the parse
     * but errors generated in the {@link FormAction} as exceptions need
     * to be caught and set explicitly
     * 
     * @param field
     * @param error
     */
	public void setError(String field,String error) {
		errors.put(field, error);
	}
	public void setGeneralError(String error) {
		errors.put(MapForm.GENERAL_ERROR, error);
	}
}