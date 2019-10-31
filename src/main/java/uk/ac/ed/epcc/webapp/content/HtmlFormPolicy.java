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

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

public class HtmlFormPolicy{

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
/** Should locked inputs have their vvalues posed as hidden parameters
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