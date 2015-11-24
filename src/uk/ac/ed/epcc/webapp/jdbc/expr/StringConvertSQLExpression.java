// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** SQLValue that converts an SQLValue of a different type to a string
 * 
 * @author spb
 *
 * @param <T> type of nested SQLAccessor
 */
@uk.ac.ed.epcc.webapp.Version("$Id: StringConvertSQLExpression.java,v 1.3 2014/09/15 14:30:24 spb Exp $")

public class StringConvertSQLExpression<T>  implements SQLExpression<String> {
	private SQLExpression<T> a;
    public StringConvertSQLExpression(SQLExpression<T> a){
    	this.a=a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	public String makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getString(pos);
		} catch (SQLException e) {
			throw new DataFault("Error reading value as string",e);
		}

	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("String(");
    	a.add(sb, true);
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super String> getTarget() {
		return String.class;
	}
}