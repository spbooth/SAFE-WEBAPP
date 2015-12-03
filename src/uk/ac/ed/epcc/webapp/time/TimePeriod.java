//| Copyright - The University of Edinburgh 2011                            |
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