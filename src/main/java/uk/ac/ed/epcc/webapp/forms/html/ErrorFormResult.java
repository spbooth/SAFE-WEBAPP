//| Copyright - The University of Edinburgh 2014                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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