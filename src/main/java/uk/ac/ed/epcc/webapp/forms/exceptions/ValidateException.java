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
package uk.ac.ed.epcc.webapp.forms.exceptions;

import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;


/**
 * Exception to indicate some kind of problem with form validation. The
 * message text should be for the End user.
 * This can either be thrown from a {@link FieldValidator} or a {@link FormValidator}.
 * If thrown from a {@link FormValidator} the error is a generic error unless
 * the field in specified explicitly in the exception.
 * 
 * 
 * 
 * If it is more convenient to perform validation as part of the {@link FormAction}
 * then you can throw a {@link TransitionValidationException} there instead.
 * 
 */


public class ValidateException extends FieldException {

	private String field=null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidateException() {
		super();

	}

	public ValidateException(String message) {
		super(message);

	}
	public ValidateException(String field, String message) {
		super(message);
		this.field=field;
	}

	public ValidateException(String message, Throwable cause) {
		super(message, cause);

	}
	public ValidateException(String field,String message, Throwable cause) {
		super(message, cause);
		this.field=field;
	}
	public ValidateException(Throwable cause) {
		super(cause);

	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

}