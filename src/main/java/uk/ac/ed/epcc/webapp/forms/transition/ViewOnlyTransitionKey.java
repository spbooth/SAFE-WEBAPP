package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.servlet.ViewTransitionKey;

/** A {@link TransitionKey} that references a bookmarkable
 * read only transition.
 * This is a convenience class to make it easy to add a view transition
 * to a provider that uses {@link TransitionKey}s directly
 * 
 * 
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class ViewOnlyTransitionKey<T> extends TransitionKey<T> implements ViewTransitionKey<T> {

	public ViewOnlyTransitionKey(Class<? super T> t, String name, String help) {
		super(t, name, help);
	}

	public ViewOnlyTransitionKey(Class<? super T> t, String name) {
		super(t, name);
	}

}
