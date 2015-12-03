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
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Key for transitions on {@link Part}s.
 * @author spb
 *
 */

public abstract class PartTransitionKey<T extends PartManager.Part> extends TransitionKey<T> {

	/**
	 * @param name
	 * @param help
	 */
	public PartTransitionKey( String name, String help) {
		super(PartManager.Part.class, name, help);
	
	}

	/**
	 * @param name
	 */
	public PartTransitionKey(String name) {
		super(PartManager.Part.class, name);
	}

	/** IS the operation allowed on the target;
	 * 
	 * @param target
	 * @param sess
	 * @return
	 */
	public abstract boolean allow(T target, SessionService<?> sess);
}