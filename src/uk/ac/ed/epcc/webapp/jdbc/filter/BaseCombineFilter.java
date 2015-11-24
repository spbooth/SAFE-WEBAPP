// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Base class for Filters than combines multiple filters. 
 * 
 * This class assumes it is ok to supress multiple copies of equal filters.
 * 
 * @author spb
 * @param <T> Type of target 
 *
 */
public abstract class BaseCombineFilter<T> implements PatternFilter<T>, JoinFilter<T> , OrderFilter<T>{
		private Class<? super T> target;
		protected LinkedHashSet<PatternFilter<? super T>> filters;
	    protected LinkedHashSet<OrderClause> order=null;
	    public BaseCombineFilter(Class<? super T> target){
	    	this.target=target;
	    	filters = new LinkedHashSet<PatternFilter<? super T>>();
	    }
	    /** A {@link FilterVisitor} that encodes the rules for adding a filter.
	     * 
	     * @author spb
	     *
	     */
	    private class AddFilterVisitor implements FilterVisitor<Boolean,T>{

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
			 */
			public Boolean visitPatternFilter(PatternFilter<? super T> fil) {
				if( BaseCombineFilter.this.getClass().isAssignableFrom(fil.getClass())){
		    		// This is a filter of the same type or super type so merge
					// pattern filters
		    		// directly to avoid redundant braces and possible duplication
					// of clauses.
		    		BaseCombineFilter<T> comb = (BaseCombineFilter<T>) fil;
		    		for(PatternFilter<? super T> nest : comb.filters){
		    			addPatternFilter(nest);
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
	    	
	    }
	    protected final BaseCombineFilter<T> add(BaseFilter<? super T> fil,boolean check_types){
	    	
	    	if( fil == null || fil==this){
	    		return this;
	    	}
	    	if( check_types){
	    		if( target == null ){
	    			target=fil.getTarget();
	    		}else{
	    			// Its OK to add a super-type filter to a more specific filter but
	    			// not the other way round.
	    			Class target2 = fil.getTarget();
	    			if( target2 != null && target != null && ! target2.isAssignableFrom(target)){
	    				if( target.isAssignableFrom(target2)){
	    					// adding more restricive target
	    					target=target2;
	    				}else{
	    					//TODO check this always but run as assertion for a bit.
	    					assert(false);
	    					//throw new ConsistencyError("Incompatible filter types "+target2.getCanonicalName()+","+target.getCanonicalName());
	    				}
	    			}
	    		}
	    	}
	    	try {
				fil.acceptVisitor(new AddFilterVisitor());
			} catch (Exception e) {
				throw new ConsistencyError("Fatal filter combine error",e);
			}
	    	return this;
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
		@SuppressWarnings("unchecked")
		public final  List<PatternArgument> getParameters(List<PatternArgument> res) {
			for(BaseFilter f: filters){
				if( f instanceof PatternFilter){
					((PatternFilter)f).getParameters(res);
				}
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
		/** Default SQL pattern to use if no filter clauses provided
		 * 
		 * @return String
		 */
		protected abstract String getDefaultPattern();

		/** The SQL combining clause ie. AND or OR
		 * 
		 * @return String
		 */
		protected abstract String getCombiner();
		
		public Class<? super T> getTarget(){
			return target;
		}

}