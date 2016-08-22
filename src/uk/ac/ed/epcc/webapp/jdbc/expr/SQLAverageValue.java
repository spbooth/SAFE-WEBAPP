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

import uk.ac.ed.epcc.webapp.AverageValue;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link SQLValue} that generates an {@link AverageValue} from a combination of
 * a sum {@link SQLValue} and a count field. 
 * @author spb
 *
 */

public class SQLAverageValue implements SQLValue<AverageValue>{

	public SQLAverageValue(SQLExpression<? extends Number> value) {
		super();
		this.value = value;
	}

	private final SQLExpression<? extends Number> value;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super AverageValue> getTarget() {
		return AverageValue.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("SUM(");
		int fields = value.add(sb, qualify);
		sb.append("), COUNT(");
		// make sure we count the number of records where
		// the value is actually defined.
		fields += value.add(sb,qualify);
		sb.append(")");
		return fields;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		// first the sum clause
		list = value.getParameters(list);
		// then the count clause
		return value.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public AverageValue makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return new AverageValue(value.makeObject(rs, pos).doubleValue(), rs.getLong(pos+1));
		} catch (SQLException e) {
			throw new DataException("Error getting count", e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return value.getRequiredFilter();
	}

}