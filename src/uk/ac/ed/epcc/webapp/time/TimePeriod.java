// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.time;

import java.util.Date;
/** A TimePeriod defines a period of time between two dates
 * Strictly speaking the Period only includes the end date so if periods form a sequence then 
 * the start of the following date will match the end of the previous date.
 * This convention is chosen to because in accounting systems the end-date is often more canonical than
 * the start date.
 * @author spb
 *
 */
public interface TimePeriod {
	public Date getStart();
	public Date getEnd();
}