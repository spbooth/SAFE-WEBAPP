// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email.inputs;

import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;

/**
 * test input that must be a valid email address
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: EmailInput.java,v 1.5 2015/04/15 10:50:41 spb Exp $")

public class EmailInput extends TextInput implements HTML5Input {

	/**
	 * 
	 */
	public static final int MAX_EMAIL_LENGTH = 254;
	public EmailInput(){
		super();
		setBoxWidth(64);
		setMaxResultLength(MAX_EMAIL_LENGTH);
		setSingle(true);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.TextInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		
		String email = getString();
		if( email == null || email.trim().length()==0){
			// must be optional
			return;
		}
		if (!Emailer.checkAddress(getString())) {
			throw new ValidateException("Invalid email address");
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "email";
	}

}