// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/**
 * extended version of Input that supports parsing a String represenatation of
 * the input
 * 
 * @author spb
 * @param <T> type of input
 * 
 */
public interface ParseInput<T> extends Input<T> {
	/**
	 * get a String representation of the value in a form that is compatible
	 * with the way the input is parsed.
	 * @return String or null if no value
	 */
	public abstract String getString();

	
	/**
	 * Set the value of the input by parsing a textual representation of the
	 * input.
	 * 
	 * @param v
	 * @throws ParseException
	 */
	public abstract void parse(String v) throws ParseException;
}