//| Copyright - The University of Edinburgh 2015                            |
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

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrderFilter;
/** An {@link OrderFilter} implemented directly using {@link SQLExpression}s.
 * 
 * 
 * @author spb
 *
 * @param <I>
 * @param <T>
 */
public class SQLExpressionOrderFilter<I,T> implements SQLOrderFilter<T> {
	private final Class<T> target;
	private final boolean descending;
	private final SQLExpression<I> expr;
	@SuppressWarnings("unchecked")
	public static <I,T> SQLFilter<T> getFilter(Class<T> target,boolean descending,SQLExpression<I> expr) {
		SQLExpressionOrderFilter<I, T> fil = new SQLExpressionOrderFilter<>(target, descending, expr);
		SQLFilter<T> req = expr.getRequiredFilter();
    	if( req == null){
    		return fil;
    	}
    	return new SQLAndFilter<>(target,fil,req);

	}
	private SQLExpressionOrderFilter(Class<T> target,boolean descending,SQLExpression<I> expr) {
		this.target=target;
		this.descending=descending;
		this.expr=expr;
	}
	
	@Override
	public List<OrderClause> OrderBy() {
		LinkedList<OrderClause> result = new LinkedList<>();
		result.add(new SQLExpressionOrderClause<>(descending,expr));
		return result;
	}
	@Override
	public void accept(T o) {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X, T> vis) throws Exception {
		return vis.visitOrderFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<T> getTarget() {
		return target;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (descending ? 1231 : 1237);
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
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
		SQLExpressionOrderFilter other = (SQLExpressionOrderFilter) obj;
		if (descending != other.descending)
			return false;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}