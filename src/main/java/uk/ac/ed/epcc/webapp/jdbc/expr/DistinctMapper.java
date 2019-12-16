//| Copyright - The University of Edinburgh 2019                            |
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.DistinctCount;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** Generate a {@link DistinctCount} for a {@link SQLValue}
 * @author Stephen Booth
 * @param <D> type of inner SQLValue
 *
 */
public class DistinctMapper<D> extends AbstractContexed implements ResultMapper<DistinctCount> {

	private final SQLValue val;
	private boolean qualify = false;
	/**
	 * @param conn
	 */
	public DistinctMapper(AppContext conn,SQLValue<D> val) {
		super(conn);
		if( val instanceof NestedSQLValue) {
			this.val = ((NestedSQLValue)val).getNested();
		}else {
			this.val = val;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#setQualify(boolean)
	 */
	@Override
	public boolean setQualify(boolean qualify) {
		boolean old = this.qualify;
		this.qualify=qualify;
		return old;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#makeObject(java.sql.ResultSet)
	 */
	@Override
	public DistinctCount makeObject(ResultSet rs) throws DataException, SQLException {
		Set s = new HashSet();
		do {
			s.add(val.makeObject(rs, 1));
		}while(rs.next());
		
		return new DistinctCount(s);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#makeDefault()
	 */
	@Override
	public DistinctCount makeDefault() {
		return DistinctCount.zero();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getTarget()
	 */
	@Override
	public String getTarget() {
		StringBuilder sb = new StringBuilder();
		sb.append("DISTINCT ");
		int count = val.add(sb, qualify);
		if( count != 1 ) {
			getLogger().warn(() -> "Multi field SQLValue used in DISTINCT reduction "+val.toString());
		}
		return sb.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getTargetParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
		return val.getParameters(list);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getModify()
	 */
	@Override
	public String getModify() {
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getModifyParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
		return list;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		return val.getRequiredFilter();
	}

}
