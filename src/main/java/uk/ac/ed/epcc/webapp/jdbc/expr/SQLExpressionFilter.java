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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.model.data.FieldValue;
import uk.ac.ed.epcc.webapp.model.data.Repository;


/** A {@link SQLFilter} that compares a {@link SQLExpression} to a constant value.
 * 
 * Note this does not include any filter required by the {@link SQLExpression} so factory methods are
 * used to ensure these are in place.
 * @param <T> Type of filter
 * @param <V> Type of expression
 */
public class SQLExpressionFilter<T,V> implements SQLFilter<T>, PatternFilter<T> {
    private final SQLExpression<V> expr;
    private final V value;
    private final MatchCondition match;

    @SuppressWarnings("unchecked")
	public static <T,V> SQLFilter<T> getFilter(SQLExpression<V> expr,MatchCondition m,V value){
    	SQLExpressionFilter<T, V> fil;
    	if( expr instanceof DateSQLExpression){
    		DateSQLExpression dse = (DateSQLExpression) expr;
    		if( dse.preferSeconds()){
    			fil = new SQLExpressionFilter(dse.getSeconds(), m,((Date)value).getTime()/1000L);
    		}else{
    			fil = new SQLExpressionFilter(dse.getMillis(), m,((Date)value).getTime());
    		}
    	}else{
    		fil = new SQLExpressionFilter<>(expr, m,value);
    	}
		SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<>(expr.getFilterTag(),fil,req);
    }
    @SuppressWarnings("unchecked")
	public static <T,V> SQLFilter<T> getFilter(SQLExpression<V> expr,V value){
    	SQLExpressionFilter<T, V> fil = new SQLExpressionFilter<>(expr, value);
		SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<>(expr.getFilterTag(),fil,req);
    }
    private SQLExpressionFilter(SQLExpression<V> expr,V value){
    	this.expr=expr;
    	this.match=null;
    	this.value=value;
    }
	private SQLExpressionFilter(SQLExpression<V> expr,MatchCondition match,V value){
    	this.expr=expr;
    	this.match=match;
    	this.value=value;
    }

	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list=expr.getParameters(list);
		list.add(new PatternArgument(){

		
			@Override
			@SuppressWarnings("unchecked")
			public void addArg(PreparedStatement stmt, int pos)
					throws SQLException {
				if( expr instanceof FieldValue){
					((FieldValue) expr).setObject(stmt, pos, value);
				}else{
					Repository.setObject(stmt,pos, value);
				}
			}

			@Override
			public boolean canLog() {
				return true;
			}

			@Override
			public Object getArg() {
				return value;
			}

			@Override
			public String getField() {
				if( expr instanceof FieldValue){
					return ((FieldValue)expr).getFieldName();
				}
				return "<expression>";
			}
			
		});
		return list;
	}

	@Override
	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify) {
		sb.append("(");
		if( expr instanceof FieldValue){
			((FieldValue)expr).addField(sb, qualify);
		}else {
			expr.add(sb,qualify);
		}
		if( match != null){
			sb.append(match.match());
		}else{
		    sb.append("=");
		}
		sb.append("?)");
		return sb;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + ((match == null) ? 0 : match.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SQLExpressionFilter other = (SQLExpressionFilter) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (match != other.match)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SQLExpressionFilter(");
		sb.append(expr.toString());
		if( match != null){
			sb.append(match.match());
		}else{
		    sb.append("=");
		}
		sb.append(value.toString());
		sb.append(")");
		return sb.toString();
	}
	@Override
	public String getTag() {
		return expr.getFilterTag();
	}

}