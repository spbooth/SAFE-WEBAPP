// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/** An UnmodifiableInput that wraps an existing input showing the value of the wrapped input 
 * as Unmodifiable text in the form. 
 * 
 * @author spb
 *
 * @param <V>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: LockedInput.java,v 1.6 2014/09/15 14:30:20 spb Exp $")

public class LockedInput<V> implements UnmodifiableInput ,  Input<V>{
    Input<V> wrapped;
    public LockedInput(Input<V> wrapped_input){
    	wrapped=wrapped_input;
    }
    public Input<V> getNested(){
    	return wrapped;
    }
	public String getLabel() {
		return wrapped.getPrettyString(wrapped.getValue());
	}

	public V convert(Object v) throws TypeError {
		return wrapped.convert(v);
	}

	public String getKey() {
		return wrapped.getKey();
	}

	public String getPrettyString(V value) {
		return wrapped.getPrettyString(value);
	}

	public String getString(V value) {
		return wrapped.getString(value);
	}

	public V getValue() {
		return wrapped.getValue();
	}

	public void setKey(String key) {
		wrapped.setKey(key);
	}

	public V setValue(V v) throws TypeError {
		// We need to be able to set values to populate the current 
		// state in update forms.
		return wrapped.setValue(v);
	}

	public void validate() throws FieldException {
		wrapped.validate();
	}

	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}

}