// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;

@uk.ac.ed.epcc.webapp.Version("$Id: NestAction.java,v 1.3 2014/09/15 14:30:17 spb Exp $")


/** A {@link FormAction} that ignores the current form and chains to a different transition
 * on the target object. 
 * 
 * This is used to implement cancel/abort  buttons.
 * 
 * @author spb
 *
 * @param <K>
 * @param <T>
 */
public class NestAction<K,T> extends FormAction {
	ViewTransitionFactory<K, T> provider;
	K key;
	T target;
	public NestAction(ViewTransitionFactory<K, T> provider, K key, T target) {
		this.provider=provider;
		this.key=key;
		this.target=target;
	}

	@Override
	public ChainedTransitionResult action(Form f) throws ActionException {

		return new ChainedTransitionResult<T, K>(provider, target, key);
	}

	@Override
	public String getHelp() {
		return provider.getHelp(key);
	}

}