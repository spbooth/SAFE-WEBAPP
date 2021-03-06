//| Copyright - The University of Edinburgh 2012                            |
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
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** SQLExpression that counts distinct values of an expression. 
 * @author spb
 * @param <T> type of nested expression
 *
 */

public class CountDistinctExpression<T> implements SQLExpression<Integer> {
	public CountDistinctExpression(SQLValue<T> expr) {
		super();
		this.expr = expr;
	}

	private SQLValue<T> expr;

	public int add(StringBuilder sb, boolean qualify) {
		sb.append("COUNT(DISTINCT ");
		int res = expr.add(sb, qualify);
		sb.append(" )");
		return res;
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return expr.getParameters(list);
	}

	public Integer makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return rs.getInt(pos);
	}

	public SQLFilter getRequiredFilter() {
		return expr.getRequiredFilter();
	}

	public Class<Integer> getTarget() {
		return Integer.class;
	}
	
}