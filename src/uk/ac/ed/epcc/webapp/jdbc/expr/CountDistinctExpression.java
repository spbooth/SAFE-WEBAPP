// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataError;

/** SQLExpression that counts distinct values of an expression. 
 * @author spb
 * @param <T> type of nested expression
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CountDistinctExpression.java,v 1.2 2014/09/15 14:30:23 spb Exp $")
public class CountDistinctExpression<T> implements SQLExpression<Integer> {
	public CountDistinctExpression(SQLExpression<T> expr) {
		super();
		this.expr = expr;
	}

	private SQLExpression<T> expr;

	public int add(StringBuilder sb, boolean qualify) {
		sb.append("COUNT(DISTINCT ");
		int res = expr.add(sb, qualify);
		sb.append(" )");
		return res;
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return expr.getParameters(list);
	}

	public Integer makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getInt(pos);
		} catch (SQLException e) {
			throw new DataError(e);
		}
	}

	public SQLFilter getRequiredFilter() {
		return expr.getRequiredFilter();
	}

	public Class<? super Integer> getTarget() {
		return Integer.class;
	}
	
}
