package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
/** A {@link ChainedTransitionResult} that uses redirect rather than forward.
 * 
 * @author Stephen Booth
 *
 * @param <T>
 * @param <K>
 */
public class RedirectChainedTransitionResult<T,K> extends ChainedTransitionResult<T, K> {

	public RedirectChainedTransitionResult(AppContext conn, Class<? extends TransitionFactory> template, String tag,
			T target, K next) {
		super(conn, template, tag, target, next);
	}

	public RedirectChainedTransitionResult(TransitionFactory<K, T> provider, T target, K next) {
		super(provider, target, next);
	}

	@Override
	public final boolean useURL() {
		return true;
	}

}
