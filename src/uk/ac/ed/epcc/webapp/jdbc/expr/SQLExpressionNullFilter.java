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

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;


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
	private final Class<? super T> target;
    private final SQLExpression<V> expr;
    private final boolean is_null;
    
    @SuppressWarnings("unchecked")
	public static <T,V> SQLFilter<T> getFilter(Class<? super T> target,SQLExpression<V> expr,boolean is_null){
    	SQLExpressionNullFilter<T, V> fil = new SQLExpressionNullFilter<>(target, expr, is_null);
    	SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<T>(target,fil,req);

    }
	private SQLExpressionNullFilter(Class<? super T> target,SQLExpression<V> expr,boolean is_null){
		this.target=target;
    	this.expr=expr;
    	this.is_null=is_null;
    }
	
	
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list=expr.getParameters(list);
		return list;
	}

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
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
	 */
	public void accept(T o) {
		
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}
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