// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;


/** SQL only version of AndFilter 
 * It can combine {@link PatternFilter}s.
 * Any attempt to add an {@link AcceptFilter} will
 * throw an exception.
 * 
 * @author spb
 * @param <T> type of object selected
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SQLAndFilter.java,v 1.5 2014/09/15 14:30:25 spb Exp $")

public class SQLAndFilter<T> extends BaseSQLCombineFilter<T>  {

	public SQLAndFilter(Class<? super T>target){
		super(target);
	}
	
	public SQLAndFilter(Class<? super T>target,SQLFilter<? super T> ...filters ){
		super(target);
		for(SQLFilter<? super T> f : filters){
			addFilter(f);
		}
	}
	
	@Override
	protected final String getCombiner() {
		return "AND";
	}

	@Override
	protected final String getDefaultPattern() {
		return "1=1";
	}

	
	

}