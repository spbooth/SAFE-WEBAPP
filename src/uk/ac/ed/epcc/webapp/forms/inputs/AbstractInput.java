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

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/**
 * Superclass for general non-composite Inputs
 * 
 * 
 * 
 * @author spb
 * @param <V> Param of object we generate
 * 
 */
public abstract class AbstractInput<V> implements Input<V>{
	String key;

	V value;

	private Set<FieldValidator<V>> validators;


	public AbstractInput() {
		super();
		value = null;
		validators=new LinkedHashSet<>();
	}
	
	public final void addValidator(FieldValidator<V> val) {
		validators.add(val);
	}

	public final void removeValidator(FieldValidator<V> val) {
		validators.remove(val);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getKey()
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * get a String representation of the value in a form that is compatible
	 * with the way the input is parsed.
	 * This provides a default implementation of a method requires by ParseInput
	 * @return String or null if no value
	 */
	public final String getString() {
	    if( value == null ){
	    	return null;
	    }
		return getString(value);
	}
	/** get a String representation of an Object that is compatible with the way
	 * the input is parsed
	 * 
	 * @param val
	 * @return String or null if val is null
	 */
    public String getString(V val){
    	if( val == null ){
    		return null;
    	}
    	return val.toString();
    }
    
    public String getPrettyString(V val){
    	if( val == null ){
    		return "no value";
    	}
    	return getString(val);
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getValue()
	 */
	public final V getValue() {
		return value;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#setKey(java.lang.Object)
	 */
	public final void setKey(String key) {
		this.key = key;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#setValue(java.lang.Object)
	 */
	public final V setValue(V v) throws TypeError{
		V old = value;
		// in case we are called without generic checking but have a convert method.
		value = convert(v);
		return old;
	}
	
	@SuppressWarnings("unchecked")
	public V convert(Object v) throws TypeError{
		return (V) v;
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#validate()
	 */
	public final void validate() throws FieldException {
		V value = getValue();
		if( value == null ) {
			return;
		}
		for(FieldValidator<V> val : validators) {
			val.validate(value);
		}
	}
	
	

}