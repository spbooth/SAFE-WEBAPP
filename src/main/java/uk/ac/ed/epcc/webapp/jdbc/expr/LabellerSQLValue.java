//| Copyright - The University of Edinburgh 2013                            |
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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Labeller;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/**
 * @author spb
 * @param <T> type of nested SQLValue
 * @param <R> type of returned object
 *
 */

public class LabellerSQLValue<T,R> implements NestedSQLValue<R,T> {

	public LabellerSQLValue(AppContext c,Labeller<T,R> labeller, SQLValue<T> nested) {
		super();
		this.conn=c;
		this.labeller = labeller;
		this.nested = nested;
	}
	private final AppContext conn;
	private final Labeller<T,R> labeller;
	private final SQLValue<T> nested;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<R> getTarget() {
		return labeller.getTarget();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		return nested.add(sb,qualify);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return nested.getParameters(list);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public R makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return labeller.getLabel(conn, nested.makeObject(rs, pos));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return nested.getRequiredFilter();
	}
	@Override
	public String toString() {
		return labeller.getClass().getSimpleName()+"("+nested.toString()+")";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.NestedSQLValue#getNested()
	 */
	@Override
	public SQLValue<T> getNested() {
		return nested;
	}

	@Override
	final public boolean groupingIsomorphic() {
		// labeller may map multiple records to the same label
		return false;
	}
}