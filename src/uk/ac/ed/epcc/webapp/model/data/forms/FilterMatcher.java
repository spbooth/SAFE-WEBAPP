// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/

package uk.ac.ed.epcc.webapp.model.data.forms;


/** interface for classes that fine tune the matching in FormFilters
 * @param <T> Type of object being matched
 * 
 */
public interface FilterMatcher<T> extends Matcher<T>, SQLMatcher<T>{
	
	
	
}