// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/** Represents an UnmodifiableInput that takes no part in the
 * form validation it just displays informational text.
 * Similar to ConstantInput but cannot cache a value and always validates.
 *  
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: InfoInput.java,v 1.2 2014/09/15 14:30:19 spb Exp $")

public class InfoInput implements Input<String>, UnmodifiableInput{
    private final String label;
    private String key;
    public InfoInput(String text){
    	label=text;
    }
	public String convert(Object v) throws TypeError {
		return null;
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(String value) {
		return value;
	}

	public String getString(String value) {
		return value;
	}

	public String getValue() {
		return label;
	}

	public void setKey(String key) {
		this.key=key;
	}

	public String setValue(String v) throws TypeError {
		return label;
	}

	public void validate() throws FieldException {
		return;
	}

	public String getLabel() {
		return label;
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}

}