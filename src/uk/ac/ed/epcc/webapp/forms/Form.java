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
package uk.ac.ed.epcc.webapp.forms;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Interface for building and querying Forms.
 * A Form is functionally an ordered set of {@link Field}s. Once
 * built a Form can be used as a Map between field names and values. The methods
 * used to actually edit a form are specific to the implementing class.
 * 
 *<p>
 *A Form may either be used as just a collection of fields with associated validation ie as a component
 *of a larger composite form or may contain a set of {@link FormAction} classes to define an action to take on form submission.
 *
 * @author spb
 * 
 */


public interface Form extends Iterable<Field>, Contexed{

	

	/**
	 * add an {@link FormAction} to this Form
	 * 
	 * @param name
	 *            String action-name/button-text
	 * @param action
	 *            FormAction
	 */
	public void addAction(String name, FormAction action); 
	
	/** remove a {@link FormAction}.
	 * 
	 * @param name
	 */
	public void removeAction(String name);

	/** get a {@link FormAction} by name.
	 * 
	 * @param name
	 * @return FormAction
	 */
	public FormAction getAction(String name);
	/**
	 * Add and input to the next slot in the form
	 * 
	 * @param key
	 *            key to use to refer to field
	 * @param label
	 *            String to display to user
	 * @param s
	 *            Input to add
	 * @return Field object created
	 */
	public <I> Field addInput(String key, String label, Input<I> s); 

	/**
	 * Add and input to the next slot in the form
	 * 
	 * @param key
	 *            key to use to refer to field
	 * @param label
	 *            String to display to user
	 * @param  help
	 *            tooltip String
	 * @param s
	 *            Input to add
	 * @return Field object created
	 */
	public <I> Field addInput(String key, String label, String help,Input<I> s);

	/**
	 * Check a form to see if it contains an input of a particular type.
	 * 
	 * @param c
	 *            Class for the Input type to be testes
	 * @return bioolean true if input exists
	 */
	public boolean containsInput(Class<? extends Input> c); 

	/**
	 * perform a named action on this form. This also checks the form is
	 * validated
	 * 
	 * @param name
	 *            String name of action
	 * @return FormResult returned by action
	 * @throws FieldException
	 * @throws ActionException
	 */
	public FormResult doAction(String name) throws FieldException, ActionException; 
	
	/**
	 * get the value associated with a Form field.
	 * non existent fields alwayrs return null.
	 * @param key
	 * @return value
	 */
	public Object get(String key); 
	/** get the Item associated with a Form field.
	 * 
	 * @param key
	 * @return item
	 */
	public Object getItem(String key);
	/** Get an {@link Iterator} over the names of the installed Actions.
	 * 
	 * @return Iterator
	 */
	public Iterator<String> getActionNames(); 
	
	
	public void setConfirm(String name, String conf);
    
	/**
	 * get the contents of a form as a Map
	 * 
	 * @return Map
	 */
	public Map<String,Object> getContents(); 

	

	/**
	 * get a Field object
	 * 
	 * @param key
	 * @return Field
	 */
	public Field getField(String key); 

	/**
	 * get an iterator over the field names
	 * 
	 * @return Iterator
	 */
	public Iterator<String> getFieldIterator(); 

	/**
	 * get the Input associated with a Field
	 * 
	 * @param key
	 * @return the Input or null if Input does not exist
	 */
	public Input getInput(String key); 
    
    /** get the form contents as a Table
     * 
     * @return Table
     */
	public Table<String,String> getTable();
	

	
	/**
	 * set the value associated with a form field
	 * 
	 * @param key
	 * @param value
	 * @return previous value
	 */
	public Object put(String key, Object value); 

	/**
	 * remove a field from the form
	 * 
	 * @param key
	 */
	public void removeField(Object key);

	
	/**
	 * set the contents of a Form from a Map
	 * 
	 * @param m
	 *            Map of values
	 */
	public void setContents(Map<String,Object> m); 
	/**
	 * Add a {@link FormValidator} to perform overall sanity check on the form.
	 * 
	 * @param v
	 *            The FormValidator to set.
	 */
	public void addValidator(FormValidator v); 

	/** Remove a {@link FormValidator}
	 * 
	 * @param v
	 */
	public void removeValidator(FormValidator v);
	
	/** Get the set of {@link FormValidator}s installed in the form.
	 * This is a copy of the internal set and is not modifiable, though
	 * its members can be removed from the active set by passing them
	 * to {@link #removeValidator(FormValidator)}
	 * 
	 * @return Set<FormValidator>
	 */
	public Set<FormValidator> getValidators();
	/**
	 * modify an existing Map updating the fields represented by the Form.
	 * Fields that are null are removed from the Map
	 * 
	 * @param m
	 *            Map to update
	 */
	public void update(Map<String,Object> m); 
	/** Compare the values of this forms inputs with the contents of a Map
	 *  and generate a text summary;
	 * @param m Map to compare to
	 * @return String summary or null
	 */
   
	public String diff(Map<String,Object> m);
	/**
	 * validate a form. This method checks the fields and the
	 * installed {@link FormValidator} if any. No details of the
	 * exact error are returned by this call.
	 * 
	 * @return boolean true if valid
	 */
	public boolean validate(); 
	/** Attempt to show/validate  the current state of the form as part of a multi-phase 
	 * form.
	 * 
	 * This is called during form construction when values from inputs are needed to construct the rest of the form.
	 * If the method returns true then form construction can continue. If false then the method will have modified the
	 * form to add actions to progress the multi-stage completion (by recursing to the self result) and the form should be displayed as-is.
	 * 
	 * <p>
	 * If multi-stage submission is not supported the method will just return true.
	 * @param self {@link FormResult} for this operation
	 * @return boolean   true if form build should continue
	 * @throws TransitionException
	 */
	default public boolean poll(FormResult self) throws TransitionException{
		return true;
	}
	/** which stage of a multi-stage form is being shown/read.
	 * 
	 * @return
	 */
	default public int getTargetStage() {
		return 0;
	}
	/** Set a form-id for this form.
	 * In HTML this is used as a prefix for the input html-ids
	 * 
	 * @param id
	 */
	public void setFormID(String id);
	/** get the form-ids
	 * In HTML this is used as a prefix for the input html-ids
	 * @return
	 */
	public String getFormID();
	
	/** set field for auto-focus
	 * 
	 * @param field
	 */
	public void setAutoFocus(String field);
	
	/** get field for auto-focus
	 * 
	 * @return
	 */
	public String getAutoFocus();
}