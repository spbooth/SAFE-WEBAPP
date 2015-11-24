package uk.ac.ed.epcc.webapp.forms.transition;


/** TransitionProvider that can generate a default transition
 * i.e. a {@link TargetLessTransition} for selecting a target or a navigation transition
 * to an index page.
 * It can also be used to supply a default transition for a target
 * 
 * @author spb
 *
 * @param <K>
 * @param <T>
 */
public interface IndexTransitionFactory<K, T> extends
		TransitionFactory<K, T> {

	/** Get the key for the default index transition.
	 * 
	 * This method can return null if a sub-class wants to supress the default transition.
	 * 
	 * @return transition key
	 */
	public K getIndexTransition();
}
