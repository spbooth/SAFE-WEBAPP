// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.table;

/** A {@link TableTransitionRegistry} that can be augmented with additionanal transitions.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CompositeTableTransitionRegistry.java,v 1.2 2014/09/15 14:30:26 spb Exp $")
public interface CompositeTableTransitionRegistry extends TableTransitionRegistry{

	public abstract <X extends TableTransitionTarget> void addTransitionSource(
			TransitionSource<X> source);

}