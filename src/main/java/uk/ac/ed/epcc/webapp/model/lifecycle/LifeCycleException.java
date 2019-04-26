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

/** An {@link Exception} thrown when a {@link LifeCycleListener} wished to veto a
 * user operation. The message text should be a message presented to the operator.
 * @author spb
 *
 */

public class LifeCycleException extends Exception {


	/** Constructor
	 * @param message  message for operator.
	 */
	public LifeCycleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}