// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** An ErrorInput is an unmodifiale input that never validates.
 * It can used to indicate that no valid selections are possible for the user
 * or that an error occuted while generating the form.
 * 
 * @author spb
 * @param <T> type of input
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ErrorInput.java,v 1.2 2014/09/15 14:30:19 spb Exp $")

public class ErrorInput<T> implements UnmodifiableInput, Input<T> {

	private final String text;
	private String key;
	public ErrorInput(String text){
		this.text=text;
	}
	public String getLabel() {
		return text;
	}
	public T convert(Object v) throws TypeError {
		return null;
	}
	public String getKey() {
		return key;
	}
	public String getPrettyString(T value) {
		return null;
	}
	public String getString(T value) {
		return null;
	}
	public T getValue() {
		return null;
	}
	public void setKey(String key) {
		this.key=key;
	}
	public T setValue(T v) throws TypeError {
		return null;
	}
	public void validate() throws FieldException {
		throw new ValidateException("No legal value possible");
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}

	
}