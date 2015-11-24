// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.exceptions;

import uk.ac.ed.epcc.webapp.forms.action.FormAction;


/**
 * Exception to indicate some kind of problem with the input to this field. The
 * message text should be for the End user.
 * 
 * If it is more convenient to perform validation as part of the {@link FormAction}
 * then you can throw a {@link TransitionValidationException} there instead.
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ValidateException.java,v 1.3 2015/02/11 10:57:37 spb Exp $")

public class ValidateException extends FieldException {

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

	public ValidateException(String message, Throwable cause) {
		super(message, cause);

	}

	public ValidateException(Throwable cause) {
		super(cause);

	}

}