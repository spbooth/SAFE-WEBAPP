// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.session.Hash;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MysqlSQLHashExpression.java,v 1.3 2014/09/15 14:30:24 spb Exp $")
public class MysqlSQLHashExpression implements SQLExpression<String>{

	private final SQLExpression<String> inner;
	private final String start;
	private final String end;
	/**
	 * @param h {@link Hash} to apply
	 * @param inner {@link SQLExpression} expression to hash
	 * @throws CannotUseSQLException 
	 * 
	 */
	public MysqlSQLHashExpression(Hash h, SQLExpression<String> inner) throws CannotUseSQLException {
		this.inner=inner;
		switch(h){
		case MD5:
		case SHA1:
			start = h.name()+"(";
			end = ")";
			break;
		case SHA256:
		case SHA384:
		case SHA512:
			start ="SHA2(";
			end = ","+h.size()+")";
			break;
		default:
			throw new CannotUseSQLException("Unsupported hash");
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		sb.append(start);
		int result = inner.add(sb, qualify);
		sb.append(end);
		return result;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return inner.getParameters(list);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public String makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getString(pos);
		} catch (SQLException e) {
			throw new DataException("Cannot retreive hash value",e);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super String> getTarget() {
		return String.class;
	}

}
