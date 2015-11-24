// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeFilterProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
/** A type converter wrapper SQLValue.
 * 
 * 
 * @author spb
 *
 * @param <T> type of value produced
 * @param <D> underlying value
 * @param <X> DataObject
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TypeFilterProducerSQLValue.java,v 1.13 2014/09/15 14:30:30 spb Exp $")

public class TypeFilterProducerSQLValue<T,D,X extends DataObject> implements  SQLAccessor<T,X>,  FilterProvider<X,T> , Targetted<T>{
	public TypeFilterProducerSQLValue(DataObjectFactory<X> fac, TypeProducer<T, D> converter, SQLAccessor<D,X> inner) {
		super();
		this.fac=fac;
		this.converter = converter;
		this.inner = inner;
	}
	private final DataObjectFactory<X> fac;
	private final TypeProducer<T,D> converter;
	private final SQLAccessor<D,X> inner;
	public T getValue(X r) {

		return converter.find(inner.getValue(r));
	}
	public Class<? super T> getTarget() {
		return converter.getTarget();
	}
	public void setValue(X r, T value) {
		inner.setValue(r, converter.getIndex(value));

	}
	public boolean canSet() {
		return inner.canSet();
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
	public SQLFilter<X> getFilter(MatchCondition match, T val)
			throws CannotFilterException {
		if( converter instanceof TypeFilterProducer){
			if( match == null ){
				return ((TypeFilterProducer<T,D>)converter).getSQLFilter(fac, val);
			}
			if( match == MatchCondition.NE){
				Set<T> vals = new HashSet<T>();
				vals.add(val);
				return ((TypeFilterProducer<T,D>)converter).getSQLExcludeFilter(fac, vals);
			}
		}
		throw new CannotFilterException("Class "+converter.getClass().getCanonicalName()+" not a TypeFilterProducer");
	}
	public SQLFilter<X> getNullFilter(boolean is_null)
			throws CannotFilterException {
		throw new CannotFilterException("Null filter not supported for TypeProducer");
	}
	public SQLFilter<X> getOrderFilter(boolean descending)
			throws CannotFilterException {
		throw new CannotFilterException("Order filter not supported");
	}
	public String toString(){
		return converter.toString()+"("+inner.toString()+")";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	public Class<? super X> getFilterType() {
		return fac.getTarget();
	}
}