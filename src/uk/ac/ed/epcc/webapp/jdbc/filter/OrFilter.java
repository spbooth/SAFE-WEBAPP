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

import java.util.LinkedHashSet;

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
public final class OrFilter<T> extends FilterSet<T> implements AcceptFilter<T> {
	/**
	 * @param target
	 */
	public OrFilter(Class<? super T> target, FilterMatcher<T> matcher) {
		super(target);
		this.matcher=matcher;
		sql_filters=new SQLOrFilter<T>(target);
		pure_accept_filters = new LinkedHashSet<AcceptFilter<? super T>>();
		mixed_filters = new LinkedHashSet<BaseFilter<? super T>>();
		dual_filters = new LinkedHashSet<DualFilter<? super T>>();
	}

	
	private final FilterMatcher<T> matcher;
	private SQLOrFilter<T> sql_filters;
	private LinkedHashSet<AcceptFilter<? super T>> pure_accept_filters;
	private LinkedHashSet<BaseFilter<? super T>> mixed_filters;
	private LinkedHashSet<DualFilter<? super T>> dual_filters;
	
	private class AddFilterVisitor implements FilterVisitor<Boolean, T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
		 */
		@Override
		public Boolean visitPatternFilter(PatternFilter<? super T> fil) throws Exception {
			sql_filters.addPatternFilter(fil);;
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
		 */
		@Override
		public Boolean visitSQLCombineFilter(BaseSQLCombineFilter<? super T> fil) throws Exception {
			sql_filters.addFilter(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
		 */
		@Override
		public Boolean visitAndFilter(AndFilter<? super T> fil) throws Exception {
			// An ANDFilter could be in any of the 3 categories.
			AcceptFilter<? super T> accept = fil.getAcceptFilter();
			if( accept != null ){
				pure_accept_filters.add(accept);
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
		public Boolean visitOrderFilter(OrderFilter<? super T> fil) throws Exception {
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
		 */
		@Override
		public Boolean visitAcceptFilter(AcceptFilter<? super T> fil) throws Exception {
			pure_accept_filters.add(fil);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
		 */
		@Override
		public Boolean visitJoinFilter(JoinFilter<? super T> fil) throws Exception {
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
		 */
		@Override
		public Boolean visitOrFiler(OrFilter<? super T> fil) throws Exception {
			sql_filters.addFilter(fil.sql_filters);
			pure_accept_filters.addAll(fil.pure_accept_filters);
			mixed_filters.addAll(fil.mixed_filters);
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
		 */
		@Override
		public Boolean visitDualFilter(DualFilter<? super T> fil) throws Exception {
			dual_filters.add(fil);
			return null;
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitOrFiler(this);
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
		// Start with pure accept filters
		for( AcceptFilter<? super T> accept : pure_accept_filters){
			if(accept.accept(o)){
				return true;
			}
		}
		for( AcceptFilter<? super T> accept : dual_filters){
			if(accept.accept(o)){
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
		return matcher.matches(fil, o);
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
		if( nonSQL()){
			throw new NoSQLFilterException("OrFilter contains non SQL filter");
		}
		SQLOrFilter<T> or = new SQLOrFilter<T>(target,sql_filters);
		for(DualFilter<? super T> fil : dual_filters){
			or.addFilter(new DualtoSQLWrapper<T>(fil));
		}
		return or;
	}
	/**
	 * @return
	 */
	public boolean nonSQL() {
		return ! (pure_accept_filters.isEmpty() && mixed_filters.isEmpty());
	}
}
