// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.exceptions;


@uk.ac.ed.epcc.webapp.Version("$Id: MissingFieldException.java,v 1.2 2014/09/15 14:30:17 spb Exp $")


public class MissingFieldException extends FieldException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingFieldException() {
		super();

	}

	public MissingFieldException(String message) {
		super(message);

	}

	public MissingFieldException(String message, Throwable cause) {
		super(message, cause);

	}

	public MissingFieldException(Throwable cause) {
		super(cause);

	}

}