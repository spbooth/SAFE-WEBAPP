// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

/** Exception indicating invalid/inconsistent configuration parameters
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConfigurationException.java,v 1.5 2014/09/15 14:30:29 spb Exp $")

public class ConfigurationException extends Exception {

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message) {
		super(message);
		
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
		
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
		
	}

}