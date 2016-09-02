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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.ConstPatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.Joiner;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/** a {@link IndexedSQLValue} that resolves to a constant value.
 * 
 * It adds the id of the reference to the SQL statement.
 * @author spb
 * @param <T> Type of owning/home table.
 * @param <I> Type of remote table
 */
public class ConstIndexedSQLValue<T extends DataObject,I extends DataObject> implements IndexedSQLValue<T, I> {

	public ConstIndexedSQLValue(AppContext conn, Class<? super T> clazz, IndexedReference<I> val) {
		super();
		this.conn = conn;
		this.clazz = clazz;
		this.val = val;
	}

	private final AppContext conn;
	private final Class<? super T> clazz;
	private final IndexedReference<I> val;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("?");
		return 1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list.add(new ConstPatternArgument<Integer>(Integer.class, Integer.valueOf(val.getID())));
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public IndexedReference<I> makeObject(ResultSet rs, int pos) throws DataException {
		// we can't make expressions out of these so its ok to return the constant
		return val;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super IndexedReference<I>> getTarget() {
		return IndexedReference.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilter(uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition, java.lang.Object)
	 */
	@Override
	public SQLFilter<T> getFilter(MatchCondition match, IndexedReference<I> val)
			throws CannotFilterException, NoSQLFilterException {
		return new GenericBinaryFilter<>(clazz, this.val.equals(val));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getNullFilter(boolean)
	 */
	@Override
	public SQLFilter<T> getNullFilter(boolean is_null) throws CannotFilterException, NoSQLFilterException {
		// its never null
		return new GenericBinaryFilter<>(clazz, ! is_null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getOrderFilter(boolean)
	 */
	@Override
	public SQLFilter<T> getOrderFilter(boolean descending) throws CannotFilterException, NoSQLFilterException {
		throw new CannotFilterException("order by constant value not supported");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	@Override
	public Class<? super T> getFilterType() {
		return clazz;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getSQLFilter(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter)
	 */
	@Override
	public SQLFilter<T> getSQLFilter(SQLFilter<I> fil) throws CannotFilterException {
		try{
			DataObjectFactory<I> fac = getFactory();
			return new Joiner<I,T>(clazz, fil, fac.res, val.getID());
		}catch(Exception e){
			throw new CannotFilterException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getFactory()
	 */
	@Override
	public DataObjectFactory<I> getFactory() throws Exception {
		IndexedProducer producer = IndexedReference.makeIndexedProducer(conn, val.getFactoryClass(), val.getTag());
		return (DataObjectFactory<I>) producer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstIndexedSQLValue other = (ConstIndexedSQLValue) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	public String toString(){
		return "CONST("+val.toString()+")";
	}
	

}
