// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;


/**
 * SQLFilter is a marker interface Filters where <i>ALL</i> the selection is
 * performed in SQL only.
 * 
 * @author spb
 * @param <T> type of object selected
 * 
 */
public interface SQLFilter<T> extends BaseSQLFilter<T> {
	/**
	 * This method only exists to ensure that types implementing this
	 * interface do NOT implement AcceptFilter
	 * 
	 * @param o
	 */
	public void accept(T o);
}