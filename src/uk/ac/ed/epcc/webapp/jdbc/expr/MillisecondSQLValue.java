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
@uk.ac.ed.epcc.webapp.Version("$Id: MillisecondSQLValue.java,v 1.3 2014/09/15 14:30:23 spb Exp $")


public class MillisecondSQLValue implements SQLValue<Long>{
	private final SQLValue<Date> a;
	public MillisecondSQLValue(SQLValue<Date> a){
		this.a=a;
	}
	
	public Class<? super Long> getTarget() {
		return Long.class;
	}
	@Override
	public String toString(){
		return "Millis("+a.toString()+")";
	}

	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb, qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	

	public Long makeObject(ResultSet rs, int pos) throws DataException {
		return Long.valueOf(a.makeObject(rs, pos).getTime());
	}

	public SQLFilter getRequiredFilter() {
		return null;
	}
}