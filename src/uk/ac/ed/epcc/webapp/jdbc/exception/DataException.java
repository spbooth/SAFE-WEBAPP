// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.exception;

/**
 * superclass of the Data exceptions thrown by DataObject
 * 
 * @author spb
 * 
 */
public class DataException extends Exception {
	public DataException(String str) {
		super(str);
	}

	public DataException(String str, Throwable cause) {
		super(str, cause);
	}
}