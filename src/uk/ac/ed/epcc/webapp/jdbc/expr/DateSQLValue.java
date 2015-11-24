// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
@uk.ac.ed.epcc.webapp.Version("$Id: DateSQLValue.java,v 1.3 2014/09/15 14:30:23 spb Exp $")

/** A {@link SQLValue} that converts a millisecond value into a Date.
 * 
 * @author spb
 *
 */
public class DateSQLValue implements SQLValue<Date>{
	private final SQLValue<Number> a;
	public DateSQLValue(SQLValue<Number> a){
		this.a=a;
	}
	
	public Class<? super Date> getTarget() {
		return Date.class;
	}
	@Override
	public String toString(){
		return "Date("+a.toString()+")";
	}

	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb, qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	

	public Date makeObject(ResultSet rs, int pos) throws DataException {
		return new Date(a.makeObject(rs, pos).longValue());
	}

	public SQLFilter getRequiredFilter() {
		return null;
	}
}