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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;

/**
 * text input that must be a valid email address
 * 
 * @author spb
 * 
 */


public class EmailInput extends TextInput {

	private static final int DEFAULT_BOX_WIDTH = 32;
	/**
	 * 
	 */
	public static final int MAX_EMAIL_LENGTH = 254;
	/**
	 * property to set the email input box width
	 * 
	 */
	public static final String EMAIL_MAXWIDTH_PROP = "email.maxwidth";
	public EmailInput(){
		super();
		setBoxWidth(DEFAULT_BOX_WIDTH); 
		addValidator(new MaxLengthValidator(MAX_EMAIL_LENGTH));

		setSingle(true);
		addValidator(new EmailFieldValidator());
	}
	public static int defaultBoxWidth(AppContext conn) {
		return conn.getIntegerParameter(EMAIL_MAXWIDTH_PROP, DEFAULT_BOX_WIDTH);
	}
	
	
	
}