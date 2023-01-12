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

/** A {@link LifeCycleListener} is a class with an interest in a particular 
 * life-cycle event such as object retirement. Usually this is used to handle cascaded changes required
 * by other classes that reference the target.
 * 
 * Though the target object will need explicit references to {@link LifeCycleListener}s for each other class that
 * has an interest in the event it does not need to implement the logic directly instead this can be moved closer to the dependent class.
 * 
 * Each {@link LifeCycleListener} only concerns itself with a single type of event and target type. 
 * If a factory class is concerned with multiple event types then multiple {@link LifeCycleListener} 
 * inner classes may be required. 
 * 
 * 
 * @author spb
 * @param <R> type of object being listened to.
 *
 */

public interface LifeCycleListener<R> extends ActionListener<R> {
	/** The target has started the event but external operations have not yet been invoked.
	 * 
	 * This allows the {@link LifeCycleListener} to perform pre-requisite setup for the operation.
	 * This could include issuing pre-requisite external operations that need to be issued first.
	 * 
	 * These changes should ideally be capable of reverse/cancel as the operation may still be aborted by calling {@link #abort(Object)} rather than
	 * completed by calling {@link #action(Object)}.
	 * <p>
	 * Normally this is called after the target has been changed to an intermediate state
	 * but before the external operation (such as a ticket) has been requested. 
	 * 
	 * This allows code called from the
	 * method to know the event has been requested but still issue operations first.
	 *In fact these actions are normally associated with the change in state.
	 * Setting an intermediate state on the target allows it to detect and avoid a circular 
	 * cascade where actions in the listener re-triggers the original event. However it is best to avoid
	 * such circular dependencies as complex cases can still result in operations being issued out of order.
	 * Best practice is to have a unique object state while the prepare methods are run to
	 * allow possible circular calls to be detected.
	 * <p>
	 * 
	 *  Despite this the operations should be idempotent so it is ok to re-run the {@link #prepare(Object)} method if the ticket is re-issued.
	 *  
	  * <p>
	 * If there are no external actions required this method may not be called and {@link #action(Object)}
	 * will be called directly.
	 * <p>
	 * If this method throws an {@link Exception} the target object has the option of rolling back its own changes 
	 * and any made by other {@link LifeCycleListener}s. It is therefore legal for the last {@link LifeCycleListener} in the 
	 * chain to trigger the external action from its prepare method.
	 * 
	 * 
	 * @param target
	 * @throws Exception 
	 */
	public default void prepare(R target) throws Exception{};
	
	/** Similar to {@link #prepare(Object)} except that
	 *  occurs after the triggering operation has been fully requested.
	 *  
	 *  
	 * This allows the {@link LifeCycleListener} to make some immediate changes without waiting for
	 * the external action to complete. These are logically after the action but with weaker guarantees
	 * (relying on action ordering rather then waiting for completion like {@link #action(Object)} does
	 *  
	 *  This is slightly safer for notification etc.
	 *  It can also be used to trigger follow on operations but these can be difficult
	 *  to abort if the primary operation is aborted so it might be safer to send
	 *  follow-ons from the action method.
	 * 
	 * 
	 * 
	 * @param target
	 * @throws Exception
	 */
	public default void issued(R target) throws Exception{};
	
	/**  A prepared event has been aborted. This allows the {@link LifeCycleListener} try to clean-up any changes it made in
	 * the {@link #prepare(Object)} call. For example by returning to the original state or cancelling the
	 * change.
	 * 
	 * @param target
	 */
	public default void abort(R target) {};
}