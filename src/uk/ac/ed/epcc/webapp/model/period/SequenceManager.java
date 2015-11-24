package uk.ac.ed.epcc.webapp.model.period;

import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** A {@link SplitManager} where the {@link TimePeriod}s are arranged in
 * non-overlapping sequences.
 * 
 * @author spb
 *
 * @param <T> type of TimePeriod
 */
public interface SequenceManager<T extends TimePeriod> extends SplitManager<T>{

	/** Test to see if two periods can be merged.
	 * Usually this means test if they are neighbouring periods from the same sequence.
	 * @param first
	 * @param second
	 * @return boolean
	 */
	public abstract boolean canMerge(T first, T second);

	/** Merge two periods 
	 * 
	 * @param first
	 * @param second
	 * @return the merged Period
	 * @throws Exception
	 */
	public abstract T merge(T first, T second) throws Exception;
	/** Get the next entry in the sequence
	 * 
	 * @param current starting period
	 * @param move_up direction of move
	 * @return period or null
	 */
	public T getNextInSequence(T current, boolean move_up);
	/** Additional validation checks
	 * 
	 * @param current
	 * @param d
	 * @throws ValidateException
	 */
	public void canChangeStart(T current, Date d)throws ValidateException;
	/** Additional validation checks
	 * 
	 * @param current
	 * @param d
	 * @throws ValidateException
	 */
	public void canChangeEnd(T current, Date d)throws ValidateException;
	/** Change the start date on the period
	 * 
	 * @param period
	 * @param d
	 * @throws Exception
	 */
	public void setStart(T period,Date d)throws Exception;
	/** Change the end date on the period
	 * 
	 * @param period
	 * @param d
	 * @throws Exception
	 */
	public void setEnd(T period,Date d)throws Exception;
}