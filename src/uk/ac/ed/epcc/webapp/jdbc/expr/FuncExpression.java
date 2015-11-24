// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
@uk.ac.ed.epcc.webapp.Version("$Id: FuncExpression.java,v 1.5 2014/09/15 14:30:23 spb Exp $")



public class FuncExpression<T> implements SQLExpression<T> {
	
	public static <T> SQLExpression<T> apply(AppContext c,SQLFunc f, Class<T> target, SQLExpression<? extends T> e){
		if(c != null &&  e instanceof DateSQLExpression){
			// move date convert outside function
			DateSQLExpression dse = (DateSQLExpression) e;
			try{
				SQLContext sqc = c.getService(DatabaseService.class).getSQLContext();
				return (SQLExpression<T>) sqc.convertToDate(new FuncExpression<Number>(f, Number.class, dse.getSeconds()), 1000L);
			}catch(SQLException ee){
				c.getService(LoggerService.class).getLogger(FuncExpression.class).error("Error getting SQLContext",ee);
			}
		}
		return new FuncExpression<T>(f, target, e);
	}
    private final SQLFunc func;
    private final SQLExpression<? extends T> e;
    private final Class<T> target_class;
    private FuncExpression(SQLFunc f, Class<T> target, SQLExpression<? extends T> e){
    	assert(f!=null);
    	// expression may be null 
    	func=f;
    	this.target_class=target;
    	this.e=e;
    }
	public int add(StringBuilder sb, boolean qualify) {
		int res=1;

		sb.append(func.name());
		sb.append("(");
		if( e == null){
			sb.append("1");
		}else{
			e.add(sb, qualify);
		}
		sb.append(")");

		assert(res == 1);
		return res;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		if( e == null ){
			return list;
		}else{
			return e.getParameters(list);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb= new StringBuilder();
		sb.append(func.name());
		sb.append("(");
		if( e != null){
			sb.append(e.toString());
		}
		sb.append(")");
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	public T makeObject(ResultSet rs, int pos) throws DataException {
		if( e != null ){
			return e.makeObject(rs,pos);
		}else{
			// count has null expression
			try {
				return (T) rs.getObject(pos);
			} catch (SQLException e1) {
				throw new DataException("Error making FuncExpression via default", e1);
			}
		}
	}
	public Class<? super T> getTarget() {
		return target_class;
	}
	public SQLFilter getRequiredFilter() {
		if( e == null ){
			return null;
		}
		return e.getRequiredFilter();
	}

}