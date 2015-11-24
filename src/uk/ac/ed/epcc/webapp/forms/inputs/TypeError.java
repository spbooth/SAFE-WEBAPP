// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;
/** Error thrown if the value passed to an input has no defined 
 * convertion to the expected type.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TypeError.java,v 1.2 2014/09/15 14:30:21 spb Exp $")

public class TypeError extends Error {

	public TypeError() {
	}

	public TypeError(String message) {
		super(message);
	}
	public TypeError(Class t){
		super("Cannot convert type "+t.getCanonicalName());
	}

	public TypeError(Throwable cause) {
		super(cause);
	}

	public TypeError(String message, Throwable cause) {
		super(message, cause);
	}

}