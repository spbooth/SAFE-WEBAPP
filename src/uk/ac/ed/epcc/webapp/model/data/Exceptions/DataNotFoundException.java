// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * Exception thrown when a query Fails to find a matching BasicDataObject
 * 
 * @author spb
 * 
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DataNotFoundException.java,v 1.7 2014/09/15 14:30:30 spb Exp $")

public class DataNotFoundException extends DataException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param str
	 */
	public DataNotFoundException(String str) {
		super(str);
	}

}