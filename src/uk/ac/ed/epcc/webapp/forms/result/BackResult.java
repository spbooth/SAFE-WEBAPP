// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;

/** FormResult indicating that navigation should return to the
 * most recently viewed ViewTransition target.
 * 
 * @author spb
 * @param <K> transition key
 * @param <T> transition target
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: BackResult.java,v 1.3 2014/09/15 14:30:21 spb Exp $")

public final class BackResult<K,T> implements FormResult {
	public final ViewTransitionFactory<K, T> provider;
	public final FormResult fallback;
	public BackResult(ViewTransitionFactory<K, T> provider, FormResult fallback){
		this.provider=provider;
		this.fallback=fallback;
	}
	public void accept(FormResultVisitor vis) throws Exception {
		vis.visitBackResult(this);
	}

}