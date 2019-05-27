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

import java.util.LinkedHashSet;
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
    protected LinkedHashSet<AcceptFilter<? super T>> accepts= new LinkedHashSet<>();
    public AndFilter(Class<T> target){
    	super(target);
    }
    public AndFilter(Class<T> target,BaseFilter<? super T> ... fil){
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
	@Override
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
		if( filter instanceof AndFilter) {
			// add component parts to reduce nesting
			AndFilter<? super T> and = (AndFilter<? super T>) filter;
			accepts.addAll(and.accepts);
		}else {
			accepts.add(filter);
		}
	}

	
	
	@Override
	public final <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		if( useBinary(false)){
			// If forced we don't need to worry about the accept filters
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
		return getNarrowingFilter();
	}
	/** get a filter that represents only the SQL portion of this filter.
	 * 
	 * 
	 * 
	 * @return {@link SQLFilter}
	 */
	public SQLFilter<T> getNarrowingFilter()  {
		SQLAndFilter<T> res = new SQLAndFilter<>(getTarget());
		if( isForced()){
			res.addFilter(new GenericBinaryFilter<>(target, getBooleanResult()));
		}else{
			for(PatternFilter<? super T> pat : filters){
				res.addPatternFilter(pat);
			}
		}
		Set<JoinFilter> joins = getJoins();
		if( joins != null ){
			for(JoinFilter join  : joins){
				res.addJoin(join);
			}
		}
		res.addOrder(OrderBy());
		return res;
	}
	/** Make a pure {@link AcceptFilter} version of the current state if 
	 * possible.
	 * 
	 * Passing a valid {@link FilterMatcher} will always return a valid filter 
	 * Passing null will return null if SQL queries are needed for the conversion
	 * 
	 * @param matcher An optional {@link FilterMatcher}
	 * 
	 * @return {@link AcceptFilter} or null if not possible
	 */
	public AcceptFilter<T> getAcceptFilter(FilterMatcher<T> matcher){
		if( isForced()){
			return new BinaryAcceptFilter<>(this);
		}
		if( hasPatternFilters() ){
			if( matcher != null ){
				return new ConvertToAcceptFilter<>(this, matcher);
			}
			return null;
		}
		if( accepts.size() == 1){
			// first filter will do wrapping a single filter in
			// a AndAcceptFilter will inhibit optimisation later
			return (AcceptFilter<T>) accepts.iterator().next();
		}
		return new AndAcceptFilter<>(getTarget(), accepts);
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
		if( ! isForced()) {
			result = prime * result + ((accepts == null) ? 0 : accepts.hashCode());
		}
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
		if( ! isForced()) {
			if (accepts == null) {
				if (other.accepts != null)
					return false;
			} else if (!accepts.equals(other.accepts))
				return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseCombineFilter#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		
		return super.isEmpty() && accepts.isEmpty();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseCombineFilter#listContents(java.lang.StringBuilder)
	 */
	@Override
	protected void listContents(StringBuilder sb) {
		if( ! accepts.isEmpty()) {
			sb.append(" accepts=");
			sb.append(accepts.toString());
		}
		super.listContents(sb);
	}
	@Override
	public Set<BaseFilter> getSet() {
		Set<BaseFilter> set = super.getSet();
		set.addAll(accepts);
		return set;
	}
	
	@Override
	public int size() {
		return super.size() + accepts.size();
	}
}