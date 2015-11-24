// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;

/** a read-only input that cannot be modified. For convenience in update forms it can cache an
 * object value but this should never actually be edited by the input instead the text label should be 
 * displayed. This input only validates against optional input
 * @param <V> type of value
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConstantInput.java,v 1.5 2014/09/15 14:30:19 spb Exp $")

public class ConstantInput<V> implements UnmodifiableInput, Input<V>,  OptionalInput {
	private String label;

	private String key;

	private V value = null;

	private  boolean optional = false;

	public ConstantInput(String label){
		this(label,null);
	}
	public ConstantInput(String label,V value) {
		this.label = label;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getString(V val) {
		if (val == null) {
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
    public final String getString(){
    	return getString(value);
    }
	public V getValue() {
		return value;
	}

	/**
	 * Is it an error not to specify this Input
	 * 
	 * @return boolean true if optional
	 * 
	 */
	public boolean isOptional() {
		return optional;
	}

	

	public void setKey(String key) {
		this.key = key;

	}

	public void setLabel(String s) {
		label = s;
	}

	/**
	 * Mark this input as optional or not
	 * 
	 * @param opt
	 *            boolean
	 */
	public void setOptional(boolean opt) {
		optional = opt;
	}

	@SuppressWarnings("unchecked")
	public V setValue(Object v) throws TypeError{
		V old = value;
		value = (V) v;
		return old;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate(boolean)
	 */
	public void validate() throws FieldException {
		boolean ok = value != null || optional;
		if (ok) {
			return;
		}
		throw new MissingFieldException(getKey() + " missing");
	}

	@SuppressWarnings("unchecked")
	public V convert(Object v) throws TypeError {
		return(V) v;
	}

	
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}

}