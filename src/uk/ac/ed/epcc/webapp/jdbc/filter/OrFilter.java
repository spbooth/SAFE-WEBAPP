// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;


/**
 * Combine SQL filters using an OR expression
 * Its only safe to use an OR combination with pure SQL 
 * @author spb
 * @param <T> type of object selected
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: OrFilter.java,v 1.5 2014/09/15 14:30:25 spb Exp $")

public class OrFilter<T> extends BaseSQLCombineFilter<T> {
	
	
	public OrFilter(Class<? super T> target) {
		super(target);
	}
	public OrFilter(Class<? super T> target,SQLFilter<? super T> ...filters ){
		super(target);
		for(SQLFilter<? super T> f : filters){
			addFilter(f);
		}
	}
	@Override
	protected final String getCombiner() {
		return "OR";
	}


	@Override
	protected final String getDefaultPattern() {
		// No OR clauses means no selection
		return "1 != 1";
	}

	
}