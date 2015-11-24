// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.time;
/** A TimePeriod made up from a series of sub-periods
 * 
 * @author spb
 *
 */
public interface SplitTimePeriod extends TimePeriod {
	public TimePeriod[] getSubPeriods();
	public int getNsplit();
}