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
package uk.ac.ed.epcc.webapp.forms.exceptions;

import uk.ac.ed.epcc.webapp.forms.action.FormAction;

/** A wrapped exception from a {@link FormAction}
 * 
 * Normally only logged with a generic error message unless it is a {@link TransitionException}
 * when the message is presented to the user.
 * 
 * @see TransitionException
 * @see TransitionValidationException
 */

public class ActionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ActionException(String message) {
		super(message);
	}

	public ActionException(String message, Throwable t) {
		super(message, t);
	}
}