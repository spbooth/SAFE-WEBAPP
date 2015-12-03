//| Copyright - The University of Edinburgh 2013                            |
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

import uk.ac.ed.epcc.webapp.content.ContentBuilder;

/** Interface for a {@link TransitionFactory} that 
 * provides additional top/botton content for navication
 * @author spb
 *
 */

public interface NavigationProvider<K,T> extends TransitionFactory<K, T> {

	public <X extends ContentBuilder> X getTopNavigation(X cb, T target);

	
	public <X extends ContentBuilder> X getBottomNavigation(X cb, T target);



}