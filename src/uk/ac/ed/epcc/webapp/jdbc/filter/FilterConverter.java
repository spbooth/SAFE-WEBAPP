// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.jdbc.filter;
@uk.ac.ed.epcc.webapp.Version("$Id: FilterConverter.java,v 1.4 2014/09/15 14:30:25 spb Exp $")

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
}