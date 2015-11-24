// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataError;

/** A {@link SQLExpression} that interprets a numeric {@link SQLExpression} to
 * as a double. This does not alter the SQL expression just the type it is interpretted as.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CastDoubleSQLExpression.java,v 1.3 2015/04/07 12:29:43 spb Exp $")
public class CastDoubleSQLExpression<N extends Number> implements SQLExpression<Double> {

	private final SQLExpression<N> expr;
	/**
	 * 
	 */
	public CastDoubleSQLExpression(SQLExpression<N> expr) {
		this.expr=expr;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		int result = expr.add(sb, qualify);
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
	public Double makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getDouble(pos);
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
	public Class<? super Double> getTarget() {
		return Double.class;
	}

}
