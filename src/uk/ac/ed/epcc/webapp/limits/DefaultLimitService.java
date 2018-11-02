//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.limits;

import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * @author Stephen Booth
 *
 */
@PreRequisiteService({CurrentTimeService.class,ConfigService.class})
public class DefaultLimitService extends LimitService {

	private final Runtime rt;
	private final long inital_available_memory;
	private final long max_memory;
	private final CurrentTimeService current_time;
	private final Date start;
	private final Date max_time;
	private final long max_millis;
	private final double max_mem_fraction;
	private final Logger log;
	/**
	 * @param conn
	 */
	public DefaultLimitService(AppContext conn) {
		super(conn);
		rt=Runtime.getRuntime();
		inital_available_memory=rt.maxMemory()-rt.totalMemory()+rt.freeMemory();
		max_memory=rt.maxMemory();
		current_time=conn.getService(CurrentTimeService.class);
		start=current_time.getCurrentTime();
		max_millis=conn.getLongParameter("resource_limit.max_millis", 120000L); // 2 min
		max_time= new Date(start.getTime()+max_millis);
		max_mem_fraction=conn.getDoubleParam("resource_limit.max_memory_fraction", 0.9);
		LoggerService ls = conn.getService(LoggerService.class);
		if( ls != null) {
			log=ls.getLogger(getClass());
		}else {
			log=null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.limits.LimitService#checkLimit()
	 */
	@Override
	public void checkLimit() throws LimitException {
		Date now = current_time.getCurrentTime();
		if( now.after(max_time)) {
			if( log != null) {
				log.warn("Resource check fail: start="+start+" now="+now);
			}
			throw new LimitException("The request is taking too long");
		}
		long free_mem = rt.maxMemory()-rt.totalMemory()+rt.freeMemory();
		double fraction_used = ((double)(inital_available_memory-free_mem))/(double)inital_available_memory;
		
		if( fraction_used >max_mem_fraction) {
			if( log != null) {
				log.warn("Resource check fail: start="+start+" now="+now+" memory "+inital_available_memory+"->"+free_mem+" "+fraction_used);
			}
			throw new LimitException("Memory use is too high");
		}
		if( log != null ) {
			log.debug("Resource check: start="+start+" now="+now+" memory "+inital_available_memory+"->"+free_mem+" "+fraction_used);
		}
	}

}
