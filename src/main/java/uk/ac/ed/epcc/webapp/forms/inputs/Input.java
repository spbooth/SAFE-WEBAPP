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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/**
 * Input represents a input box/pulldown etc for a form. It has internal state
 * holding the Object representing its current value.
 * A Input is used to get the Value of an Object from the user. 
 * 
 * Each Input also has an identifying key that should be unique within the form. This can
 * be used to produce parameter names etc. It is arguable that this is unnecessary because inputs are usually stored
 * indexed by their key so the key is usually available anyway.
 * 
 * The primary role of an Input is to customise the user interface. Constraints on permitted values should
 * normally be encoded in an embedded {@link FieldValidator}. Value constraints are frequently a higher level concern
 * and may need to be specified independently of the user interface. However the user interface needs to be aware of them (for example 
 * we want the browser to be able to validate what it can before form submission) and to introduce its own constraints (for example a 
 * multi-stage form where an earlier stage constrains permitted values later on).
 * 
 * 
 * 
 * Inputs may be composite, for example a date selector constructed from
 * pull-downs for year, month and day.
 * 
 * The Input classes are kept generic to all different types of Form. However
 * our primary interface is html and some html specific  enhancements can be included 
 * by implementing specific interfaces.
 * The Form
 * class has to implement the different types of edit operation depending on the
 * kind of selector. Having general input types allows us to use inheritance between different types
 * of Input. If we sub-classed Inputs by Form type each new Input type would need
 * to be sub-classed for every Form type. Where we subclass an Input by type
 * there is a reasonable chance that the inherited edit code will be sufficient
 * and not need re-implementing For example most Inputs can share text-box edit
 * code from a common superclass.
 * 
 * @author spb
 * @param <V> class of object Input generates
 * 
 */
public interface Input<V> {

	/**
	 * get the unique key for this selector
	 * 
	 * @return Object key for this Selector
	 */
	public abstract String getKey();

	/**
	 * get the current value of the Object created by this selector.
	 * 
	 * @return Object value of this selector
	 */
	public abstract V getValue();

	/**
	 * Set the key used by this Selector
	 * 
	 * @param key
	 */
	public abstract void setKey(String key);

	/**
	 * set the value of this selector. This is either called by the Form to
	 * specify a default value or by the edit methods in the sub-class
	 * implementation. 
	 * 
	 * 
	 * @param v
	 * @return previous value Object
	 * @throws TypeException 
	 */
	public abstract V setValue(V v) throws  TypeException;
	
	/** Set to a null value
	 * never throws exceptions
	 * 
	 */
	public abstract void setNull();
	/** Perform any supported type conversions to to generate a value of the
	 * target type. This does not check the converted value will pass validation.
	 * 
	 * @param v Object input
	 * @return target type
	 * @throws TypeException
	 */
	public default V convert(Object v) throws TypeException{
		return (V) v;
	}
	/** convert a value of the correct type for this input into a String.
	 * If this input is a parseInput this must be compatible
	 * with the way the input is parsed. To make testing easier equivalent values should convert 
	 * to the same canonical form.
	 * @see ParseInput
	 * @param value
	 * @return String or null if value is null
	 */
	public abstract String getString(V value);
    /** Convert a value into a user friendly string. This usually defaults to the same 
     * as getString but can be overridden to produce nicer output 
     * @see ListInput
     * @param value
     * @return String
     */
	default public String getPrettyString(V value) {
		return getString(value);
	}

	/**
	 * Check the validity of the current state of the Object.
	 * 
	 * 
	 * @throws FieldException
	 */
	public abstract void validate() throws FieldException;
	
	/**
	 * Check the validity of a prospective value
	 * 
	 * 
	 * @throws FieldException
	 */
	public abstract void validate(V value) throws FieldException;
	
	public abstract <R> R accept(InputVisitor<R> vis) throws Exception;

	/**Is the input empty.
	 * 
	 * empty inputs are reported as errors for non-optional fields.
	 * Additional validation is not performed if the input is empty.
	 * 
	 * @return
	 */
	default public  boolean isEmpty() {
		return getValue() == null;
	}
	/** Add a {@link FieldValidator} to this input
	 * 
	 * @param val
	 */
	public void addValidator(FieldValidator<V> val) ;
	
	/** Add a {@link FieldValidationSet} to this input
	 * 
	 * @param set
	 */
	public void addValidatorSet(FieldValidationSet<V> set);
	/** Remove a {@link FieldValidator} from this input
	 * 
	 * @param val
	 */
	public void removeValidator(FieldValidator<V> val);
	
	/** Get the current set of Validators.
	 * 
	 * @return
	 */
	public FieldValidationSet<V> getValidators();
}