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
package uk.ac.ed.epcc.webapp.forms.exceptions;

import uk.ac.ed.epcc.webapp.forms.action.FormAction;



/** A {@link TransitionException} that refers to invalid form parameters
 * detected in the {@link FormAction} rather than the form validation.
 * In this case the error message should be presented as a form error if possible
 * allowing the user to modify and re-submit the form. This extends {@link TransitionException}
 * as a generic error message is an acceptable default behaviour.
 * 
 * @author spb
 *
 */

public class TransitionValidationException extends TransitionException {

	private String field=null;
	/** create the exception
	 * @param message
	 */
	public TransitionValidationException(String message) {
		super(message);
	}
	/** create the exception for a specific field.
	 * @param message
	 */
	public TransitionValidationException(String field,String message) {
		super(message);
		this.field=field;
	}
	public String getField() {
		return field;
	}
}