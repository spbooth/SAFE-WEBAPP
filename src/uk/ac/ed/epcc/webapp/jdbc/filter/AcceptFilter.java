// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;


/**
 * A Filter that accepts objects based on a Java method
 * 
 * @author spb
 * @param <T> type of object selected
 * 
 */
public interface AcceptFilter<T> extends BaseFilter<T> {
	/**
	 * does this object match the filter
	 * 
	 * @param o
	 *            Object to ecaluate
	 * @return boolean
	 */
	public boolean accept(T o);
}