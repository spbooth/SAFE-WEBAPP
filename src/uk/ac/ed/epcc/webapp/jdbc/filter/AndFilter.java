// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.LinkedList;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Combining filter that selects the intersection of multiple filters.
 * 
 * This is a {@link PatternFilter} and an {@link AcceptFilter}. 
 * It can combine {@link AcceptFilter} and  {@link PatternFilter}.
 * 
 * @author spb
 * @param <T> Type of object selected
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AndFilter.java,v 1.7 2014/09/15 14:30:24 spb Exp $")

public class AndFilter<T> extends BaseCombineFilter<T> implements PatternFilter<T>, AcceptFilter<T>{
    private LinkedList<AcceptFilter<? super T>> accepts= new LinkedList<AcceptFilter<? super T>>();
    public AndFilter(Class<? super T> target){
    	super(target);
    }
    public AndFilter(Class<? super T> target,BaseFilter<? super T> ... fil){
    	super(target);
    	for(BaseFilter<? super T> f: fil){
    		add(f,true);
    	}
    }
    public boolean hasAcceptFilters(){
    	return ! accepts.isEmpty();
    }
    /** accept based on target object
     * 
     * This method is final to ensure all accept clauses are applied.
     * If an extending class wants to add additional accept clauses
     * they can add an inner class AcceptFiler in the constructor.
     */
	public final boolean  accept(T o) {
		for(AcceptFilter<? super T> acc: accepts){
			if( ! acc.accept(o)){
				return false;
			}
		}
		return true;
	}
	@Override
	protected final void addAccept(AcceptFilter<? super T> filter) throws ConsistencyError {
		accepts.add(filter);
	}
	@Override
	protected final String getCombiner() {
		return "AND";
	}
	@Override
	protected final String getDefaultPattern() {
		return "1=1";
	}
	
	
	
	public final <X> X acceptVisitor(FilterVisitor<X,? extends T> vis) throws Exception {
		return vis.visitAndFilter(this);
	}
	/** Make a pure SQL version of the current state if possible.
	 * @return {@link SQLFilter}
	 * @throws NoSQLFilterException 
	 */
	public SQLFilter<T> getSQLFilter() throws NoSQLFilterException{
		if( hasAcceptFilters()){
			StringBuilder sb = new StringBuilder();
			for(AcceptFilter<? super T>  a : accepts){
				sb.append(" ");
				sb.append(a.toString());
			}
			throw new NoSQLFilterException("Cannot convert to SQL contains AcceptFilter"+sb.toString());
		}
		SQLAndFilter<T> res = new SQLAndFilter<T>(getTarget());
		for(PatternFilter<? super T> pat : filters){
			res.addPatternFilter(pat);
		}
		Set<String> joins = getJoins();
		if( joins != null ){
			for(String join  : joins){
				res.addJoin(join);
			}
		}
		res.addOrder(OrderBy());
		return res;
	}
	
	public final AndFilter<T> addFilter(BaseFilter<? super T> fil){
		return (AndFilter<T>) super.add(fil,true);
	}
}