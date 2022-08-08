//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.content;

import java.util.Collection;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.html.EmitHtmlInputVisitor;
import uk.ac.ed.epcc.webapp.forms.html.InputIdVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PrefixInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TagInput;
import uk.ac.ed.epcc.webapp.preferences.Preference;
/** container object for html form state and settings.
 * 
 * Added by composition to implement {@link XMLContentBuilder}
 * 
 * @author Stephen Booth
 *
 */
public class HtmlFormPolicy{
	public static final Feature HTML_USE_LABEL_FEATURE = new Preference("html.use_label",true,"generate html labels in automatic forms");
private HtmlFormPolicy parent=null;
private Collection<String> missing_fields=null;
private Map<String,String> errors=null;
private Map<String,Object> post_params=null;




public boolean isMissing(String field){
	if( missing_fields == null ){
		if( parent != null){
			return parent.isMissing(field);
		}else{
			return false;
		}
	}
	return missing_fields.contains(field);
}
public String getError(String field){
	if( errors == null ){
		return null;
	}
	return errors.get(field);
}



/** should we do browser required field validation
 * 
 */
private boolean use_required=true;
public boolean setUseRequired(boolean use_required){
	boolean old_value = use_required;
	this.use_required=use_required;
	return old_value;
}
public boolean getUseRequired() {
	return use_required;
}
/** Should locked inputs have their values posted as hidden parameters
 * 
 */
private boolean locked_as_hidden=false;
public boolean setLockedAsHidden(boolean value) {
	boolean old = locked_as_hidden;
	this.locked_as_hidden=value;
	return old;
}
public boolean getLockedAsHidden() {
	return locked_as_hidden;
}
public Collection<String> getMissingFields() {
	return missing_fields;
}


public void setMissingFields(Collection<String> missing_fields) {
	this.missing_fields = missing_fields;
}


public Map<String,String> getErrors() {
	return errors;
}


public void setErrors(Map<String,String> errors) {
	this.errors = errors;
}


public Map<String,Object> getPostParams() {
	return post_params;
}


public void setPostParams(Map<String,Object> post_params) {
	this.post_params = post_params;
}

private String action_name=null;
public void setActionName(String action_name) {
	this.action_name = action_name;
}
public String getActionName() {
	return action_name;
}

public <I,T> void addFormLabel(ExtendedXMLBuilder sb,AppContext conn,Field<I> f, T item) throws Exception {
	String key = f.getKey();
	boolean missing = isMissing(key);
	
	Input<I> i = f.getInput();
	boolean optional = f.isOptional();
	if( HTML_USE_LABEL_FEATURE.isEnabled(conn)){
		
		InputIdVisitor vis = new InputIdVisitor(conn,optional,f.getForm().getFormID());
		vis.setRadioTarget(item);

		String id =  vis.normalise((String) i.accept(vis));
		if( id != null){
			sb.open("label");
			sb.attr("for",id);
		}else {
			// accessability testers don't like orphan labels as they
			// confuse screen readers so revert to span if no id
			// e.g. for locked input
			sb.open("span");
		}

	}else{
		sb.open("span");
	}
	if( item == null ) {
		// Not applicable to per item labels
		if (optional) {
			sb.addClass("optional");
		}else{
			sb.addClass("required");
		}
	}
	if( missing) {
		sb.addClass("missing");
	}
	String tooltip = f.getTooltip();
	if( tooltip != null && ! tooltip.isEmpty()) {
		sb.attr("title",tooltip);
	}
	if( item != null && i instanceof ListInput) {
		sb.clean(((ListInput)i).getText(item)); // Label for a specific radio item
	}else {
		sb.clean(f.getLabel());
	}
	
	sb.close(); // span or label

//	if (missing) {
//		open("b");
//		open("span");
//		addClass( "warn");
//		clean("*");
//		close(); //span
//		close(); //b
//	}
	addFieldError(sb, key);
	
}
public void addFieldError(ExtendedXMLBuilder sb, String key) {
	String error = getError(key);
	if (error != null) {
		sb.nbs();
		sb.open("span");
		sb.addClass( "field_error");
		sb.clean(error);
		sb.close(); //span
	}
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.forms.Field)
 */
public  <I,T> void addFormInput(ExtendedXMLBuilder sb,AppContext conn,Field<I> f,T item) throws Exception {
	String key =  f.getKey();
	String error = null;
	
	boolean optional = f.isOptional();
	// If we have errors to report then we are showing the post_params.
	// if we want to force errors to be shown from the Form state (say
	// updating an old object with
	// invalid state then pass null post_params or set validate
	Map<String,Object> post_params = getPostParams();
	Map<String,String> errors = getErrors();
	Collection<String> missing_fields = getMissingFields();
	boolean use_post = (post_params != null)
			&& ((errors != null && errors.size() > 0) || (missing_fields != null && missing_fields
					.size() > 0));
	if (errors != null) {
		error = errors.get(key);
	}
	Input<I> i = f.getInput();
	if( i instanceof PrefixInput){
		sb.clean(((PrefixInput)i).getPrefix());
	}

	EmitHtmlInputVisitor vis = new EmitHtmlInputVisitor(conn,optional,sb, use_post, post_params,f.getForm().getFormID());
	vis.setRadioTarget(item);
	vis.setUseRequired(getUseRequired());
	vis.setAutoFocus(f.getKey().equals(f.getForm().getAutoFocus()));
	vis.setLockedAsHidden(getLockedAsHidden());
	i.accept(vis);

	if( i instanceof TagInput){
		sb.clean(((TagInput)i).getTag());
	}
	
}
public HtmlFormPolicy getChild() {
	HtmlFormPolicy child = new HtmlFormPolicy();
	
	child.setUseRequired(use_required);
	child.setActionName(action_name);
	
	child.setMissingFields(missing_fields);
	child.setErrors(errors);
	child.setPostParams(post_params);
	
	
	
	child.parent=this;
	return child;
}
}