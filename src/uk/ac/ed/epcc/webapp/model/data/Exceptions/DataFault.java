// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * Exception wrapping a fail from the underlying database
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DataFault.java,v 1.8 2014/09/15 14:30:29 spb Exp $")

public class DataFault extends DataException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataFault(String string) {
		super(string);
	}

	public DataFault(String str, Throwable cause) {
		super(str+cause.getMessage(), cause);
	}
}