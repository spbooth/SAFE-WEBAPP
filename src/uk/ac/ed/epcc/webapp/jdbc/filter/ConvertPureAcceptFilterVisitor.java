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

/** Convert a {@link BaseFilter} to a pure {@link AcceptFilter}.
 * 
 * If the conversion is not possible then return null, unless throw_exception is set.
 * 
 * @author spb
 * @param <T> type of filter
 *
 */
public class ConvertPureAcceptFilterVisitor<T> implements FilterVisitor<AcceptFilter<? super T>, T> {

	/**
	 * 
	 * @param matcher and optional {@link FilterMatcher}
	 */
	public ConvertPureAcceptFilterVisitor(FilterMatcher<T> matcher) {
		super();
		this.matcher = matcher;
	}

	private final FilterMatcher<T> matcher;
	private boolean throw_exception=false;
	
	public boolean getThrowException() {
		return throw_exception;
	}

	public void setThrowException(boolean throw_exception) {
		this.throw_exception = throw_exception;
	}

	private void doThrow() throws NoAcceptFilterException{
		if( throw_exception){
			throw new NoAcceptFilterException("Cannot convert to a pure AcceptFilter");
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	@Override
	public AcceptFilter<T> visitPatternFilter(PatternFilter<? super T> fil) throws Exception {
		if( matcher != null ){
			return new ConvertToAcceptFilter<T>(fil, matcher);
		}
		doThrow();
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public AcceptFilter<T> visitSQLCombineFilter(BaseSQLCombineFilter<? super T> fil) throws Exception {
		if( matcher != null ){
			return new ConvertToAcceptFilter<T>(fil, matcher);
		}
		// Might be able to do something if all contents are DualFilters
		doThrow();
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public AcceptFilter<T> visitAndFilter(AndFilter<? super T> fil) throws Exception {
		AcceptFilter<T> res = ((AndFilter)fil).getAcceptFilter( matcher);
		if( res == null ){
			doThrow();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
	 */
	@Override
	public AcceptFilter<? super T> visitOrderFilter(OrderFilter<? super T> fil) throws Exception {
		doThrow();
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public AcceptFilter<? super T> visitAcceptFilter(AcceptFilter<? super T> fil) throws Exception {
		return fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public AcceptFilter<? super T> visitJoinFilter(JoinFilter<? super T> fil) throws Exception {
		doThrow();
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public AcceptFilter<? super T> visitOrFilter(OrFilter<? super T> fil) throws Exception {
		// A OrFilter is an AcceptFilter even though it uses SQL to do this
		return fil;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public AcceptFilter<? super T> visitBinaryFilter(BinaryFilter<? super T> fil) throws Exception {
		return new BinaryAcceptFilter<T>(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
	 */
	@Override
	public AcceptFilter<? super T> visitDualFilter(DualFilter<? super T> fil) throws Exception {
		return fil.getAcceptFilter();
	}

}
