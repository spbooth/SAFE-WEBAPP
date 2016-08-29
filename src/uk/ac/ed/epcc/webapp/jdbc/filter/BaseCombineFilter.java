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
public abstract class BaseCombineFilter<T> extends FilterSet<T> implements PatternFilter<T>, JoinFilter<T> , OrderFilter<T>{
		protected LinkedHashSet<PatternFilter<? super T>> filters;
	    protected LinkedHashSet<OrderClause> order=null;
	    public BaseCombineFilter(Class<? super T> target){
	    	super(target);
	    	filters = new LinkedHashSet<PatternFilter<? super T>>();
	    }
	    /** A {@link FilterVisitor} that encodes the rules for adding a filter.
	     * 
	     * @author spb
	     *
	     */
	    class AddFilterVisitor implements FilterVisitor<Boolean,T>{

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
			 */
			public Boolean visitPatternFilter(PatternFilter<? super T> fil) {
				if( fil instanceof BaseCombineFilter &&
						(((BaseCombineFilter)fil).getFilterCombiner() == getFilterCombiner() || ((BaseCombineFilter)fil).filters.size()==1)
				){
					// This is a filter of the same combination type so merge
					// pattern filters
					// also merge if there is only one inner filter as the type of combination is irrelevant then
		    		// directly to avoid redundant braces and possible duplication
					// of clauses.
		    		BaseCombineFilter<T> comb = (BaseCombineFilter<T>) fil;
		    		for(PatternFilter<? super T> nest : comb.filters){
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
			public Boolean visitSQLCombineFilter(BaseSQLCombineFilter<? super T> fil) {
				visitPatternFilter(fil);
				visitOrderFilter(fil);
				Set<String> joins = fil.getJoins();
				if( joins != null ){
					for(String join : joins){
						addJoin(join);
					}
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
			 */
			public Boolean visitAndFilter(AndFilter<? super T> fil) throws Exception{
				if( ! fil.hasAcceptFilters()){
					// we can convert to SQL best to do this as it
					// allows filter to be added to other SQL filters
					fil.getSQLFilter().acceptVisitor(this);
					return null;
				}
				visitAcceptFilter(fil);
				visitPatternFilter(fil);
				visitOrderFilter(fil);
				Set<String> joins = fil.getJoins();
				if( joins != null ){
					for(String join : joins){
						addJoin(join);
					}
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
			 */
			public Boolean visitOrderFilter(OrderFilter<? super T> fil) {
			    addOrder(fil.OrderBy());
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
			 */
			public Boolean visitAcceptFilter(AcceptFilter<? super T> fil) {
				addAccept(fil);
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
			 */
			public Boolean visitJoinFilter(JoinFilter<? super T> fil) {
				addPatternFilter(fil);
				addJoin(fil.getJoin());
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
			 */
			@Override
			public Boolean visitOrFilter(OrFilter<? super T> fil) throws Exception {
				if( fil.nonSQL()){
					addAccept(fil);
				}else{
					// Add in as SQL by preference
					FilterConverter.convert(fil).acceptVisitor(this);
				}
				return null;
			}

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
			 */
			@Override
			public Boolean visitDualFilter(DualFilter<? super T> fil) throws Exception {
				return visitPatternFilter(fil);
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
	    
		protected void addPatternFilter(PatternFilter<? super T> filter) {
			filters.add(filter);
		}

		protected abstract void addAccept(AcceptFilter<? super T> filter) throws ConsistencyError;
		
		Set<String> join=null;
		
		protected final void addJoin(String add) throws ConsistencyError {
			     // we use a Set to remove  duplicate joins
			     // this will only work where the join strings are identical
			     // but this at least covers the case of multiple dereference
			     // expressions generated automatically
				if( add != null && add.trim().length() > 0){
					if( join == null ){
						join = new LinkedHashSet<String>(); // order of joins is significant
					}
					// trim is important as getJoin might add spaces in nested
					join.add(add.trim());
				}
		}

		protected final Set<String> getJoins(){
			return join;
		}
		
		public final String getJoin(){
			if( join == null ){
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for( String s : join){
				if( sb.length() > 0){
					sb.append(' ');	
				}
				sb.append(s);
			}
			return sb.toString();
		}
		public final  List<PatternArgument> getParameters(List<PatternArgument> res) {
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
		
		protected abstract FilterCombination getFilterCombiner();
		/** Default SQL pattern to use if no filter clauses provided
		 * 
		 * @return String
		 */
		protected final String getDefaultPattern(){
			return getFilterCombiner().getDefault();
		}

		/** The SQL combining clause ie. AND or OR
		 * 
		 * @return String
		 */
		protected final String getCombiner(){
			return getFilterCombiner().getCombiner();
		}

}