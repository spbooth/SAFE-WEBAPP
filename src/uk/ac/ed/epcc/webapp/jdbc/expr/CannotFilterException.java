// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;
/** Exception thrown when the requested Filter cannot be generated.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CannotFilterException.java,v 1.3 2014/09/15 14:30:23 spb Exp $")

public class CannotFilterException extends Exception {

	public CannotFilterException() {
		super();
	}

	public CannotFilterException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public CannotFilterException(String message) {
		super(message);
		
	}

	public CannotFilterException(Throwable cause) {
		super(cause);
		
	}

}