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
package uk.ac.ed.epcc.webapp.email.inputs;

import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.MultipleInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
/** Input for a list of email addresses.
 * 
 * @author spb
 *
 */


public class EmailListInput extends TextInput implements MultipleInput {
	/**
	 * 
	 */
	public EmailListInput() {
		super();
		setSingle(true); // can't be a textarea and an html5 email input
	}

	/**
	 * @param allow_null
	 */
	public EmailListInput(boolean allow_null) {
		super(allow_null);
		setSingle(true); // can't be a textarea and an html5 email input
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
		if (!Emailer.checkAddressList(getString())) {
			throw new ValidateException("Expecting comma seperated email addresses");
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	@Override
	public String getType() {
		return "email";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultipleInput#isMultiple()
	 */
	@Override
	public boolean isMultiple() {
		return true;
	}
}