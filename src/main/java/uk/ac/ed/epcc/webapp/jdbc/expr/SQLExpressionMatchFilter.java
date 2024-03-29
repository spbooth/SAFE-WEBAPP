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

import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.model.data.Repository;


/** A {@link SQLFilter} that compares two {@link SQLExpression}s.
 * 
 * Note these do not include any filter required by the {@link SQLExpression} so factory methods are used to ensure these
 * are in place.
 * 
 * @author spb
 *
 * @param <T> type of target
 * @param <V> type of expression
 */
public class SQLExpressionMatchFilter<T,V> implements SQLFilter<T>, PatternFilter<T> {
	private final String filter_tag;
    private final SQLExpression<? extends V> expr1;
    private final SQLExpression<? extends V> expr2;
  
    private final MatchCondition match;
    
    @SuppressWarnings("unchecked")
	public static <T,V>  SQLFilter<T> getFilter(String target,SQLExpression<? extends V> expr1,SQLExpression<? extends V> expr2){
    	if( expr1 instanceof ConstExpression &&  expr1 instanceof ConstExpression){
    		ConstExpression<V,T> const1 = (ConstExpression<V,T>) expr1;
    		ConstExpression<V,T> const2 = (ConstExpression<V,T>) expr2;
    		return new GenericBinaryFilter<>(const1.getValue().equals(const2.getValue()));
    	}
    	if( expr1 instanceof DateSQLExpression && expr2 instanceof DateSQLExpression){
    		// compare underlying value
    		return getFilter(target, ((DateSQLExpression)expr1).getMillis(), ((DateSQLExpression)expr2).getMillis());
    	}
    	
    	SQLExpressionMatchFilter<T, V> fil = new SQLExpressionMatchFilter<T,V>(target, expr1, expr2);
    	SQLFilter<T> req1 = expr1.getRequiredFilter();
    	SQLFilter<T> req2 = expr1.getRequiredFilter();
    	if( req1 == null && req2 == null){
    		return fil;
    	}
    	return new SQLAndFilter<>(target,fil,req1,req2);

    }
    @SuppressWarnings("unchecked")
	public static <T,V>  SQLFilter<T> getFilter(String target,SQLExpression<? extends V> expr1,MatchCondition m,SQLExpression<? extends V> expr2){
    	if( expr1 instanceof ConstExpression &&  expr1 instanceof ConstExpression){
    		ConstExpression<V,T> const1 = (ConstExpression<V,T>) expr1;
    		ConstExpression<V,T> const2 = (ConstExpression<V,T>) expr2;
    		return new GenericBinaryFilter<>(m.compare(const1.getValue(), const2.getValue()));
    	}
    	if( expr1 instanceof DateSQLExpression && expr2 instanceof DateSQLExpression){
    		// compare underlying value
    		DateSQLExpression dse1 = (DateSQLExpression)expr1;
			DateSQLExpression dse2 = (DateSQLExpression)expr2;
			if( dse1.preferSeconds()){
				return getFilter(target, dse1.getSeconds(), m, dse2.getSeconds());
			}
			return getFilter(target, dse1.getMillis(), m, dse2.getMillis());
    	}
    	
    	SQLExpressionMatchFilter<T, V> fil = new SQLExpressionMatchFilter<>(target, expr1, m,expr2);
    	SQLFilter<T> req1 = expr1.getRequiredFilter();
    	SQLFilter<T> req2 = expr1.getRequiredFilter();
    	if( req1 == null && req2 == null){
    		return fil;
    	}
    	return new SQLAndFilter<>(target,fil,req1,req2);

    }
	private SQLExpressionMatchFilter(String target,SQLExpression<? extends V> expr1,SQLExpression<? extends V> expr2){
		this.filter_tag=target;
    	this.expr1=expr1;
    	this.expr2=expr2;
    	this.match=null;
    }
	private SQLExpressionMatchFilter(String target,SQLExpression<? extends V> expr1,MatchCondition match,SQLExpression<? extends V> expr2){
		this.filter_tag=target;
    	this.expr1=expr1;
    	this.match=match;
    	this.expr2=expr2;
    }

	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list=expr1.getParameters(list);
		list=expr2.getParameters(list);
		
		return list;
	}

	@Override
	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify) {
		//sb.append("(");
		expr1.add(sb,qualify);
		if( match != null){
			sb.append(match.match());
		}else{
		    sb.append("=");
		}
		expr2.add(sb, qualify);
		//sb.append(")");
		return sb;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr1 == null) ? 0 : expr1.hashCode());
		result = prime * result + ((expr2 == null) ? 0 : expr2.hashCode());
		result = prime * result + ((match == null) ? 0 : match.hashCode());
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
		SQLExpressionMatchFilter other = (SQLExpressionMatchFilter) obj;
		if (expr1 == null) {
			if (other.expr1 != null)
				return false;
		} else if (!expr1.equals(other.expr1))
			return false;
		if (expr2 == null) {
			if (other.expr2 != null)
				return false;
		} else if (!expr2.equals(other.expr2))
			return false;
		if (match != other.match)
			return false;
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return filter_tag;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SQLExpressionMatchFilter(");
		sb.append(expr1.toString());
		if( match != null){
			sb.append(match.match());
		}else{
		    sb.append("=");
		}
		sb.append(expr2.toString());
		sb.append(")");
		return sb.toString();
	}
}