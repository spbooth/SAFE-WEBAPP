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
package uk.ac.ed.epcc.webapp.jdbc.filter;


/** Helper classes to convert generic filters into {@link SQLFilter}s.
 * Unlike {@link FilterConverter} this returns the original filter if
 * conversion is not possible
 * 
 * @author spb
 *
 */
public class OptionalFilterConverter<T> implements FilterVisitor<BaseFilter<T>, T> {

	public static <X> BaseFilter<X> convert(BaseFilter<X> fil)  {
		if( fil instanceof SQLFilter || fil == null){
			return (SQLFilter<X>) fil;
		}
		OptionalFilterConverter<X> conv = new OptionalFilterConverter<>();
	
		try {
			return fil.acceptVisitor(conv);
		} catch (Exception e) {
			return fil;
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	@Override
	public BaseFilter<T> visitPatternFilter(PatternFilter<T> fil) throws NoSQLFilterException {
		return fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public BaseFilter<T> visitSQLCombineFilter(
			BaseSQLCombineFilter<T> fil) throws NoSQLFilterException {
		return  fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public BaseFilter<T> visitAndFilter(AndFilter<T> fil) throws NoSQLFilterException {
		if( ! fil.hasAcceptFilters()) {
			return fil.getSQLFilter();
		}
		return fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
	 */
	@Override
	public BaseFilter<T> visitOrderFilter(SQLOrderFilter<T> fil) throws NoSQLFilterException {
		return fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public BaseFilter<T> visitAcceptFilter(AcceptFilter<T> fil) throws NoSQLFilterException {
		return fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public BaseFilter<T> visitJoinFilter(JoinFilter<T> fil) throws NoSQLFilterException {
		return fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public BaseFilter<T> visitOrFilter(OrFilter<T> fil) throws NoSQLFilterException {
		if( fil.nonSQL()) {
			return fil;
		}
		return fil.getSQLFilter();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public BaseFilter<T> visitBinaryFilter(BinaryFilter<T> fil)  {
		if( fil instanceof SQLFilter){
			return fil;
		}
		return new GenericBinaryFilter<>((Class<T>) fil.getTarget(), fil.getBooleanResult());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
	 */
	@Override
	public BaseFilter<T> visitDualFilter(DualFilter<T> fil)  {
		return fil.getSQLFilter();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
	 */
	@Override
	public BaseFilter<T> visitBinaryAcceptFilter(BinaryAcceptFilter<T> fil) throws Exception {
		return visitBinaryFilter(fil);
	}
}