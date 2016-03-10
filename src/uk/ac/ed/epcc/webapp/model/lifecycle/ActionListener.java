//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.lifecycle;

import uk.ac.ed.epcc.webapp.Targetted;

/** An object that needs to perform some cascaded operations after
 * a trigger event takes place on a target object.
 * 
 * The general case of this (where the event requires external actions and so may
 * be aborted) is the {@link LifeCycleListener}.
 * 
 * @see LifeCycleListener
 * @author spb
 *
 * @param <R> type of target.
 */

public interface ActionListener<R> extends Targetted<R> {

	/** Does the {@link ActionListener} want to allow the event to take place. This is to give the 
	 * listener a chance to veto the event when it knows in advance its not capable of supporting the operation in its current state. 
	 * For example veto project setup because there are no machines configured.
	 * 
	 * @param target  object.
	 * @param throw_reason set to true to throw exception instead of returning boolean.
	 * @return boolean true if operation allowed
	 * @throws LifeCycleException
	 */
	public abstract boolean allow(R target, boolean throw_reason)
			throws LifeCycleException;

	/** Called after the event has taken place (and any external actions have completed) to allow the {@link LifeCycleListener} to 
	 * make/finalise corresponding changes to its own objects.
	 * 
	 * @param target
	 */
	public abstract void action(R target);

}