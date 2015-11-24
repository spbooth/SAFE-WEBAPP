// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import java.util.Set;

import uk.ac.ed.epcc.webapp.Contexed;

/** Interface for tests that want to run generic transition tests
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface TransitionFactoryDataProvider<K,T> extends Contexed{

	public TransitionFactory<K, T> getTransitionFactory();
	/** Get a set of targets to test transitions on.
	 * 
	 * @return Set of targets-
	 */
	public Set<T> getTargets() throws Exception;
}
