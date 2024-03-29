//| Copyright - The University of Edinburgh 2015                            |
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

import uk.ac.ed.epcc.webapp.servlet.ViewTransitionKey;

/** TransitionProvider that can generate a default transition
 * i.e. a {@link TargetLessTransition} for selecting a target or a navigation transition
 * to an index page.
 * It can also be used to supply a default transition for a target.
 * 
 * An Index transition should usually not modify state. Consider having the key
 * implement {@link ViewTransitionKey}
 * 
 * @author spb
 * @see DefaultingTransitionFactory
 * @param <K> type of transition key
 * @param <T> type of transition target
 */
public interface IndexTransitionFactory<K, T> extends
		TransitionFactory<K, T> {

	/** Get the key for the default index transition.
	 * 
	 * This method can return null if a sub-class wants to supress the default transition.
	 * 
	 * @return transition key
	 */
	public K getIndexTransition();
}