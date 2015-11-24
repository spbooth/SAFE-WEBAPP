// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.lifecycle;

/** An {@link Exception} thrown when a {@link LifeCycleListener} wished to veto a
 * user operation. The message text should be a message presented to the operator.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: LifeCycleException.java,v 1.2 2014/09/15 14:30:33 spb Exp $")
public class LifeCycleException extends Exception {


	/** Constructor
	 * @param message  message for operator.
	 */
	public LifeCycleException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
