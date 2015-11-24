// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Interface for a SQL based matcher
 * 
 * @author spb
 *
 * @param <T> type of filter
 */
public interface SQLMatcher<T> {
	/** return a SQL filter to implement the select.
	 * @param res Parent Repository for the Filter
	 * @param target   target field
	 * @param form_value  
	 * @return filter 
	 */
	public SQLFilter<T> getSQLFilter(Class<? super T> clazz, Repository res, String target, Object form_value);
}