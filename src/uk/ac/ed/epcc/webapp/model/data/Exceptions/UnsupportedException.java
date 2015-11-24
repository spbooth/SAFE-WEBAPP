// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

/** Varient of DataException thrown to indicate an unsupported operation 
 * in the data layer
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: UnsupportedException.java,v 1.7 2014/09/15 14:30:30 spb Exp $")

public class UnsupportedException extends DataFault {

	public UnsupportedException(String str) {
		super(str);
	}

	public UnsupportedException(String str, Throwable cause) {
		super(str, cause);
	}

}