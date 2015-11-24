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

/** {@link SQLValue} that converts an {@link SQLValue} of a different type to an integer
 * Note that this will prevent SQL filtering.
 * @author spb
 *
 * @param <T> type of nested SQLAccessor
 */
@uk.ac.ed.epcc.webapp.Version("$Id: IntConvertSQLValue.java,v 1.4 2014/09/15 14:30:23 spb Exp $")

public class IntConvertSQLValue<T>  implements SQLValue<Integer> {
	private SQLValue<T> a;
    public IntConvertSQLValue(SQLValue<T> a){
    	this.a = a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Integer makeObject(ResultSet rs, int pos) throws DataException {
		T temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	if( temp instanceof Number ){
	    		return Integer.valueOf(((Number)temp).intValue());
	    	}
	    	if( temp instanceof String){
	    		return Integer.parseInt((String)temp);
	    	}
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Int(");
    	sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super Integer> getTarget() {
		return Integer.class;
	}
}