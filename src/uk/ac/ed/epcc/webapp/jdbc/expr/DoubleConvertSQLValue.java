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

/** SQLAccessor that converts an SQLAccessor of a different type to a double
 * 
 * @author spb
 *
 * @param <T> type of nested SQLAccessor
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DoubleConvertSQLValue.java,v 1.3 2014/09/15 14:30:23 spb Exp $")

public class DoubleConvertSQLValue<T>  implements SQLValue<Double> {
	private SQLValue<T> a;
    public DoubleConvertSQLValue(SQLValue<T> a){
    	this.a = a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Double makeObject(ResultSet rs, int pos) throws DataException {
		T temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	if( temp instanceof Number ){
	    		return Double.valueOf(((Number)temp).doubleValue());
	    	}
	    	if( temp instanceof String){
	    		return Double.parseDouble((String)temp);
	    	}
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Double(");
    	sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super Double> getTarget() {
		return Double.class;
	}
}