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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
/** Common Base class for different types of HTML Form
 * 
 * @author spb
 *
 */
public abstract class BaseHTMLForm extends MapForm {
	public static final String FORM_STATE_ATTR = "form_state";
	public static final String FORM_STAGE_INPUT = "form_stage";
	
	public BaseHTMLForm(AppContext c) {
		super(c);
	}
	
	protected int target_stage=0; // stage being evaluates
	protected int stage=0;// form stage we are considering
	
	@Override
	public int getTargetStage() {
		return target_stage;
	}
	@Override
	public boolean poll(FormResult self) throws TransitionException{
		boolean result=false;
		AppContext c = getContext();
		
		
		final ServletService service = c.getService(ServletService.class);
		Map<String,Object>	params = (Map<String, Object>) service.getRequestAttribute(FORM_STATE_ATTR);
		if( params == null) {
			params = service.getParams(); // use post params if not caches
		}
		
		String stage_string=(String) params.get(FORM_STAGE_INPUT);
		if( stage_string != null ) {
			// from post is from a multi stage form
			target_stage = Integer.parseInt(stage_string);
		}
		
		if( stage < target_stage ) {

			// current form state should parse and validate from params
			if( parsePost(null, params, false) && validate()) {
				// lock all these fields going forward
				for(Iterator<String> it=getFieldIterator();it.hasNext();) {
					String field = it.next();
					Field ff = getField(field);
					ff.lock();
				}
			}else {
				getLogger().error("re-validate failed in poll "+stage+":"+target_stage, new Exception());
				throw new TransitionException("Internal error please retry");
			}
			stage++;
			result=true;  // keep building
		}
		
		if( ! result ) {
			// Form creation has been terminated early 
			// so add a next action
			addAction("Next", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					assert( f == BaseHTMLForm.this);
					// Need to cache form state for later in same request
					// we are relying on the form redisplay being generated from
					// a forward not a redirect
					Map<String,Object> values = new HashMap<>();
					values.put(FORM_STAGE_INPUT, Integer.toString(target_stage+1));
					addStringMap(values);
					service.setRequestAttribute(FORM_STATE_ATTR, values);
					return self;
				}
			});
		}
		
		return result;
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
		if( target_stage > 0 ) {
			// record which stage we are at
			BaseHTMLForm.emitHiddenParam(result, FORM_STAGE_INPUT, Integer.toString(target_stage));
		}
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
		HtmlBuilder result = new HtmlBuilder();
		return getHtmlFieldTable(result, missing_fields, errors, post_params).toString();
	}

	
	protected < X extends HtmlBuilder> X getHtmlFieldTable(X result,Collection<String> missing_fields, Map<String,String> errors,
				Map<String,Object> post_params) {
		result.setLockedAsHidden(stage>0);
		result.setErrors(errors);
		result.setMissingFields(missing_fields);
		result.setPostParams(post_params);
		result.addFormTable(getContext(), this);
		
		return result;
	}
	
	
	
	public static void emitHiddenParam(ExtendedXMLBuilder hb,Input i) {
		String value;
		if (i instanceof ParseInput) {
			ParseInput p = (ParseInput) i;
			value = p.getString();
		} else {
			value = i.getValue().toString();
		}
		emitHiddenParam(hb,i.getKey(), value);
	}
	public static void emitHiddenParam(ExtendedXMLBuilder hb,String key, String value){
		hb.open("input");
		hb.attr("type","hidden");
		hb.attr("name",key);
		hb.attr("value",value);
		hb.close();

	}
	public static void getHiddenParam(ExtendedXMLBuilder hb,Input i){
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