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
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link ResultMapper} that returns the number of matching records
 * 
 * @author spb
 *
 */
public class CounterDistinctMapper<T> implements ResultMapper<Long>{
	private final SQLExpression<T> expr;
	private boolean qualify=false;
	/**
	 * @param expr
	 */
	public CounterDistinctMapper(SQLExpression<T> expr) {
		super();
		this.expr = expr;
	}

	public Long makeObject(ResultSet rs) throws SQLException {
		
			if( rs.first()){
			    return rs.getLong(1);
			}else{
				return null;
			}
	}

	public String getTarget() {
		StringBuilder sb = new StringBuilder();
		sb.append("COUNT( DISTINCT ");
		expr.add(sb, qualify);
		sb.append(")");
		return sb.toString();
	}

	public Long makeDefault() {
		return new Long(0);
	}

	public String getModify() {
		return null;
	}

	public boolean setQualify(boolean qualify) {
		boolean old = this.qualify;
		this.qualify=qualify;
		return old;
	}

	public SQLFilter getRequiredFilter() {
		return null;
	}

	public List<PatternArgument> getTargetParameters(
			List<PatternArgument> list) {
		
		return expr.getParameters(list);
	}

	public List<PatternArgument> getModifyParameters(
			List<PatternArgument> list) {
		return list;
	}
}