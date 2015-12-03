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