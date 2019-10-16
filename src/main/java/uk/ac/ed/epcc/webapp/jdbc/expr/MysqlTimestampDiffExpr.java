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
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;

/**
 * @author Stephen Booth
 *
 */
public class MysqlTimestampDiffExpr implements SQLExpression<Long> {
	@Override
	public String toString() {
		return "MysqlTimestampDiffExpr [resolution=" + resolution + ", start=" + start + ", end=" + end + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + (int) (resolution ^ (resolution >>> 32));
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MysqlTimestampDiffExpr other = (MysqlTimestampDiffExpr) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (resolution != other.resolution)
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	/**
	 * @param resolution
	 * @param start
	 * @param end
	 */
	public MysqlTimestampDiffExpr(long resolution, SQLExpression<Date> start, SQLExpression<Date> end) {
		super();
		this.resolution = resolution;
		this.start = start;
		this.end = end;
	}

	private final long resolution;
	private final SQLExpression<Date> start;
	private final SQLExpression<Date> end;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		
		long scale=1L;
		String unit;
		if( resolution > 1L && resolution%1000L == 0L) {
			unit="SECOND,";
			scale = resolution/1000L;
		}else {
			unit="MICROSECOND,";
			scale=resolution*1000L;
		}
		if( scale > 1L) {
			sb.append("(");
		}
		sb.append("TIMESTAMPDIFF(");
		sb.append(unit);
		start.add(sb, qualify);
		sb.append(",");
		end.add(sb, qualify);
		sb.append(")");
		if( scale > 1L) {
			sb.append("/");
			sb.append(Long.toString(scale));
			sb.append(")");
		}
		return 1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		start.getParameters(list);
		end.getParameters(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public Long makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return rs.getLong(pos);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<Long> getTarget() {
		return Long.class;
	}

}
