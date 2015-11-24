// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;

/** A variant of {@link SQLValue} which can produce an alternative
 * SQL fragment for group-by clauses. This allows complex transformations
 * that do not change the grouping result to be supressed
 * 
 * 
 * @author spb
 * @param <T> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: GroupingSQLValue.java,v 1.2 2014/09/15 14:30:23 spb Exp $")
public interface GroupingSQLValue<T> extends SQLValue<T> {

	/** Add the group-by clause to a query.
	 * Note this can be a null operation in which case it
	 * will return zero.
	 * 
	 * @param sb
	 * @param qualify
	 * @return actual number of fields added
	 */
	public int addGroup(StringBuilder sb,boolean qualify);
	/** Get the parameters for a group-by clause.
	 * 
	 * @param list
	 * @return modified list
	 */
	public List<PatternArgument> getGroupParameters(List<PatternArgument> list);
}
