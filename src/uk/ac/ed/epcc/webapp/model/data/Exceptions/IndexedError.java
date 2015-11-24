// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

/** Illegal index value
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: IndexedError.java,v 1.5 2014/09/15 14:30:30 spb Exp $")

public class IndexedError extends Error {

	

	public IndexedError(String message) {
		super(message);
	}

	public IndexedError(Throwable cause) {
		super(cause);
	}

	public IndexedError(String message, Throwable cause) {
		super(message, cause);
	}

}