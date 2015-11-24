// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;

/**
 * Superclass for general non-composite Inputs
 * 
 * 
 * 
 * @author spb
 * @param <V> Param of object we generate
 * 
 */
public abstract class AbstractInput<V> implements Input<V>, OptionalInput {
	String key;

	V value;

	boolean optional;

	public AbstractInput() {
		super();
		value = null;
		optional = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getKey()
	 */
	public String getKey() {
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
	public V getValue() {
		return value;
	}

	/**
	 * Is it an error not to specify this Input
	 * 
	 * @return boolean true if optional
	 * 
	 */
	public  boolean isOptional() {
		return optional;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#setKey(java.lang.Object)
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Mark this input as optional or not
	 * 
	 * @param opt
	 *            boolean
	 */
	public  void setOptional(boolean opt) {
		optional = opt;
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
	public void validate() throws FieldException {
		if (value == null && !optional) {
			throw new MissingFieldException(getKey() + " missing");
		}
	}

}