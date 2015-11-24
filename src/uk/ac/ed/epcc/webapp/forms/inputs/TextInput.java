// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

@uk.ac.ed.epcc.webapp.Version("$Id: TextInput.java,v 1.5 2015/01/20 16:55:46 spb Exp $")


public class TextInput extends ParseAbstractInput<String> {

	private boolean allow_null;
	private boolean trim=true;
	public TextInput() {
		this(false);
	}

	public TextInput(boolean allow_null) {
		super();
		this.allow_null = allow_null;
	}

	public final void parse(String v) throws ParseException {
		if (v == null || v.length() == 0) {
			if (allow_null) {
				setValue(null);
			} else {
				setValue("");
			}
		} else {
			if( force_single ){
				v = v.replace("\n", "");
			}
			if( getTrim()){
				v = v.trim();
			}
			setValue(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate()
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		
			Object v = getValue();
			if (v != null && !(v instanceof String)) {
				throw new ValidateException("Invalid input type in TextInput "+v.getClass().getCanonicalName());
			}
			String s = (String) v;
			if (s != null && s.length() > getMaxResultLength() && getMaxResultLength() > 0) {
				throw new ValidateException("Input too long");
			}
			if (s == null || s.trim().length() == 0) {
				// empty string counts as missing
				if (!isOptional()) {
					throw new MissingFieldException(getKey() + " missing");
				}
			}
		
	}

	public boolean getTrim() {
		return trim;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

}