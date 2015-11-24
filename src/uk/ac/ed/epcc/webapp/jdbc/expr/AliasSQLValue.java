// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A SQLValue decorator that adds an alias name to an SQL expression.
 * This allows a display name to be encoded in the ResultSet reducing the additional parameters that
 * need to be passed around in Java
 * 
 * @author spb
 *
 * @param <T> Java type produced
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AliasSQLValue.java,v 1.4 2014/09/15 14:30:22 spb Exp $")

public final class AliasSQLValue<T> implements GroupingSQLValue<T> {
    private final SQLExpression<T> exp;
    private final String alias;
    public AliasSQLValue(SQLExpression<T> e, String name){
    	exp=e;
    	alias=name;
    	if( exp == null ){
    		throw new ConsistencyError("Null expression passed to AliasSQLExpression");
    	}
    }
	public int add(StringBuilder sb, boolean qualify) {
		int res = exp.add(sb,qualify);
		if(alias != null){
			sb.append(" AS `");
			sb.append(alias);
			sb.append("`");
		}
		// its an expression it should be a single value.
		assert(res == 1);
		return res;
	}
	/** add just the name of the field (for group by)
	 * 
	 * @param sb
	 * @param qualify 
	 */
	public int addGroup(StringBuilder sb,boolean qualify){
		if( alias == null ){
			exp.add(sb, qualify);
		}else{
			sb.append("`");
			sb.append(alias);
			sb.append("`");
		}
		return 1;
	}
	public T makeObject(ResultSet rs, int pos) throws DataException {
		return exp.makeObject(rs, pos);
	}

	@Override
	public String toString(){
		if( alias == null ){
			return exp.toString();
		}
		return exp.toString()+" AS "+alias;
	}
	public Class<? super T> getTarget() {
		return exp.getTarget();
	}
	public SQLFilter getRequiredFilter() {
		return exp.getRequiredFilter();
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return exp.getParameters(list);
	}
	public List<PatternArgument> getGroupParameters(List<PatternArgument> list) {
		if( alias == null ){
			return exp.getParameters(list);
		}else{
			return list;
		}
	}
}