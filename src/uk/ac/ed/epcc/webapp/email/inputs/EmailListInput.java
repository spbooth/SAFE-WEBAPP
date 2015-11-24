// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email.inputs;

import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
/** Input for a list of email addresses.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: EmailListInput.java,v 1.2 2014/09/15 14:30:16 spb Exp $")

public class EmailListInput extends TextInput {
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
		if (!Emailer.checkAddressList(getString())) {
			throw new ValidateException("Expecting comma seperated email addresses");
		}
	}
}