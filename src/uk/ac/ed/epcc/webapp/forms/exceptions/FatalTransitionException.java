// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.forms.exceptions;

/** This is a {@link TransitionException} indicating an unexpected internal error.
 * The framework will attempt to roll-back any database changes.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class FatalTransitionException extends TransitionException {

	/**
	 * @param message
	 */
	public FatalTransitionException(String message) {
		super(message);
	}

}
