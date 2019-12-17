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
import uk.ac.ed.epcc.webapp.DistinctCount;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link SQLValue} that generates an {@link DistinctCount}
 * 
 * This is intended to be used in group by queries in conjunction with
 * a group-by clause on the nested {@link GroupingSQLValue}.
 * The SQL fragment is therefore the same as the nested value
 * but the result in wrapped in a {@link DistinctCount}
 * 
 * @author spb
 *
 */

public class DistinctCountSQLValue implements GroupingSQLValue<DistinctCount>{

	public DistinctCountSQLValue(GroupingSQLValue value) {
		super();
		this.value = value;
	}

	private final GroupingSQLValue value;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<DistinctCount> getTarget() {
		return DistinctCount.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		return value.add(sb, qualify);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return value.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public DistinctCount makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		
		return DistinctCount.make(value.makeObject(rs, pos));

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return value.getRequiredFilter();
	}

	@Override
	public int addGroup(StringBuilder sb, boolean qualify) {
		return value.addGroup(sb, qualify);
	}

	@Override
	public List<PatternArgument> getGroupParameters(List<PatternArgument> list) {
		return value.getGroupParameters(list);
	}

	@Override
	public boolean checkContentsCanGroup() {
		return value.checkContentsCanGroup();
	}

}