// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * Exception thrown when a query finds multiple results in a context where on a
 * single result is expected
 * 
 * @author spb
 * 
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MultipleResultException.java,v 1.7 2014/09/15 14:30:30 spb Exp $")

public class MultipleResultException extends DataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param str
	 */
	public MultipleResultException(String str) {
		super(str);

	}

}