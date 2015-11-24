// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.exceptions;

public abstract class FieldException extends Exception {

	public FieldException() {
		super();
	}

	public FieldException(String message) {
		super(message);
	}

	public FieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldException(Throwable cause) {
		super(cause);
	}

}