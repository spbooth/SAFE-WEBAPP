// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.AverageValue;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link SQLValue} that generates an {@link AverageValue} from a combination of
 * a sum {@link SQLValue} and a count field. 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SQLAverageValue.java,v 1.4 2014/09/15 14:30:24 spb Exp $")
public class SQLAverageValue implements SQLValue<AverageValue>{

	public SQLAverageValue(SQLExpression<? extends Number> value) {
		super();
		this.value = value;
	}

	private final SQLExpression<? extends Number> value;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super AverageValue> getTarget() {
		return AverageValue.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("SUM(");
		int fields = value.add(sb, qualify);
		sb.append("), COUNT(");
		// make sure we count the number of records where
		// the value is actually defined.
		fields += value.add(sb,qualify);
		sb.append(")");
		return fields;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		// first the sum clause
		list = value.getParameters(list);
		// then the count clause
		return value.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public AverageValue makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return new AverageValue(value.makeObject(rs, pos).doubleValue(), rs.getLong(pos+1));
		} catch (SQLException e) {
			throw new DataException("Errir getting count", e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return value.getRequiredFilter();
	}

}
