// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.List;

/** A {@link SQLFilter} that also requests a particular order for the results.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface OrderFilter<T> extends BaseSQLFilter<T> {
	/**
	 * override the ORDER BY clause of the factory return null to use Factory
	 * default
	 * 
	 * @return List<OrderClause>
	 */
	public List<OrderClause> OrderBy();
	
}