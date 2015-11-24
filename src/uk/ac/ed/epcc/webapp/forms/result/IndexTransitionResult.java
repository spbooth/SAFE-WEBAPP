package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory;

/** A FormResult generating an IndexTransition
 * 
 * @author spb
 *
 * @param <T>
 * @param <K>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: IndexTransitionResult.java,v 1.3 2014/09/15 14:30:21 spb Exp $")
public class IndexTransitionResult<T, K> extends ChainedTransitionResult<T, K> {

	@Override
	public boolean useURL() {
		return true;
	}

	public IndexTransitionResult(IndexTransitionFactory<K, T> provider) {
		super(provider, null,null);
		
	}

}
