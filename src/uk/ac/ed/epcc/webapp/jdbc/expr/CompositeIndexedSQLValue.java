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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/** A {@link IndexedSQLValue} that extends through a join
 * @author spb
 * @param <H> Type of home table
 * @param <I> Type of intermediate table
 * @param <T> Type of target table
 *
 */
public class CompositeIndexedSQLValue<H extends DataObject,I extends DataObject, T extends DataObject> 
implements IndexedSQLValue<H,T>{
	public CompositeIndexedSQLValue(IndexedSQLValue<H, I> base, IndexedSQLValue<I, T> branch) throws CannotFilterException {
		super();
		this.base = base;
		this.branch = branch;
		this.required_filter=base.getSQLFilter(branch.getRequiredFilter());
	}

	private final IndexedSQLValue<H,I> base;
	private final IndexedSQLValue<I,T> branch;
	private final SQLFilter required_filter;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		return branch.add(sb, qualify);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return branch.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public IndexedReference<T> makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return branch.makeObject(rs, pos);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		return required_filter;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<IndexedReference> getTarget() {
		return branch.getTarget();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilter(uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition, java.lang.Object)
	 */
	@Override
	public SQLFilter<H> getFilter(MatchCondition match, IndexedReference val)
			throws CannotFilterException, NoSQLFilterException {
		return base.getSQLFilter(branch.getFilter(match, val));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getNullFilter(boolean)
	 */
	@Override
	public SQLFilter<H> getNullFilter(boolean is_null) throws CannotFilterException, NoSQLFilterException {
		return base.getSQLFilter(branch.getNullFilter(is_null));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getOrderFilter(boolean)
	 */
	@Override
	public SQLFilter<H> getOrderFilter(boolean descending) throws CannotFilterException, NoSQLFilterException {
		return base.getSQLFilter(branch.getOrderFilter(descending));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	@Override
	public Class<H> getFilterType() {
		return base.getFilterType();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getSQLFilter(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter)
	 */
	@Override
	public SQLFilter<H> getSQLFilter(SQLFilter<T> fil) throws CannotFilterException {
		return base.getSQLFilter(branch.getSQLFilter(fil));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getFactory()
	 */
	@Override
	public DataObjectFactory<T> getFactory() throws Exception {
		return branch.getFactory();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return base.toString()+"["+branch.toString()+"]";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getIDExpression()
	 */
	@Override
	public SQLExpression<Integer> getIDExpression() {
		return branch.getIDExpression();
	}

}
