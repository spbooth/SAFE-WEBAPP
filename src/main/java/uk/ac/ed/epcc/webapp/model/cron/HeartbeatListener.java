//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.cron;

import java.util.Date;

/** Interface for classes that need to listen for heart-beat events.
 * 
 * 
 * 
 * @author spb
 *
 */

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