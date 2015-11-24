package uk.ac.ed.epcc.webapp.forms.transition;


/** TransitionProvider that can generate a default indexing transition
 * i.e. a {@link TargetLessTransition} for selecting a target or a navigation transition
 * to an index page.
 * 
 * @author spb
 *
 * @param <K>
 * @param <T>
 */
public interface IndexTransitionProvider<K, T> extends
		IndexTransitionFactory<K, T> , TransitionProvider<K, T>{

}
