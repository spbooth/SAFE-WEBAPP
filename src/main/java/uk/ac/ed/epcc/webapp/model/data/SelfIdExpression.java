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

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link SQLExpression} for the unique id field.
 * 
 * Keep visibility to package as this bypasses all type checking.
 * Normally you use a {@link SelfSQLValue} 
 * @author spb
 *
 */
public class SelfIdExpression implements SQLExpression<Integer> {

	private final Repository res;
	/**
	 * 
	 */
	public SelfIdExpression(Repository res) {
		this.res=res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		res.addUniqueName(sb, qualify, true);
		return 1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public Integer makeObject(ResultSet rs, int pos) throws DataException, SQLException {
			return Integer.valueOf(rs.getInt(pos));
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
	public Class<Integer> getTarget() {
		return Integer.class;
	}

	@Override
	public String getFilterTag() {
		return res.getTag();
	}

}
