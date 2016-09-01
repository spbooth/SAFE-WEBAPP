//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.LinkedList;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Combining filter that selects the intersection of multiple filters.
 * 
 * This is a {@link PatternFilter}, {@link BinaryFilter} and an {@link AcceptFilter} though it has its own
 * target method in {@link FilterVisitor}. 
 * It can combine {@link AcceptFilter}, {@link BinaryFilter} and  {@link PatternFilter}.
 * 
 * @author spb
 * @param <T> Type of object selected
 *
 */


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
    public boolean hasPatternFilters(){
    	return ! filters.isEmpty();
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

	
	
	public final <X> X acceptVisitor(FilterVisitor<X,? extends T> vis) throws Exception {
		if( useBinary()){
			// If forced we don't need to wory about the accept filters
			return vis.visitBinaryFilter(this);
		}
		return vis.visitAndFilter(this);
	}
	/** Make a pure SQL version of the current state if possible.
	 * @return {@link SQLFilter}
	 * @throws NoSQLFilterException 
	 */
	public SQLFilter<T> getSQLFilter() throws NoSQLFilterException{
		if( hasAcceptFilters() && ! isForced()){
			// does not matter if we have added accept to a forced filter we know the value
			StringBuilder sb = new StringBuilder();
			for(AcceptFilter<? super T>  a : accepts){
				sb.append(" ");
				sb.append(a.toString());
			}
			throw new NoSQLFilterException("Cannot convert to SQL contains AcceptFilter"+sb.toString());
		}
		SQLAndFilter<T> res = new SQLAndFilter<T>(getTarget());
		if( isForced()){
			res.addFilter(new GenericBinaryFilter<T>(target, getBooleanResult()));
		}else{
			for(PatternFilter<? super T> pat : filters){
				res.addPatternFilter(pat);
			}
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
	/** Make a pure {@link AcceptFilter} version of the current state if possible
	 * 
	 * @return {@link AcceptFilter} or null if not possible
	 */
	public AcceptFilter<T> getAcceptFilter(){
		if( isForced()){
			return new BinaryAcceptFilter<T>(this);
		}
		if( hasPatternFilters() ){
			return null;
		}
		return new AndAcceptFilter<T>(getTarget(), accepts);
	}
	public final AndFilter<T> addFilter(BaseFilter<? super T> fil){
		return (AndFilter<T>) super.add(fil,true);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseCombineFilter#getFilterCombiner()
	 */
	@Override
	protected final FilterCombination getFilterCombiner() {
		return FilterCombination.AND;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accepts == null) ? 0 : accepts.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AndFilter other = (AndFilter) obj;
		if (accepts == null) {
			if (other.accepts != null)
				return false;
		} else if (!accepts.equals(other.accepts))
			return false;
		return true;
	}
}