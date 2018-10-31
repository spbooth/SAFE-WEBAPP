//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;




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

		return new ChainedTransitionResult<>(provider, target, key);
	}

	@Override
	public String getHelp() {
		return provider.getHelp(key);
	}

}