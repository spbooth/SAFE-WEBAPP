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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Base class for Filters than combines multiple SQL filters. 
 * 
 * Only the SQL parts of the filters are considered by this class.
 * 
 * This class assumes it is ok to supress multiple copies of equal filters.
 * 
 * @author spb
 * @param <T> Type of target 
 *
 */
public abstract class BaseCombineFilter<T> extends FilterSet<T> implements PatternFilter<T>, JoinFilter<T> , OrderFilter<T>, BinaryFilter<T>{
	    // Note some filters may appear in both 
		protected LinkedHashSet<PatternFilter> filters;
	    protected LinkedHashSet<OrderClause> order=null;
	    private boolean force_value;
	    public BaseCombineFilter(Class<? super T> target){
	    	super(target);
	    	filters = new LinkedHashSet<PatternFilter>();
	    	force_value=getFilterCombiner().getDefault();
	    }
	    /** A {@link FilterVisitor} that encodes the rules for adding a filter.
	     * The SQL components don't depend on the type of filter so extract these to a super-type
	     * 
	     * @author spb
	     *
	     */
	    protected abstract class AbstractAddFilterVisitor<X> implements FilterVisitor<Boolean,X>{

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
			 */
			public final Boolean visitPatternFilter(PatternFilter<? super X> fil) {
				if( fil instanceof BaseCombineFilter &&
						(((BaseCombineFilter)fil).getFilterCombiner() == getFilterCombiner() || ((BaseCombineFilter)fil).filters.size()==1)
				){
					// This is a filter of the same combination type so merge
					// pattern filters
					// also merge if there is only one inner filter as the type of combination is irrelevant then
		    		// directly to avoid redundant braces and possible duplication
					// of clauses.
		    		BaseCombineFilter<X> comb = (BaseCombineFilter<X>) fil;
		    		for(PatternFilter<? super X> nest : comb.filters){
		    			// call same method recursively in case the contents are also a BaseCombineFilter
		    			visitPatternFilter(nest);
		    		}
		    	}else{
		    		// just add the nested filter
		    		addPatternFilter(fil);
		    	}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
			 */
			public final Boolean visitSQLCombineFilter(BaseSQLCombineFilter<? super X> fil) {
				visitPatternFilter(fil);
				handleOrderClause(fil);
				Set<JoinFilter> joins = fil.getJoins();
				if( joins != null ){
					for(JoinFilter join : joins){
						addJoin(join);
					}
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
			 */
			public final Boolean visitAndFilter(AndFilter<? super X> fil) throws Exception{
				if( ! fil.hasAcceptFilters()){
					// we can convert to SQL best to do this as it
					// allows filter to be added to other SQL filters
					fil.getSQLFilter().acceptVisitor(this);
					return null;
				}
				if(getFilterCombiner() == FilterCombination.AND) {
					// These methods will loop over the inner filters to remove
					// nesting if appropriate (ie combine mechanism is right).
					visitAcceptFilter(fil);
					visitPatternFilter(fil);
				}else {
					if( fil.hasAcceptFilters() && 
							fil.hasPatternFilters()) {
						throw new ConsistencyError("Cannot combine accept/pattern in OR combination");
					}
					// Either one or both of these is empty 
					// so we can add separately
					visitAcceptFilter(fil);
					visitPatternFilter(fil);
				}
				handleOrderClause(fil);
				Set<JoinFilter> joins = fil.getJoins();
				if( joins != null ){
					for(JoinFilter join : joins){
						addJoin(join);
					}
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
			 */
			public final Boolean visitOrderFilter(SQLOrderFilter<? super X> fil) {
			    return handleOrderClause(fil);
			}
			private final Boolean handleOrderClause(OrderFilter<? super X> fil) {
			    addOrder(fil.OrderBy());
				return null;
			}
			

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
			 */
			public final Boolean visitJoinFilter(JoinFilter<? super X> fil) {
				addJoin(fil);
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
			 */
			@Override
			public final Boolean visitOrFilter(OrFilter<? super X> fil) throws Exception {
				if( fil.nonSQL()){
					visitAcceptFilter(fil);
				}else{
					// Add in as SQL by preference
					FilterConverter.convert(fil).acceptVisitor(this);
				}
				return null;
			}


			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
			 */
			@Override
			public final Boolean visitBinaryFilter(BinaryFilter<? super X> fil) throws Exception {
				if( fil.getBooleanResult() != getFilterCombiner().getDefault()){
					force_value=fil.getBooleanResult();
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
			 */
			@Override
			public final Boolean visitDualFilter(DualFilter<? super X> fil) throws Exception {
				return fil.getSQLFilter().acceptVisitor(this);
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
			 */
			@Override
			public final Boolean visitBinaryAcceptFilter(BinaryAcceptFilter<? super X> fil) throws Exception {
				return visitBinaryFilter(fil);
			}
	    	
	    }
	    class AddFilterVisitor extends AbstractAddFilterVisitor<T>{

			

			

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
			 */
			public Boolean visitAcceptFilter(AcceptFilter<? super T> fil) {
				addAccept(fil);
				return null;
			}
	    	
	    }
	    @Override
		protected FilterVisitor getAddVisitor() {
			return new AddFilterVisitor();
		}

		protected final void addOrder(List<OrderClause> new_order) throws ConsistencyError {
			if( new_order == null || new_order.size() == 0 ){
				return;
			}
			if( order == null ){
				order = new LinkedHashSet<OrderClause>();
			}
			order.addAll(new_order);
			
		}
	    
		protected void addPatternFilter(PatternFilter filter) {
			filters.add(filter);
		}

		protected abstract void addAccept(AcceptFilter<? super T> filter) throws ConsistencyError;
		
		Set<JoinFilter> join=null;
		
		protected final void addJoin(JoinFilter add) throws ConsistencyError {
			     // we use a Set to remove  duplicate joins
			     // this will only work where the join strings are identical
			     // but this at least covers the case of multiple dereference
			     // expressions generated automatically
				if( add != null ){
					if( join == null ){
						join = new LinkedHashSet<JoinFilter>(); // order of joins is significant
					}
					join.add(add);
				}
		}

		protected final Set<JoinFilter> getJoins(){
			return join;
		}
		
		public final String getJoin(){
			if( join == null ){
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for( JoinFilter s : join){
				if( sb.length() > 0){
					sb.append(' ');	
				}
				sb.append(s.getJoin());
			}
			return sb.toString();
		}
		public final  List<PatternArgument> getParameters(List<PatternArgument> res) {
			if( isForced()){
				return res;
			}
			for(PatternFilter<? super T> f: filters){
				f.getParameters(res);
			}
			return res;
		}
	   
		public final List<OrderClause>   OrderBy(){
			if( order == null){
				return null;
			}
			return new LinkedList<OrderClause>(order);
		}
		public final StringBuilder addPattern(StringBuilder sb,boolean qualify) {
			if( isForced()){
				sb.append(" ");
				sb.append(Boolean.toString(force_value));
				sb.append(" ");
				return sb;
			}
			
			boolean seen =false;
			
			for(PatternFilter<? super T> f: filters){
				if( seen ){
				   sb.append(" ) ");
				   sb.append(getCombiner());
				}else{
					seen = true;
				}
				sb.append(" ( ");
				f.addPattern(sb, qualify);
			}
			if( seen ){
			  sb.append(" ) ");
			}else{
			  sb.append(getDefaultPattern());
			}
			return sb;
		}
		
		/** get the boolean value of the filter if defined.
		 * If the filter itself does not accept the {@link BinaryFilter} visitor method then
		 * the returned value does not represent the full filter condition.
		 * it is valid when the filter is empty or when forced.
		 */
		@Override
		public boolean getBooleanResult() {
			return force_value;
		}

		protected abstract FilterCombination getFilterCombiner();
		/** Default SQL pattern to use if no filter clauses provided
		 * 
		 * @return String
		 */
		protected final String getDefaultPattern(){
			return Boolean.toString(getFilterCombiner().getDefault());
		}

		/** The SQL combining clause ie. AND or OR
		 * 
		 * @return String
		 */
		protected final String getCombiner(){
			return getFilterCombiner().getCombiner();
		}

		/** has the filter been forced to a fixed value
		 * 
		 * @return
		 */
		protected boolean isForced(){
			return force_value != getFilterCombiner().getDefault();
		}
		/** Is this filter exactly a {@link BinaryFilter}
		 * ie it has a forced value and no joins or order clauses.
		 * 
		 * @return
		 */
		protected boolean useBinary(boolean ignore_order_or_join){
			// If forced/empty value and no joins/order act as binary filter
			return (isEmpty() || isForced())  && (ignore_order_or_join || (order==null && join==null));
		}

		public boolean isEmpty(){
			return filters.isEmpty();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			if( ! isForced()) {
				result = prime * result + ((filters == null) ? 0 : filters.hashCode());
				result = prime * result + ((join == null) ? 0 : join.hashCode());
			}
			result = prime * result + ((order == null) ? 0 : order.hashCode());
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
			BaseCombineFilter other = (BaseCombineFilter) obj;
			if (force_value != other.force_value)
				return false;
			if( ! isForced() && ! other.isForced()) {
				if (filters == null) {
					if (other.filters != null)
						return false;
				} else if (!filters.equals(other.filters))
					return false;

				if (join == null) {
					if (other.join != null)
						return false;
				} else if (!join.equals(other.join))
					return false;
			}
			if (order == null) {
				if (other.order != null)
					return false;
			} else if (!order.equals(other.order))
				return false;
			return true;
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("(");
			listContents(sb);
			sb.append(")");
			return sb.toString();
			
		}

		/** extension point for the {@link #toString()} method
		 * @param sb
		 */
		protected void listContents(StringBuilder sb) {
			if( ! filters.isEmpty()) {
				sb.append(" filters=");
				sb.append(filters.toString());
			}
			if( join != null && ! join.isEmpty()) {
				sb.append(" join=");
				sb.append(join.toString());
			}
			if( order != null && ! order.isEmpty()) {
				sb.append(" order=");
				sb.append(order.toString());
			}
			sb.append(" force=");
			sb.append(force_value);
		}

		@Override
		public Set<BaseFilter> getSet() {
			Set<BaseFilter> sets = new HashSet<>();
			sets.addAll(filters);
			if( join != null) {
				sets.addAll(join);
			}
			return sets;
		}
}