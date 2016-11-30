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

import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.FieldValue;


/** A {@link SQLFilter} that compares a {@link SQLExpression} to a constant value.
 * 
 * Note this does not include any filter required by the {@link SQLExpression} so factory methods are
 * used to ensure these are in place.
 * @param <T> Type of filter
 * @param <V> Type of expression
 */
public class SQLExpressionFilter<T,V> implements SQLFilter<T>, PatternFilter<T> {
	private final Class<? super T> target;
    private final SQLExpression<V> expr;
    private final V value;
    private final MatchCondition match;

    @SuppressWarnings("unchecked")
	public static <T,V> SQLFilter<T> getFilter(Class<? super T> target,SQLExpression<V> expr,MatchCondition m,V value){
    	SQLExpressionFilter<T, V> fil;
    	if( expr instanceof DateSQLExpression){
    		fil = new SQLExpressionFilter(target, ((DateSQLExpression) expr).getMillis(), m,((Date)value).getTime());
    	}else{
    		fil = new SQLExpressionFilter<>(target, expr, m,value);
    	}
		SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<T>(target,fil,req);
    }
    @SuppressWarnings("unchecked")
	public static <T,V> SQLFilter<T> getFilter(Class<? super T> target,SQLExpression<V> expr,V value){
    	SQLExpressionFilter<T, V> fil = new SQLExpressionFilter<>(target, expr, value);
		SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<T>(target,fil,req);
    }
    private SQLExpressionFilter(Class<? super T> target,SQLExpression<V> expr,V value){
		this.target=target;
    	this.expr=expr;
    	this.match=null;
    	this.value=value;
    }
	private SQLExpressionFilter(Class<? super T> target,SQLExpression<V> expr,MatchCondition match,V value){
		this.target=target;
    	this.expr=expr;
    	this.match=match;
    	this.value=value;
    }

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list=expr.getParameters(list);
		list.add(new PatternArgument(){

		
			@SuppressWarnings("unchecked")
			public void addArg(PreparedStatement stmt, int pos)
					throws SQLException {
				if( expr instanceof FieldValue){
					((FieldValue) expr).setObject(stmt, pos, value);
				}else{
					stmt.setObject(pos, value);
				}
			}

			public boolean canLog() {
				return true;
			}

			public Object getArg() {
				return value;
			}

			public String getField() {
				if( expr instanceof FieldValue){
					return ((FieldValue)expr).getFieldName();
				}
				return "<expression>";
			}
			
		});
		return list;
	}

	public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
		sb.append("(");
		expr.add(sb,qualify);
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
	

}