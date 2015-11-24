// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** Convert a date field to milliseconds
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MysqlMillisecondConverter.java,v 1.4 2014/09/15 14:30:24 spb Exp $")

public class MysqlMillisecondConverter implements SQLExpression<Number>{
   @Override
	public int hashCode() {
		return exp.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MysqlMillisecondConverter other = (MysqlMillisecondConverter) obj;
		if (exp == null) {
			if (other.exp != null)
				return false;
		} else if (!exp.equals(other.exp))
			return false;
		return true;
	}
private SQLExpression<Date> exp;

 
public MysqlMillisecondConverter(SQLExpression<Date> e){
	   exp=e;
}

public Long makeObject(ResultSet rs, int pos) throws DataException {
	try {
		return rs.getLong(pos);
	} catch (SQLException e) {
		throw new DataException("Failed to make long from date", e);
	}
}
public int add(StringBuilder sb, boolean qualify) {
	sb.append("(1000*UNIX_TIMESTAMP(");
	int res = exp.add(sb,qualify);
	sb.append("))");
	assert( res == 1 ); // Its an SQLExpression
	return res;
	
 }
public List<PatternArgument> getParameters(List<PatternArgument> list) {
	return exp.getParameters(list);
}
public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("(1000*UNIX_TIMESTAMP(");
	sb.append(exp.toString());
	sb.append("))");
	return sb.toString();
 }

public Class<? super Number> getTarget() {
	return Number.class;
}
public SQLFilter getRequiredFilter() {
	return exp.getRequiredFilter();
}
}