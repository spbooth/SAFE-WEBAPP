// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
@uk.ac.ed.epcc.webapp.Version("$Id: SQLExpressionMatchFilter.java,v 1.6 2014/09/15 14:30:24 spb Exp $")


public class SQLExpressionMatchFilter<T,V> implements SQLFilter<T>, PatternFilter<T> {
	private final Class<? super T> target;
    private final SQLExpression<V> expr1;
    private final SQLExpression<V> expr2;
  
    private final MatchCondition match;
	public SQLExpressionMatchFilter(Class<? super T> target,SQLExpression<V> expr1,SQLExpression<V> expr2){
		this.target=target;
    	this.expr1=expr1;
    	this.expr2=expr2;
    	this.match=null;
    }
	public SQLExpressionMatchFilter(Class<? super T> target,SQLExpression<V> expr1,MatchCondition match,SQLExpression<V> expr2){
		this.target=target;
    	this.expr1=expr1;
    	this.match=match;
    	this.expr2=expr2;
    }

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list=expr1.getParameters(list);
		list=expr2.getParameters(list);
		
		return list;
	}

	public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
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