// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** A resultMapper that returns a single value specified by a SQLExpression
 * 
 * @author spb
 *
 * @param <O>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ValueResultMapper.java,v 1.3 2014/09/15 14:30:24 spb Exp $")

public class ValueResultMapper<O> implements ResultMapper<O> {
    private SQLValue<O> expr;
    private boolean qualify=false;
    public ValueResultMapper(SQLValue<O> e){
    	assert(e != null);
    	expr=e;
    }
	public String getModify() {
		return null;
	}

	public String getTarget(){
		StringBuilder sb = new StringBuilder();
		expr.add(sb, qualify);
		return sb.toString();
	}

	public O makeDefault() {
		return null;
	}

	public O makeObject(ResultSet rs) throws DataException {
		O res =  expr.makeObject(rs, 1);
		return res;
	}

	public boolean setQualify(boolean qualify) {
		boolean old=qualify;
		this.qualify=qualify;
		return old;
	}
	public SQLFilter getRequiredFilter() {
		return expr.getRequiredFilter();
	}
	public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
		return expr.getParameters(list);
	}
	public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
		return list;
	}

}