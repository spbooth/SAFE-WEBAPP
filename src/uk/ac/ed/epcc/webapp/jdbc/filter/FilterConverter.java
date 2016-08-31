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
 * 
 * @author spb
 *
 */
public class FilterConverter<T> implements FilterVisitor<SQLFilter<T>, T> {

	public static <X> SQLFilter<X> convert(BaseFilter<X> fil) throws NoSQLFilterException{
		if( fil instanceof SQLFilter || fil == null){
			return (SQLFilter<X>) fil;
		}
		FilterConverter<X> conv = new FilterConverter<X>();
		try {
			return fil.acceptVisitor(conv);
		}catch( NoSQLFilterException nsql){
			throw nsql;
		} catch (Exception e) {
			throw new NoSQLFilterException("Error converting to SQL", e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	public SQLFilter<T> visitPatternFilter(PatternFilter<? super T> fil) throws NoSQLFilterException {
		if( fil instanceof SQLFilter){
			return (SQLFilter<T>) fil;
		}
		throw new NoSQLFilterException("Filter Not an SQLFilter "+fil.getClass().getCanonicalName());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	public SQLFilter<T> visitSQLCombineFilter(
			BaseSQLCombineFilter<? super T> fil) throws NoSQLFilterException {
		return (SQLFilter<T>) fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	public SQLFilter<T> visitAndFilter(AndFilter<? super T> fil) throws NoSQLFilterException {
		return (SQLFilter<T>) fil.getSQLFilter();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
	 */
	public SQLFilter<T> visitOrderFilter(OrderFilter<? super T> fil) throws NoSQLFilterException {
		if( fil instanceof SQLFilter){
			return (SQLFilter<T>) fil;
		}
		throw new NoSQLFilterException("Filter Not an SQLFilter "+fil.getClass().getCanonicalName());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	public SQLFilter<T> visitAcceptFilter(AcceptFilter<? super T> fil) throws NoSQLFilterException {
		throw new NoSQLFilterException("Cannot convert AcceptFilter to SQL");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	public SQLFilter<T> visitJoinFilter(JoinFilter<? super T> fil) throws NoSQLFilterException {
		if( fil instanceof SQLFilter){
			return (SQLFilter<T>) fil;
		}
		throw new NoSQLFilterException("Filter Not an SQLFilter "+fil.getClass().getCanonicalName());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFiler(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public SQLFilter<T> visitOrFilter(OrFilter<? super T> fil) throws Exception {
		return (SQLFilter<T>) fil.getSQLFilter();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public SQLFilter<T> visitBinaryFilter(BinaryFilter<? super T> fil) throws Exception {
		if( fil instanceof SQLFilter){
			return (SQLFilter<T>) fil;
		}
		return new GenericBinaryFilter<T>(fil.getTarget(), fil.getBooleanResult());
	}
}