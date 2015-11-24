package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
/** Use a {@link SQLExpression} as an {@link OrderClause}
 * 
 * @author spb
 *
 * @param <T>
 */
public class SQLExpressionOrderClause<T> implements OrderClause {

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
