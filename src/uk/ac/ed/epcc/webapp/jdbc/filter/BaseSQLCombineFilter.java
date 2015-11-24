// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;


import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Combining filter that is pure SQL.
 * Any attempt to add an {@link AcceptFilter} will
 * throw an exception.
 * * 
 * @author spb
 * @param <T> type of object selected
 *
 */
public abstract class BaseSQLCombineFilter<T> extends BaseCombineFilter<T> implements
		SQLFilter<T>{


	/**
	 * @param target
	 */
	protected BaseSQLCombineFilter(Class<? super T> target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected final void addAccept(AcceptFilter<? super T> filter) throws ConsistencyError {
		throw new ConsistencyError("Adding AcceptFilter to SQLFilter");
	}
	
	public final BaseSQLCombineFilter<T> addFilter(SQLFilter<? super T> fil){
		return (BaseSQLCombineFilter<T>) super.add(fil,true);
	}
	public final void accept(T o) {

	}
	public final <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitSQLCombineFilter(this);
	}

}