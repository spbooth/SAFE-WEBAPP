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

import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;



/** An equivalent to an {@link ArrayFuncExpression} that implements {@link DateSQLExpression}.
 * 
 * @author spb
 * @param <F> filter type
 * @param <T> type of expression
 */
public class DateArrayFuncExpression<F> implements DateSQLExpression {
	
	
    private final ArrayFunc func;
    private final DateSQLExpression e[];
    
    
    public DateArrayFuncExpression(ArrayFunc f, DateSQLExpression ... e){
    	assert(f!=null);
    	// expression may be null 
    	func=f;
    	this.e=e;
    }
	public int add(StringBuilder sb, boolean qualify) {
		int res=1;

		sb.append(func.name());
		sb.append("(");
		boolean seen=false;
		for(DateSQLExpression x : e){
			if( seen ){
				sb.append(",");
			}
			x.add(sb,qualify);
			seen=true;
		}
		sb.append(")");

		assert(res == 1);
		return res;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(DateSQLExpression x : e){
			x.getParameters(list);
		}
		return list;
	}
	
	@Override
	public String toString(){
		StringBuilder sb= new StringBuilder();
		sb.append(func.name());
		sb.append("(");
		boolean seen=false;
		for(DateSQLExpression x : e){
			if( seen ){
				sb.append(",");
			}
			sb.append(x.toString());
			seen=true;
		}
		sb.append(")");
		return sb.toString();
	}
	
	public Date makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return e[0].makeObject(rs,pos);
	}
	
	@SuppressWarnings("unchecked")
	public SQLFilter<F> getRequiredFilter() {
		SQLAndFilter<F> result = null;
		for(DateSQLExpression x: e){
			SQLFilter fil = x.getRequiredFilter();
			if(fil != null){
				if( result == null){
					result = new SQLAndFilter<>(getFilterTag());
				}
				result.addFilter(fil);
			}
		}
		return result;
	}
	private String filter_tag = null;
	@Override
	public String getFilterTag() {
		if( filter_tag != null) {
			return filter_tag;
		}
		for(SQLExpression exp : e) {
			String t = exp.getFilterTag();
			if( t!= null) {
				filter_tag=t;
				return t;
			}
		}
		return null;
	}
	@Override
	public Class<Date> getTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SQLExpression<? extends Number> getMillis() {
		SQLExpression[] tmp = new SQLExpression[e.length];
		for(int i=0; i< e.length ; i++) {
			tmp[i] = e[i].getMillis();
		}
		return new ArrayFuncExpression<>(func, tmp[0].getTarget(), tmp);
	}
	@Override
	public SQLExpression<? extends Number> getSeconds() {
		SQLExpression[] tmp = new SQLExpression[e.length];
		for(int i=0; i< e.length ; i++) {
			tmp[i] = e[i].getSeconds();
		}
		return new ArrayFuncExpression<>(func, tmp[0].getTarget(), tmp);
	}
	@Override
	public boolean preferSeconds() {
		for(DateSQLExpression d : e) {
			if( ! d.preferSeconds()) {
				return false;
			}
		}
		return true;
	}

}