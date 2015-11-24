// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Id: TransitionValidationException.java,v 1.3 2015/02/11 10:57:37 spb Exp $")
public class TransitionValidationException extends TransitionException {

	/** create the exception
	 * @param message
	 */
	public TransitionValidationException(String message) {
		super(message);
	}

}
