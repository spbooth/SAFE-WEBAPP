// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;
/** A Data error similar to a DataException but this is taken to be
 * a non recoverable error.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DataError.java,v 1.5 2014/09/15 14:30:29 spb Exp $")

public class DataError extends Error {

	
	public DataError(String message) {
		super(message);
		
	}

	public DataError(Throwable cause) {
		super(cause);
		
	}

	public DataError(String message, Throwable cause) {
		super(message, cause);
		
	}

}