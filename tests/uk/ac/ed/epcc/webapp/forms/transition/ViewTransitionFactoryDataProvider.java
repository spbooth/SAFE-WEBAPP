//| Copyright - The University of Edinburgh 2012                            |
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

import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public interface ViewTransitionFactoryDataProvider<K,T> extends
		TransitionFactoryDataProvider<K, T> {
	public ViewTransitionFactory<K, T> getTransitionFactory();
	
	/** user allowed access to target
	 * 
	 * @param target
	 * @return SessionService
	 * @throws Exception 
	 */
	public SessionService<?> getAllowedUser(T target) throws Exception;
	
	/** User not allowed access to target.
	 * 
	 * @param target
	 * @return SessionService or null to skip test
	 * @throws Exception 
	 */
	public SessionService<?> getForbiddenUser(T target) throws Exception;
}