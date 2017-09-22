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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.timer;

import uk.ac.ed.epcc.webapp.AppContextService;
/** A {@link AppContextService} for implementing Timers
 * 
 * @author spb
 *
 */
public interface TimerService extends AppContextService<TimerService>{

	/** Generate timer statistics to the log of the timer service.
	 * 
	 */
	public abstract void timerStats();

	/** Generate timer statistics to the log of the specified class.
	 * @param clazz Class for logging
	 */
	public abstract void timerStats(Class clazz);

	/** write stats to a {@link StringBuilder}
	 * 
	 * @param sb
	 */
	public void timerStats(StringBuilder sb);
	/** Start a named timer
	 * 
	 * @param name
	 */
	public abstract void startTimer(String name);

	/** Stop a named timer
	 * 
	 * @param name
	 */
	public abstract void stopTimer(String name);

}