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
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link TransitionKey} used by {@link DynamicFormTransitionProvider}
 * @author spb
 *
 */

public abstract class DynamicFormTransitionKey<T extends DynamicForm> extends TransitionKey<T> {

	/**
	 * @param name
	 * @param help
	 */
	public DynamicFormTransitionKey( String name, String help) {
		super(DynamicForm.class, name, help);
	
	}

	/**
	 * @param name
	 */
	public DynamicFormTransitionKey(String name) {
		super(DynamicForm.class, name);
	}

	/** IS the operation allowed on the target;
	 * 
	 * @param target
	 * @param sess
	 * @return
	 */
	public abstract boolean allow(DynamicForm target, SessionService<?> sess);
}