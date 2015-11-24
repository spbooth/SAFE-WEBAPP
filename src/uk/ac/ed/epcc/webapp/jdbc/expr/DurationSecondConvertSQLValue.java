// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Duration;

/** {@link SQLValue} that converts a {@link Duration} into seconds
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DurationSecondConvertSQLValue.java,v 1.4 2014/09/15 14:30:23 spb Exp $")

public class DurationSecondConvertSQLValue  implements SQLValue<Number> {
	private SQLValue<Duration> a;
    public DurationSecondConvertSQLValue(SQLValue<Duration> a){
    	this.a = a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Number makeObject(ResultSet rs, int pos) throws DataException {
		Duration temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	return temp.getSeconds();
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Seconds(");
    	sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super Number> getTarget() {
		return Number.class;
	}
}