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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/**
 * Field represents a single field in the form consisting of a label and and Input.
 * 
 * @author spb
 * @param <I> Type of field value
 * 
 */


public final class Field<I> {
	@Override
	public String toString() {
		return "Field [label=" + label + ", value="+(sel.getValue()==null?"null":sel.getPrettyString(sel.getValue()))+", sel="
				+ sel + "]";
	}

	

	/** text label of this field presented to the user.
	 * 
	 */
	private String label;
	
	private String tooltip;
	private boolean optional=false;
	

	private Input<I> sel;
	
	private final Form f;

	public Field(Form form,String key, String label, Input<I> sel) {
		super();
		this.f=form;
	//	this.key = key;
		if (label != null) {
			this.label = label;
		} else {
			this.label = null;
		}
		this.sel = sel;
		this.sel.setKey(key);
	}
	/** Convert the Input to an UnmodifiedInput
	 * by wrapping it in a LockedInput
	 * 
	 */
    public void lock(){
    	if( sel != null && ! (sel instanceof LockedInput)){
    		sel = new LockedInput<>(sel);
    	}
    }
    public boolean isLocked() {
    	if( sel != null && sel instanceof UnmodifiableInput) {
    		return true;
    	}
    	return false;
    }
    
    /** If the current field value forced to particular value
     * The value of the field is updated to that value if it is forced.
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isFixed() {
    	if( isLocked()){
    		return true;
    	}
    	
    	if( sel != null && ! optional ) {
    		try {
				return sel.accept(new IsForcedVisitor());
			} catch (Exception e) {
				Logger.getLogger(f.getContext(),getClass()).error("Error checking for fixed input",e);
				return false;
			}
    	}
    	return false;
    	
    }
	/**
	 * Does this field use a particular type of input
	 * 
	 * @param c
	 *            Class of Input to be tested
	 * @return boolean true if matches
	 */
	@SuppressWarnings("unchecked")
	public boolean containsInput(Class<? extends Input> c) {
		Input i = getInput();
		if (c.isAssignableFrom(i.getClass())) {
			return true;
		}
		if (i instanceof MultiInput) {
			return ((MultiInput) i).containsInput(c);
		}
		return false;
	}

	/**
	 * get the Input to be used with this Field
	 * 
	 * @return Input
	 */
	public Input<I> getInput() {
		return sel;
	}
    public void setInput(Input<I> i){
    	String key = getKey();
    	sel=i;
    	if( key != null ){
    	  sel.setKey(key);
    	}
    }
    
	/**
	 * get the key associated with this field
	 * 
	 * @return Object
	 */
	public String getKey() {
		return sel.getKey();
	}

	/**
	 * get the label to be displayed with this field
	 * 
	 * @return String label
	 */
	public String getLabel() {
		if( label != null) {
			return label; // explicit override
		}
		FormTextGenerator gen = f.getFormTextGenerator();
		if( gen != null ) {
			String l = gen.getLabel(getKey());
			if( l != null) {
				return l;
			}
		}
		return getKey();
	}

	/**
	 * get the current value associated with this Field
	 * 
	 * @return value
	 */
	public I getValue() {
		return sel.getValue();
	}

	/**
	 * Set an external validator for the field that augments the validation performed by the input.
	 * 
	 * In most cases it is preferable to extend the {@link Input} to perform additional validation. However 
	 * this can be useful for avoiding code duplication as a {@link FieldValidator} is added by composition
	 * and is preferable to a {@link FormValidator} if the validation only involves a single field because 
	 * the errors will be associated wih that field.
	 * 
	 * @param v
	 *            FieldValidator to set
	 */
	public void addValidator(FieldValidator<I> v) {
		sel.addValidator(v);
	}
	
	public void removeValidator(FieldValidator<I> v){
		sel.removeValidator(v);
	}

	/**
	 * set the value associated with this field
	 * 
	 * @param o
	 *            Value to set
	 * @return previous value
	 * @throws TypeException
	 */
	public I setValue(Object o)  throws TypeException{
		
		return sel.setValue(sel.convert(o));
		
	}
	
	/**
	 * set the label associated with this field
	 * 
	 * @param label
	 *            Label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * validate the contents of this field
	 * 
	 * @throws FieldException
	 */
	public void validate() throws FieldException {
		if( sel.isEmpty()) {
			if( optional) {
				return;
			}
			throw new MissingFieldException();
		}
		sel.validate();
		
	}
	public String getTooltip() {
		if( tooltip != null) {
			return tooltip;
		}
		FormTextGenerator gen = f.getFormTextGenerator();
		if( gen != null) {
			return gen.getFieldHelp(getKey());
		}
		return null;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public Form getForm() {
		return f;
	}
	public boolean isOptional() {
		return optional;
	}
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

}