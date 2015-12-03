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


/** TransitionProvider that can generate a default transition for a target
 * if no other transition is specified.
 * @author spb
 *
 * @param <K>
 * @param <T>
 */
public interface DefaultingTransitionFactory<K, T> extends
		TransitionFactory<K, T> {

	/** Get the key for the default transition.
	 * 
	 * This method can return null if a sub-class wants to supress the default transition or if
	 * there is no valid default for the target.
	 * @param target 
	 * @return transition key
	 */
	public K getDefaultTransition(T target);
}