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
@uk.ac.ed.epcc.webapp.Version("$Id: ActionException.java,v 1.4 2015/02/11 10:57:37 spb Exp $")
public class ActionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ActionException(String message) {
		super(message);
	}

	public ActionException(String message, Throwable t) {
		super(message, t);
	}
}