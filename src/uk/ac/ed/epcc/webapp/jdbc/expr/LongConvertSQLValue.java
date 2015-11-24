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
@uk.ac.ed.epcc.webapp.Version("$Id: LongConvertSQLValue.java,v 1.1 2015/04/03 19:43:20 spb Exp $")

public class LongConvertSQLValue<T>  implements SQLValue<Long> {
	private SQLValue<T> a;
    public LongConvertSQLValue(SQLValue<T> a){
    	this.a = a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Long makeObject(ResultSet rs, int pos) throws DataException {
		T temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	if( temp instanceof Number ){
	    		return Long.valueOf(((Number)temp).longValue());
	    	}
	    	if( temp instanceof String){
	    		return Long.parseLong((String)temp);
	    	}
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Long(");
    	sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super Long> getTarget() {
		return Long.class;
	}
}