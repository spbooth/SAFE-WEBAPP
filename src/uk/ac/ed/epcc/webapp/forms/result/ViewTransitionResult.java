// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;

/** A Form result generating a View Transition.
 * 
 * This is essentially a {@link ChainedTransitionResult} with a null key but with the destination 
 * a "bookmarkable" location so in HTML it uses a redirect not a forward. The {@link TransitionServlet}
 * will convert this to a plain {@link ChainedTransitionResult} before processing. In non web contexts the
 * two representations are identical. 
 * @author spb
 *
 * @param <T>
 * @param <K>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ViewTransitionResult.java,v 1.3 2014/09/15 14:30:21 spb Exp $")

public class ViewTransitionResult<T,K> extends ChainedTransitionResult<T, K> {

	@Override
	public boolean useURL() {
		return true;
	}

	public ViewTransitionResult(ViewTransitionFactory<K, T> provider, T target) {
		super(provider, target, null);
	}

}