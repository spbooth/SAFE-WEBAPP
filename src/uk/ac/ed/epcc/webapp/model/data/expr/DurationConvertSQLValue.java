// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Duration;

/** SQLAccessor that converts an SQLAccessor of a different type to a Duration
 * 
 * @author spb
 *
 * @param <T> type of nested SQLAccessor
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DurationConvertSQLValue.java,v 1.10 2014/09/15 14:30:30 spb Exp $")

public class DurationConvertSQLValue<T extends Number>  implements SQLValue<Duration> {
	private final SQLValue<T> a;
	private final long resolution;
    public DurationConvertSQLValue(SQLValue<T> a,long resolution){
    	this.a = a;
    	this.resolution=resolution;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Duration makeObject(ResultSet rs, int pos) throws DataException {
		T temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	if( temp instanceof Number ){
	    		return new Duration((Number)temp,resolution);
	    	}
	    	
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Duration(");
		sb.append(a.toString());
		sb.append(",");
		sb.append(Long.toString(resolution));
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super Duration> getTarget() {
		return Duration.class;
	}
}