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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput;
/** Common Base class for different types of HTML Form
 * 
 * @author spb
 *
 */
public abstract class BaseHTMLForm extends MapForm {
	public BaseHTMLForm(AppContext c) {
		super(c);
	}
	/**
	 * emit the action buttons (if any) registered for this form.
	 * 
	 * @return String HTML fragment
	 */
	public String getActionButtons() {
		return getActionButtons(new HtmlBuilder()).toString();
	}
	public HtmlBuilder getActionButtons(HtmlBuilder result){
		result.setActionName(action_name);
		result.addActionButtons(this);
		return result;
	}
	/**
	 * get the current contents of the form as a set of hidden parameters
	 * 
	 * @return String HTML form fragment
	 */
	public String getHiddenForm() {
		return getHiddenForm(new HtmlBuilder()).toString();
	}
	public <X extends HtmlPrinter> X getHiddenForm(X result){
		for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();
			Field f =  getField(key);
			Object val = f.getValue();
			if (val != null) {
				getHiddenParam(result,f.getInput());
			}
		}
		return result;

	}
	/**
	 * emit an HTML form with error markup specified 
	 * if post_params or errors are null show errors from the Form state 
	 * @param missing_fields
	 * @param errors
	 * @param post_params
	 * @return string HTML fragment
	 * @throws Exception 
	 * 
	 */
	protected String getHtmlFieldTable(Collection<String> missing_fields, Map<String,String> errors,
			Map<String,Object> post_params) {
		return getHtmlFieldTable(new HtmlBuilder(), missing_fields, errors, post_params).toString();
	}

	
	protected < X extends HtmlBuilder> X getHtmlFieldTable(X result,Collection<String> missing_fields, Map<String,String> errors,
				Map<String,Object> post_params) {
		result.setErrors(errors);
		result.setMissingFields(missing_fields);
		result.setPostParams(post_params);
		result.addFormTable(getContext(), this);
		
		return result;
	}
	
	
	
	private void emitHiddenParam(HtmlPrinter hb,Input i) {
		String value;
		if (i instanceof ParseInput) {
			ParseInput p = (ParseInput) i;
			value = p.getString();
		} else {
			value = i.getValue().toString();
		}
		emitHiddenParam(hb,i.getKey(), value);
	}
	private void emitHiddenParam(HtmlPrinter hb,String key, String value){
		hb.open("input");
		hb.attr("type","hidden");
		hb.attr("name",key);
		hb.attr("value",value);
		hb.close();

	}
	private void getHiddenParam(HtmlPrinter hb,Input i){
		if(i instanceof ParseMapInput){
			ParseMapInput c = (ParseMapInput) i;
			Map<String,Object> map = c.getMap();
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				String value = map.get(key).toString();
				emitHiddenParam(hb,key,value);
			}
		}else if (i instanceof MultiInput) {
			MultiInput c = (MultiInput) i;
			for (Iterator it = c.getInputs(); it.hasNext();) {
				Input t = (Input) it.next();
				emitHiddenParam(hb,t);
			}
		} else {
			emitHiddenParam(hb,i);
		}
	}

	

	
	
	
}