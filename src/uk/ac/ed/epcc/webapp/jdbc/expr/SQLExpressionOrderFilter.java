package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** An {@link OrderFilter} implemented using {@link SQLExpression}s.
 * 
 * @author spb
 *
 * @param <I>
 * @param <T>
 */
public class SQLExpressionOrderFilter<I,T> implements OrderFilter<T> , SQLFilter<T>{
	private final Class<? super T> target;
	private final boolean descending;
	private final SQLExpression<I> expr;
	public SQLExpressionOrderFilter(Class<? super T> target,boolean descending,SQLExpression<I> expr) {
		this.target=target;
		this.descending=descending;
		this.expr=expr;
	}
	
	public List<OrderClause> OrderBy() {
		LinkedList<OrderClause> result = new LinkedList<OrderClause>();
		result.add(new SQLExpressionOrderClause<I>(descending,expr));
		return result;
	}
	public void accept(T o) {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitOrderFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}

}
