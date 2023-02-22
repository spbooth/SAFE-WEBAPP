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


/** A {@link SQLFilter} that compares a {@link SQLExpression} to null.
 * 
 * Note that the class does not include any filter required by the expression so 
 * factory methods are used to ensure these are in place.
 * @author spb
 *
 * @param <T>
 * @param <V>
 */
public class SQLExpressionNullFilter<T,V> implements SQLFilter<T>, PatternFilter<T> {
	private final String filter_tag;
    private final SQLExpression<V> expr;
    private final boolean is_null;
    
    @SuppressWarnings("unchecked")
	public static <T,V> SQLFilter<T> getFilter(String target,SQLExpression<V> expr,boolean is_null){
    	SQLExpressionNullFilter<T, V> fil = new SQLExpressionNullFilter<T,V>(target, expr, is_null);
    	SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<>(target,fil,req);

    }
	private SQLExpressionNullFilter(String target,SQLExpression<V> expr,boolean is_null){
		this.filter_tag=target;
    	this.expr=expr;
    	this.is_null=is_null;
    }
	
	
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list=expr.getParameters(list);
		return list;
	}

	@Override
	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify) {
		//sb.append("(");
		expr.add(sb,qualify);
		if( is_null ){
			sb.append(" IS NULL");
		}else{
			sb.append(" IS NOT NULL");
		}
		return sb;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + (is_null ? 1231 : 1237);
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
		SQLExpressionNullFilter other = (SQLExpressionNullFilter) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (is_null != other.is_null)
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
		sb.append("SQLExpressionNullFilter(");
		sb.append(expr.toString());
		if( is_null) {
			sb.append("==null)");
		}else {
			sb.append("==null)");
		}
		return sb.toString();
	}
}