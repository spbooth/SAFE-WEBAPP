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

/** TransitionProvider that can generate a default transition for a target
 * if no other transition is specified.
 * 
  * Like {@link ViewTransitionFactory} this defines the operation if no transition is specified for a target. If both interfaces are implemented the
 * default transition is taken if one is returned but a view transition is considered if {@link #getDefaultTransition(Object)} returns null.
 * 
 * <p> 
 * An default transition should usually not modify state. Consider having the key
 * implement {@link ViewTransitionKey} if this is not the case.
 * @author spb
 * @see IndexTransitionFactory
 *
 * @param <K> type of transition key
 * @param <T> type of transition target
 */
public interface DefaultingTransitionFactory<K, T> extends
		TransitionFactory<K, T> {

	/** Get the key for the default transition.
	 * 
	 * This method can return null if a sub-class wants to suppress the default transition, if
	 * there is no valid default for the target or if a view transition is to be used.
	 * 
	 * Note that the target must be non-null. To implement a default transition with no target use
	 * {@link IndexTransitionFactory}
	 * @param target 
	 * @return transition key
	 */
	public K getDefaultTransition(T target);
}