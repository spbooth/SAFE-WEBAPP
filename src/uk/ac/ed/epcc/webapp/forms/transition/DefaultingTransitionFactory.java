package uk.ac.ed.epcc.webapp.forms.transition;


/** TransitionProvider that can generate a default transition for a target
 * if no other transition is specified.
 * @author spb
 *
 * @param <K>
 * @param <T>
 */
public interface DefaultingTransitionFactory<K, T> extends
		TransitionFactory<K, T> {

	/** Get the key for the default transition.
	 * 
	 * This method can return null if a sub-class wants to supress the default transition or if
	 * there is no valid default for the target.
	 * @param target 
	 * @return transition key
	 */
	public K getDefaultTransition(T target);
}
