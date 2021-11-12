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
import uk.ac.ed.epcc.webapp.forms.inputs.BoundedDateInput;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.time.TimePeriod;
/** Interface for class that can split {@link TimePeriod}s
 * Normally this is implemented by the appropriate factory class
 * @author spb
 *
 * @param <T>
 */
public interface SplitManager<T extends TimePeriod> {

	/** get the appropriate date input to use for this class
	 * 
	 * @return
	 */
	public abstract BoundedDateInput getDateInput();

	/** Validate a proposed split. The location of the split within the period is validated 
	 * explicitly. This is for additional constraints specific to the class logic.
	 * 
	 * @param orig
	 * @param d
	 * @throws ValidateException
	 */
	public default void canSplit(T orig, Date d) throws ValidateException{
		
	}
	/** Split a record at a specified time returning new record starting
	 * at the split.
	 * 
	 * @param orig
	 * @param d
	 * @return new record or null if split outside record time range.
	 * @throws Exception
	 */
	public abstract T split(T orig, Date d) throws Exception;
	/** Get a date before which edits should be confirmed.
	 * 
	 * @return Date or null
	 */
	 public default Date getEditMarker() {
		 return null;
	 }
	 
	 
	 /** Get a date before which edits are forbidden for the current user
	  * If no limit return null.
	  * 
	  * 
	  * @return Date or null
	  */
	 default public Date getEditLimit(SessionService<?> sess) {
		 return null;
	 }
}