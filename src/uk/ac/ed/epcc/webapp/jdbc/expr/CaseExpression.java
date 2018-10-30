//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.GetListFilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.MakeSelectVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** Combines SQLExpressions as a CASE statement.
 * A CASE expression consists of a series of expressions with corresponding {@link SQLFilter}s for which records they apply to
 * and a default {@link SQLExpression} when no filters applu.
 * @author spb
 * 
 * @param <X> type of filter
 * @param <R> return type
 *
 */

public class CaseExpression<X,R> implements SQLExpression<R> {
	/** encode one clause of the expression
	 * 
	 * @author spb
	 *
	 * @param <T> type of filter
	 * @param <V> type of value
	 */
    public static class Clause<T,V>{
    	/**
		 * @param filter
		 * @param value
		 */
		public Clause(SQLFilter<T> filter, SQLExpression<? extends V> value) {
			super();
			this.filter = filter;
			this.value = value;
		}
	
		
		public final SQLFilter<T> filter;
    	public final SQLExpression<? extends V> value;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((filter == null) ? 0 : filter.hashCode());
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
			Clause other = (Clause) obj;
			if (filter == null) {
				if (other.filter != null)
					return false;
			} else if (!filter.equals(other.filter))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
    	
    }
	
    
   
    private final Class<R> target;
	private final SQLExpression<? extends R> default_expr;
	private final LinkedList<Clause<X,R>> options;
	
	/**
	 * @param default_expression 
	 * @param clauses 
	 * 
	 */
	public CaseExpression(Class<R> target,SQLExpression<? extends R> default_expression, Clause<X,R> ... clauses ) {
		this.target=target;
		this.default_expr=default_expression;
		this.options=new LinkedList<CaseExpression.Clause<X,R>>();
		for(Clause<X,R> c : clauses ){
			options.add(c);
		}
	}
	public CaseExpression(Class<R> target,SQLExpression<? extends R> default_expression, LinkedList<Clause<X,R>> clauses  ) {
		this.target=target;
		this.default_expr=default_expression;
		this.options=new LinkedList<CaseExpression.Clause<X,R>>(clauses);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("CASE ");
		// We only support simple expression filters here so ok to pass
		// null for the tables parameter
		MakeSelectVisitor<X> vis = new MakeSelectVisitor<X>(null,sb, qualify,true);
		for( Clause<X,R> c: options){
			sb.append("WHEN ");
			try {
				sb = c.filter.acceptVisitor(vis);
			} catch (Exception e) {
				// can't recover from this
				throw new ConsistencyError("Bad filter", e);
			}
			sb.append(" THEN ");
			c.value.add(sb, qualify);
		}
		if( default_expr != null ){
			sb.append(" ELSE ");
			default_expr.add(sb, qualify);
		}
		sb.append(" END");
		return 1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for( Clause<X,R> c: options){
			GetListFilterVisitor<X> vis = new GetListFilterVisitor<X>(list,true);
			try{
				list = c.filter.acceptVisitor(vis);
			} catch (Exception e) {
				// can't recover from this
				throw new ConsistencyError("Bad filter", e);
			}
			list = c.value.getParameters(list);
		}
		if( default_expr != null ){
			list = default_expr.getParameters(list);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public R makeObject(ResultSet rs, int pos) throws DataException, SQLException {

		return (R)  rs.getObject(pos);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<R> getTarget() {
		return target;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((default_expr == null) ? 0 : default_expr.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		CaseExpression other = (CaseExpression) obj;
		if (default_expr == null) {
			if (other.default_expr != null)
				return false;
		} else if (!default_expr.equals(other.default_expr))
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}