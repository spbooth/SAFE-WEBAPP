//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/** A generic combining filter that can combine both {@link BaseSQLFilter}s and {@link AcceptFilter}s
 * in an OR combination.
 * 
 * To do this it has to convert everything into {@link AcceptFilter}s using separate SQL queries for each
 * component {@link BaseSQLFilter}. Though pure {@link SQLFilter}s can be combined as a {@link SQLOrFilter} and
 * checked as a single query. This can make this kind of filter very expensive and prevents the use of joins etc.
 * These filters should be avoided in performance critical queries like report generation 
 *  however they are still useful as a way of simplifying complex combinational logic that would be just as 
 * inefficient if written by hand. The actual accept/reject operation is implemented by a nested {@link FilterMatcher}
 * (usually the factory class).
 * 
 * 
 * @author spb
 *
 */
public final class OrFilter<T> extends FilterSet<T> implements AcceptFilter<T>, BinaryFilter<T> {
	/**
	 * @param target
	 */
	public OrFilter(Class<T> target, FilterMatcher<T> matcher) {
		super(target);
		this.matcher=matcher;
		sql_filters=new SQLOrFilter<>(target);
		pure_accept_filters = new LinkedHashSet<>();
		mixed_filters = new LinkedHashSet<>();
		dual_filters = new LinkedHashSet<>();
	}

	
	private final FilterMatcher<T> matcher;
	private SQLOrFilter<T> sql_filters;
	private LinkedHashSet<AcceptFilter<? super T>> pure_accept_filters;
	private LinkedHashSet<BaseFilter<? super T>> mixed_filters;
	private LinkedHashSet<DualFilter<? super T>> dual_filters;
	private boolean force_value=false;
	private class AddFilterVisitor implements FilterVisitor<Boolean, T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
		 */
		@Override
		public Boolean visitPatternFilter(PatternFilter<T> fil) throws Exception {
			sql_filters.addPatternFilter(fil);;
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
		 */
		@Override
		public Boolean visitSQLCombineFilter(BaseSQLCombineFilter<T> fil) throws Exception {
			sql_filters.addFilter(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
		 */
		@Override
		public Boolean visitAndFilter(AndFilter<T> fil) throws Exception {
			// The AndFilter will invoke the visitBinaryFilter 
			// if its forced
			
			// An ANDFilter could be in any of the 3 categories.
			AcceptFilter<? super T> accept = fil.getAcceptFilter(null);
			if( accept != null ){  
				// This is a pure accept filter as matcher was null
				// this will usually add to the pure_accept_filters but use the visitor
				// as it might also be  BinaryAcceptFilter
				addFilter(accept);
				return null;
			}
			if( ! fil.hasAcceptFilters()){
				SQLFilter<? super T> sql = fil.getSQLFilter();
				sql_filters.addFilter(sql);
				return null;
			}
			mixed_filters.add(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
		 */
		@Override
		public Boolean visitOrderFilter(SQLOrderFilter<T> fil) throws Exception {
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
		 */
		@Override
		public Boolean visitAcceptFilter(AcceptFilter<T> fil) throws Exception {
			if( fil instanceof BinaryAcceptFilter){
				return visitBinaryFilter((BinaryFilter<T>) fil);
			}
			pure_accept_filters.add(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
		 */
		@Override
		public Boolean visitJoinFilter(JoinFilter<T> fil) throws Exception {
			sql_filters.addJoin(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
		 */
		@Override
		public Boolean visitOrFilter(OrFilter<T> fil) throws Exception {
			sql_filters.addFilter(fil.sql_filters);
			pure_accept_filters.addAll(fil.pure_accept_filters);
			mixed_filters.addAll(fil.mixed_filters);
			dual_filters.addAll(fil.dual_filters);
			return null;
		}


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
		 */
		@Override
		public Boolean visitBinaryFilter(BinaryFilter<T> fil) throws Exception {
			if( fil.getBooleanResult()){
				force_value=true;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
		 */
		@Override
		public Boolean visitDualFilter(DualFilter<T> fil) throws Exception {
			dual_filters.add(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
		 */
		@Override
		public Boolean visitBinaryAcceptFilter(BinaryAcceptFilter<T> fil) throws Exception {
			return visitBinaryFilter(fil);
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		if( force_value){
			return vis.visitBinaryFilter(this);
		}
		return vis.visitOrFilter(this);
	}
	public final OrFilter<T> addFilter(BaseFilter<? super T> fil){
		add(fil, true);
		return this;
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(T o) {
		if( force_value){
			return true;
		}
		// Start with pure accept filters
		for( AcceptFilter<? super T> accept : pure_accept_filters){
			if(accept.accept(o)){
				return true;
			}
		}
		for( DualFilter<? super T> dual : dual_filters){
			if(dual.getAcceptFilter().accept(o)){
				return true;
			}
		}
		if( ! sql_filters.isEmpty()){
			if( filterMatches(sql_filters, o)){
				return true;
			}
		}
		for( BaseFilter<? super T> fil : mixed_filters){
			if ( filterMatches(fil, o)){
				return true;
			}
		}
		
		return false;
	}
	/** check for a match between a sub-filter and a target objec
	 * 
	 * @param fil
	 * @param o
	 * @return
	 * @throws DataException 
	 */
	private boolean filterMatches(BaseFilter<? super T> fil, T o) {
		return matcher.matches((BaseFilter<T>) fil, o);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterSet#getAddVisitor()
	 */
	@Override
	protected FilterVisitor getAddVisitor() {
		return new AddFilterVisitor();
	}
	/** Make a pure SQL version of the current state if possible.
	 * @return {@link SQLFilter}
	 * @throws NoSQLFilterException 
	 */
	public SQLFilter<T> getSQLFilter() throws NoSQLFilterException{
		if( force_value){
			return new GenericBinaryFilter<>(target, true);
		}
		if( isEmpty()){
			return new GenericBinaryFilter<>(target, false);
		}
		if( nonSQL()){
			throw new NoSQLFilterException("OrFilter contains non SQL filter");
		}
		SQLOrFilter<T> or = new SQLOrFilter<>(target,sql_filters);
		for(DualFilter<? super T> fil : dual_filters){
			or.addFilter(fil.getSQLFilter());
		}
		return or;
	}
	/**
	 * @return
	 */
	public boolean nonSQL() {
		if( force_value){
			return false; // know the value
		}
		return ! (pure_accept_filters.isEmpty() && mixed_filters.isEmpty());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter#getBooleanResult()
	 */
	@Override
	public boolean getBooleanResult() {
		return force_value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		if( ! force_value ) {
			// all forced true or filters are equivalent
			result = prime * result + ((dual_filters == null) ? 0 : dual_filters.hashCode());
			result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
			result = prime * result + ((mixed_filters == null) ? 0 : mixed_filters.hashCode());
			result = prime * result + ((pure_accept_filters == null) ? 0 : pure_accept_filters.hashCode());
			result = prime * result + ((sql_filters == null) ? 0 : sql_filters.hashCode());
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
		OrFilter other = (OrFilter) obj;
		if( force_value && other.force_value) {
			// both forced to true can ignore filters
			return true;
		}
		if (force_value != other.force_value)
			return false;
		if (dual_filters == null) {
			if (other.dual_filters != null)
				return false;
		} else if (!dual_filters.equals(other.dual_filters))
			return false;
		
		if (matcher == null) {
			if (other.matcher != null)
				return false;
		} else if (!matcher.equals(other.matcher))
			return false;
		if (mixed_filters == null) {
			if (other.mixed_filters != null)
				return false;
		} else if (!mixed_filters.equals(other.mixed_filters))
			return false;
		if (pure_accept_filters == null) {
			if (other.pure_accept_filters != null)
				return false;
		} else if (!pure_accept_filters.equals(other.pure_accept_filters))
			return false;
		if (sql_filters == null) {
			if (other.sql_filters != null)
				return false;
		} else if (!sql_filters.equals(other.sql_filters))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterSet#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		
		return sql_filters.isEmpty() && pure_accept_filters.isEmpty() && mixed_filters.isEmpty() && dual_filters.isEmpty();
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		boolean seen=false;
		sb.append("OrFilter(");
		if( ! sql_filters.isEmpty()){
			sb.append(sql_filters);
			seen=true;
		}
		for(BaseFilter a : pure_accept_filters){
			if( seen ){ sb.append(","); }else{ seen=true; }
			sb.append(a);
		}
		for(BaseFilter a : mixed_filters){
			if( seen ){ sb.append(","); }else{ seen=true; }
			sb.append(a);
		}
		for(BaseFilter a : dual_filters){
			if( seen ){ sb.append(","); }else{ seen=true; }
			sb.append(a);
		}
		sb.append(" force=");
		sb.append(Boolean.toString(force_value));
		sb.append(")");
		return sb.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterSet#getSet()
	 */
	@Override
	public Set<BaseFilter> getSet() {
		Set<BaseFilter> result = new HashSet<>();
		result.addAll(sql_filters.getSet());
		result.addAll(pure_accept_filters);
		result.addAll(mixed_filters);
		result.addAll(dual_filters);
		return result;
	}
	
	public FilterMatcher<T> getMatcher(){
		return matcher;
	}
}
