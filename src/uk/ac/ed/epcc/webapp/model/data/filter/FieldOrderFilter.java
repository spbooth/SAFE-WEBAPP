// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** An OrderFilter that uses a single field
 * 
 * This is for modification loops that might change the default ordering and therefore 
 * might fall foul of the chunking code.
 * 
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FieldOrderFilter.java,v 1.5 2014/09/15 14:30:30 spb Exp $")

public class FieldOrderFilter<T> implements OrderFilter<T>, SQLFilter<T> {

	private final Class<? super T> target;
	private final Repository res;
	private final String name;
	private final boolean descending;
	public FieldOrderFilter(Class<? super T> target,Repository res,String name,boolean descending){
		this.target=target;
		this.res=res;
		this.name=name;
		this.descending=descending;
	}
	public final List<OrderClause> OrderBy() {
		LinkedList<OrderClause> order = new LinkedList<OrderClause>();
		order.add(res.getOrder(name, descending));
		return order;
	}
	
	
	public void accept(T o) {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public final <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitOrderFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}

}