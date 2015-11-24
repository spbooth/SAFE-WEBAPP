// Copyright - The University of Edinburgh 2011
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