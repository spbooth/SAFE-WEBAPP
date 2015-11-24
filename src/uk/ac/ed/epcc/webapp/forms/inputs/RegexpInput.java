// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
@uk.ac.ed.epcc.webapp.Version("$Id: RegexpInput.java,v 1.3 2014/09/15 14:30:20 spb Exp $")

/** Input for a regular expression pattern.
 * 
 * @author spb
 *
 */
public class RegexpInput extends TextInput implements TagInput{

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.TextInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		String s = getValue();
		if (s != null) {
			try {
				Pattern.compile(s);
			} catch (PatternSyntaxException e) {
				throw new ParseException("Invalid regular expression");
			}
		}
	}

	public String getTag() {
		return "(Regular expression)";
	}

}