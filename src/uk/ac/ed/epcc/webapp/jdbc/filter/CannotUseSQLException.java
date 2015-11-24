// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;
/** Exception thrown when a requested operation cannot be performed via SQL
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CannotUseSQLException.java,v 1.2 2014/09/15 14:30:24 spb Exp $")

public class CannotUseSQLException extends Exception {

	public CannotUseSQLException() {
		super();
	}

	public CannotUseSQLException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public CannotUseSQLException(String message) {
		super(message);
		
	}

	public CannotUseSQLException(Throwable cause) {
		super(cause);
		
	}

}