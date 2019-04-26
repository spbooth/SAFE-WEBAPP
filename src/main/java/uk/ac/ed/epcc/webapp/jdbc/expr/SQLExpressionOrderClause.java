//| Copyright - The University of Edinburgh 2015                            |
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

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
/** Use a {@link SQLExpression} as an {@link OrderClause}
 * 
 * Note this does not include any required filter needed by the {@link SQLExpression}
 * @author spb
 *
 * @param <T>
 */
public class SQLExpressionOrderClause<T> implements OrderClause {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (descending ? 1231 : 1237);
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
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
		SQLExpressionOrderClause other = (SQLExpressionOrderClause) obj;
		if (descending != other.descending)
			return false;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		return true;
	}

	private final SQLExpression<T> expr;
	boolean descending;
	public SQLExpressionOrderClause(boolean descending,SQLExpression<T> expr) {
		this.descending=descending;
		this.expr=expr;
	}

	public StringBuilder addClause(StringBuilder sb, boolean qualify) {
		expr.add(sb, qualify);
		if( descending ){
			sb.append(" DESC");
		}
		return sb;
	}

}