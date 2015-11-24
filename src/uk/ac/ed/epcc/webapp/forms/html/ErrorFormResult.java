// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Collection;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;

/** A {@link FormResult} that indicates a form validation error.
 * @author spb
 * @param <T> 
 * @param <K> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ErrorFormResult.java,v 1.3 2014/09/15 14:30:18 spb Exp $")
public class ErrorFormResult<T,K> implements FormResult {
	/**
	 * @param provider
	 * @param target
	 * @param key
	 * @param errors
	 * @param missing
	 */
	public ErrorFormResult(TransitionFactory<K, T> provider, T target, K key,
			Map<String, String> errors, Collection<String> missing) {
		super();
		this.provider = provider;
		this.target = target;
		this.key = key;
		this.errors = errors;
		this.missing = missing;
	}

	private final TransitionFactory<K, T> provider;
	private final T target;

	private final K key;
	
	private final Map<String,String> errors;
	private final Collection<String> missing;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResult#accept(uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor)
	 */
	public void accept(FormResultVisitor vis) throws Exception {
		if( vis instanceof WebFormResultVisitor){
			((WebFormResultVisitor)vis).visitErrorFormResult(this);
			return;
		}
		throw new UnsupportedResultException();
	}

	public TransitionFactory<K, T> getProvider() {
		return provider;
	}

	public T getTarget() {
		return target;
	}

	public K getKey() {
		return key;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public Collection<String> getMissing() {
		return missing;
	}

}
