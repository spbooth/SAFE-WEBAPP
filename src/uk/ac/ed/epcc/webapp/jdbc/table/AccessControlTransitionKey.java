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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 * @param <T> target type of transition
 *
 */
public abstract class AccessControlTransitionKey<T> extends TransitionKey<T> {

	/**
	 * @param t
	 * @param name
	 * @param help
	 */
	public AccessControlTransitionKey(Class<? super T> t, String name, String help) {
		super(t, name, help);
		
	}

	/** Access control to the transition.
	 * This is used to widen the default permissions
	 * 
	 * @param serv
	 * @return true if operation allowed
	 */
	public abstract boolean allow(SessionService<?> serv);
}
