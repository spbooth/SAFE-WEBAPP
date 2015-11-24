// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.jfreechart;
@uk.ac.ed.epcc.webapp.Version("$Id: UnsupportedOperation.java,v 1.8 2014/09/15 14:30:13 spb Exp $")


public class UnsupportedOperation extends Exception {

	public UnsupportedOperation() {
	}

	public UnsupportedOperation(String message) {
		super(message);
	}

	public UnsupportedOperation(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedOperation(Throwable cause) {
		super(cause);
	}

}