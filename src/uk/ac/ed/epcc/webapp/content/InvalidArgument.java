// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/*
 * Created on 22-Jan-2004
 *
 */
package uk.ac.ed.epcc.webapp.content;


/**
 * Invalid input exception for the SAF
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: InvalidArgument.java,v 1.2 2014/09/15 14:30:14 spb Exp $")

public class InvalidArgument extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param string
	 */
	public InvalidArgument(String string) {

		super(string);
	}

}