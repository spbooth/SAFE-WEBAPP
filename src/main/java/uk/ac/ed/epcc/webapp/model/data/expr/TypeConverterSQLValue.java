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
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.*;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter;
/** A type converter wrapper SQLValue.
 * normally you want a {@link TypeFilterProducerSQLValue}
 * @author spb
 *
 * @param <H> type of host object
 * @param <T> Type of object produced
 * @param <D> Type of underlying object.
 */


public class TypeConverterSQLValue<H,T,D> implements  NestedSQLValue<T,D>, FilterProvider<H,T>{
	
	
	public TypeConverterSQLValue(Class<T> type,TypeConverter<T, D> converter, SQLValue<D> inner) {
		super();
		this.converter = converter;
		this.type=type;
		this.inner = inner;
	}
	private final TypeConverter<T,D> converter;
	private final Class<T> type;
	private final SQLValue<D> inner;
	
	@Override
	public SQLValue<D> getNested(){
		return inner;
	}
	protected TypeConverter<T, D> getConverter(){
		return converter;
	}
	@Override
	public final Class<T> getTarget() {
		return type;
	}
	
	@Override
	public final int add(StringBuilder sb, boolean qualify) {
		
		return inner.add(sb, qualify);
	}
	@Override
	public final List<PatternArgument> getParameters(List<PatternArgument> list) {
		return inner.getParameters(list);
	}
	@Override
	public final T makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return converter.find(inner.makeObject(rs, pos));
	}
	
	@Override
	public final String toString(){
		return converter.toString()+"("+inner.toString()+")";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilter(uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition, java.lang.Object)
	 */
	@Override
	public final SQLFilter<H> getFilter(MatchCondition match, T val)
			throws CannotFilterException, NoSQLFilterException {
		if( match != null && match != MatchCondition.NE){
			throw new CannotFilterException("Cannot perform relative match via TypeConverter");
		}
		D equiv = converter.getIndex(val);
		if( inner instanceof FilterProvider){
			return ((FilterProvider<H, D>)inner).getFilter(match, equiv);
		}else if( inner instanceof SQLExpression){
			return SQLExpressionFilter.getFilter((SQLExpression<D>)inner,match, equiv);
		}
		throw new CannotFilterException();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getNullFilter(boolean)
	 */
	@Override
	public final SQLFilter<H> getNullFilter(boolean is_null)
			throws CannotFilterException, NoSQLFilterException {
		if( inner instanceof FilterProvider){
			return ((FilterProvider<H, D>)inner).getNullFilter(is_null);
		}
		throw new NoSQLFilterException();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getOrderFilter(boolean)
	 */
	@Override
	public final SQLFilter<H> getOrderFilter(boolean descending)
			throws CannotFilterException, NoSQLFilterException {
		throw new CannotFilterException("Cannot generate order using TypeConverter");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	@Override
	public final String getFilterTag() {
		return inner.getFilterTag();
	}

	@Override
	public boolean groupingIsomorphic() {
		// Can't assume mapping is isomorphic for all TypeConverters
		return false;
	}

}