// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataError;

/** A {@link SQLExpression} that rounds an numeric {@link SQLExpression} to
 * the nearest integer.
 * @author spb
 * @param <N> type of inner expression
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RoundSQLExpression.java,v 1.3 2014/09/15 14:30:24 spb Exp $")
public class RoundSQLExpression<N extends Number> implements SQLExpression<Integer> {

	private final SQLExpression<N> expr;
	/**
	 * @param expr {@link SQLExpression} to convert
	 * 
	 */
	public RoundSQLExpression(SQLExpression<N> expr) {
		this.expr=expr;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("ROUND(");
		int result = expr.add(sb, qualify);
		sb.append(",0)");
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return expr.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public Integer makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getInt(pos);
		} catch (SQLException e) {
			throw new DataError(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return expr.getRequiredFilter();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super Integer> getTarget() {
		return Integer.class;
	}

}
