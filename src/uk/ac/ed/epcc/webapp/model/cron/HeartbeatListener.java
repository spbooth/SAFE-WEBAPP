// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.cron;

import java.util.Date;

/** Interface for classes that need to listen for heart-beat events.
 * 
 * 
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface HeartbeatListener {

	/** Find and run all Events that are Queued and past their schedule time.
	 * It is always legal for this method to return null but if a Date is returned it
	 * is an indication of the next queued event handled by the class and may be used to optimise the
	 * heart-beat frequency.
	 * @return Date of next event or null
	 * 
	 */
	public abstract Date run();

}