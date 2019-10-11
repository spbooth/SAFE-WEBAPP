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
package uk.ac.ed.epcc.webapp.model.period;

import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** A {@link SplitManager} where the {@link TimePeriod}s are arranged in
 * ordered sequences. By default these are non-overlapping sequences in time order.
 * Optionally overlapps can be enabled on a sequence by sequence bases in which case the sequence order
 * is roughly time ordered.
 * 
 * @author spb
 *
 * @param <T> type of TimePeriod
 */
public interface SequenceManager<T extends TimePeriod> extends SplitManager<T>{

	/** Does the record come from a sequence that disallows overlapping records
	 * 
	 * @param target
	 * @return
	 */
	public default boolean noOverlapps(T target) {
		return true;
	}
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
	/** Get the next entry in the sequence.
	 * 
	 * @param current starting period
	 * @param move_up direction of move
	 * @return period or null
	 */
	public T getNextInSequence(T current, boolean move_up);
	
	
	
	/** Get the element from the sequence most likely to be merged with the
	 * current record
	 * 
	 * @param current
	 * @param move_up
	 * @return Merge candidate
	 */
	default public T getMergeCandidate(T current, boolean move_up) {
		// This is correct (but not necessarily most efficient) for non-overlapping sequences 
		return getNextInSequence(current, move_up);
	}
	/** Additional validation checks
	 * 
	 * @param current
	 * @param d
	 * @throws ValidateException
	 */
	default public void canChangeStart(T current, Date d)throws ValidateException{
		
	}
	/** Additional validation checks
	 * 
	 * @param current
	 * @param d
	 * @throws ValidateException
	 */
	default public void canChangeEnd(T current, Date d)throws ValidateException{
		
	}
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