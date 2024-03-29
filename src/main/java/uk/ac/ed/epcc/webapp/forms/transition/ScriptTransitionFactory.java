//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.forms.transition;

import java.util.Set;

import uk.ac.ed.epcc.webapp.tags.WebappHeadTag;

/** Interface for {@link TransitionFactory}s
 * that need to add additional CSS and ECMAScript to the html of a transition.
 * Only has any effect in the html context
 * form.
 * @see WebappHeadTag
 * @author spb
 * @param <K> type of transition key
 *
 */
public interface ScriptTransitionFactory<K> {
	public Set<String> getAdditionalCSS(K key);
	public Set<String> getAdditionalScript(K key);
}
