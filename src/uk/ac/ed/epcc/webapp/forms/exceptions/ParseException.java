// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.exceptions;


@uk.ac.ed.epcc.webapp.Version("$Id: ParseException.java,v 1.3 2015/10/26 10:06:58 spb Exp $")

/** A {@link  FieldException} indicating a problem with parsing the input.
 * 
 * @author spb
 *
 */
public class ParseException extends FieldException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseException() {
		super();

	}

	public ParseException(String message) {
		super(message);

	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);

	}

	public ParseException(Throwable cause) {
		super(cause);

	}

}