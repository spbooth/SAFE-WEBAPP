// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface ShowDisabledTransitions<K,T> extends TransitionFactory<K, T> {
	/** If the transition is NOT allowed but this method returns true the button is stills hown but disabled.
	 * @param c AppContext
	 * @param target target object
	 * @param key identifying key object for transition
	 * @return boolean is operation allowed
	 */
	public boolean showDisabledTransition(AppContext c,T target,K key);
}
