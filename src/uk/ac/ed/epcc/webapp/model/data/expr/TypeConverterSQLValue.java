// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpressionFilter;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter;
/** A type converter wrapper SQLValue.
 * 
 * @author spb
 *
 * @param <H> type of host object
 * @param <T> Type of object produced
 * @param <D> Type of underlying object.
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TypeConverterSQLValue.java,v 1.14 2014/09/15 14:30:30 spb Exp $")

public class TypeConverterSQLValue<H,T,D> implements  SQLValue<T>, FilterProvider<H,T>{
	private final Class<? super H> target;
	public TypeConverterSQLValue(Class<? super H> target,TypeConverter<T, D> converter, SQLValue<D> inner) {
		super();
		this.target=target;
		this.converter = converter;
		this.inner = inner;
	}
	private final TypeConverter<T,D> converter;
	private final SQLValue<D> inner;
	
	public Class<? super T> getTarget() {
		return converter.getTarget();
	}
	
	public int add(StringBuilder sb, boolean qualify) {
		
		return inner.add(sb, qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return inner.getParameters(list);
	}
	public T makeObject(ResultSet rs, int pos) throws DataException {
		return converter.find(inner.makeObject(rs, pos));
	}
	public SQLFilter getRequiredFilter() {
		return inner.getRequiredFilter();
	}
	public String toString(){
		return converter.toString()+"("+inner.toString()+")";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilter(uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition, java.lang.Object)
	 */
	public SQLFilter<H> getFilter(MatchCondition match, T val)
			throws CannotFilterException, NoSQLFilterException {
		if( match != null ){
			throw new CannotFilterException("Cannot perform relative match via TypeConverter");
		}
		D equiv = converter.getIndex(val);
		if( inner instanceof FilterProvider){
			return ((FilterProvider<H, D>)inner).getFilter(null, equiv);
		}else if( inner instanceof SQLExpression){
			return new SQLExpressionFilter<H, D>(target,(SQLExpression<D>)inner, equiv);
		}
		throw new CannotFilterException();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getNullFilter(boolean)
	 */
	public SQLFilter<H> getNullFilter(boolean is_null)
			throws CannotFilterException, NoSQLFilterException {
		if( inner instanceof FilterProvider){
			return ((FilterProvider<H, D>)inner).getNullFilter(is_null);
		}
		throw new NoSQLFilterException();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getOrderFilter(boolean)
	 */
	public SQLFilter<H> getOrderFilter(boolean descending)
			throws CannotFilterException, NoSQLFilterException {
		throw new CannotFilterException("Cannot generate order using TypeConverter");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	public Class<? super H> getFilterType() {
		return target;
	}

	

}