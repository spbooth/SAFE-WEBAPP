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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** Default implementation of the {@link TimerService}
 * 
 * @author spb
 *
 */
@PreRequisiteService({LoggerService.class})

public class DefaultTimerService implements Contexed,TimerService{
	private static final String TIMER_TOTAL = "Total";
	 private Map<String,Timer> timers=null;
	 private AppContext conn;
	 private String prefix="";
	 public DefaultTimerService(AppContext conn){
		 this.conn=conn;
		 timers = new HashMap<String,Timer>();
 		startTimer(TIMER_TOTAL);
	 }
	    
	    private int n_timers=0;
	    private static final int MAX_TIMERS=10000;
		public void cleanup() {
			//TODO this will not work if LoggerService cleans up before TimerService
			if( timers != null ){
				stopTimer(TIMER_TOTAL);
			    timerStats();
			    timers=null;
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.TimerService#timerStats()
		 */
		public void timerStats(Class clazz) {
			if( timers != null ){
				
				long target = (long) (0.01 * timers.get(TIMER_TOTAL).getTime());
				LoggerService service = conn.getService(LoggerService.class);
				if( service != null){
					Logger log=service.getLogger(clazz);
					if(log != null){
					Set<Timer> stats = new TreeSet<Timer>(timers.values());
					for(Timer t: stats){
						if( t.getTime() > target){
							log.info(prefix+t.getStats());
						}
					}
					}
				}
				
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.TimerService#startTimer(java.lang.String)
		 */
		public final void startTimer(String name){
			if( timers == null ){
				return;
			}
			Timer t = timers.get(name);
			if( t == null ){
				n_timers++;
				if( n_timers > MAX_TIMERS ){
					return;
				}
				//Logger log = conn.getService(LoggerService.class).getLogger(getClass());
				//log.debug("making timer "+name);
				t = new Timer(name);
				timers.put(name, t);
			}
			t.start();
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.TimerService#stopTimer(java.lang.String)
		 */
		public final void stopTimer(String name){
			if( timers == null ){
				return;
			}
			Timer t = timers.get(name);
			if( t == null ){
				if( n_timers > MAX_TIMERS){
					return;
				}
				throw new ConsistencyError("Non existant timer stopped "+name);
			}
			t.stop();
		}
		public AppContext getContext() {
			return conn;
		}
		public Class<TimerService> getType() {
			return TimerService.class;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.timer.TimerService#timerStats()
		 */
		@Override
		public void timerStats() {
			timerStats(getClass());
		}
		/**
		 * @return the prefix
		 */
		public String getPrefix() {
			return prefix;
		}
		/**
		 * @param prefix the prefix to set
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		
}