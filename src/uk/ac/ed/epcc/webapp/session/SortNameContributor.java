package uk.ac.ed.epcc.webapp.session;

import java.util.Comparator;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** An interface for {@link Composite}s that contribute to
 * the sorting order and name type
 * 
 */
public interface SortNameContributor<T> {

	/** Add terms to the sorting name in the
	 * order consistent with the sorting order
	 * 
	 * @param sb
	 * @return true if anything added.
	 */
	public boolean addSortName(T target,StringBuilder sb);
	
	/** Add {@link OrderClause} entries to the SQL default order
	 * 
	 * @param order
	 */
	public void addOrder(List<OrderClause> order);
	
	/** get a {@link Comparator} that compares target types
	 * according to the sort order
	 * 
	 * @return
	 */
	public Comparator<T> getComparator();
}
