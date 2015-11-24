// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.exceptions;

/**
 * ConsistencyException thrown when a subclass fails a consistency check.
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConsistencyError.java,v 1.2 2014/09/15 14:30:17 spb Exp $")

public class ConsistencyError extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConsistencyError(String str) {
		super(str);
	}

	public ConsistencyError(String string, Throwable e) {
		super(string,e);
	}
}