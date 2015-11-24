// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** Combine multiple {@link SQLExpression} by concatination in SQL.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConcatSQLExpression.java,v 1.2 2014/09/15 14:30:23 spb Exp $")
public class ConcatSQLExpression implements SQLExpression<String>{

	private LinkedList<SQLExpression<String>> values=new LinkedList<SQLExpression<String>>();
	public ConcatSQLExpression(){
		
	}
	public ConcatSQLExpression(SQLExpression<String> ...sqlValues ){
		for(SQLExpression<String> v : sqlValues){
			values.add(v);
		}
	}
	public void add(SQLExpression<String> v){
		values.add(v);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super String> getTarget() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		
		boolean seen=false;
		sb.append("CONCAT(");
		for(SQLValue<String> v : values){
			if( seen ){
				sb.append(",");
			}else{
				seen=true;
			}
			v.add(sb, qualify);
		}
		sb.append(")");
		return 1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(SQLValue<String> v: values){
			list = v.getParameters(list);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public String makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getString(pos);
		} catch (SQLException e) {
			throw new DataException("Failed to make string from concat expr", e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return null;
	}

	
}
