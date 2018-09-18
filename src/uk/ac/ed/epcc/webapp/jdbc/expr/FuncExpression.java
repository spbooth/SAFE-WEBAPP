//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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



/** A {@link SQLExpression} that applies a {@link SQLFunc} to
 * and inner {@link SQLExpression}. if the inner expression is null a constant
 * value <b>1</b> is used.
 * 
 * @author spb
 *
 * @param <T>
 */
public class FuncExpression<T> implements SQLExpression<T> {
	
	public static <T> SQLExpression<T> apply(AppContext c,SQLFunc f, Class<T> target, SQLExpression<? extends T> e){
		if(c != null &&  e instanceof DateSQLExpression){
			// move date convert outside function
			DateSQLExpression dse = (DateSQLExpression) e;
			DatabaseService db_serv = c.getService(DatabaseService.class);
			try{
				
				SQLContext sqc = db_serv.getSQLContext();
				return (SQLExpression<T>) sqc.convertToDate(new FuncExpression<Number>(f, Number.class, dse.getSeconds()), 1000L);
			}catch(SQLException ee){
				db_serv.logError("Error getting SQLContext",ee);
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
	public T makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		if( e != null ){
			return e.makeObject(rs,pos);
		}else{
			// count has null expression
			return (T) rs.getObject(pos);
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