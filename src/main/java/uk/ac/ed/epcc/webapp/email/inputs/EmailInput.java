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
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/**
 * test input that must be a valid email address
 * 
 * @author spb
 * 
 */


public class EmailInput extends TextInput implements HTML5Input , FormatHintInput{

	/**
	 * 
	 */
	public static final int MAX_EMAIL_LENGTH = 254;
	public EmailInput(){
		super();
		setBoxWidth(32); // 64 is too long for EmailChangeRequest page
		addValidator(new MaxLengthValidator(MAX_EMAIL_LENGTH));

		setSingle(true);
		addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String email) throws FieldException {
				if( email == null || email.trim().length()==0){
					// must be optional
					return;
				}
				if (!Emailer.checkAddress(getString())) {
					throw new ValidateException("Invalid email address");
				}
				
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "email";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput#getFormatHint()
	 */
	@Override
	public String getFormatHint() {
		return "name@example.com";
	}
}