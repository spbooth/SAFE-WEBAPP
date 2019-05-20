//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.timer;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;

/** A no-operation {@link TimerService}.
 * 
 * USed in tests
 * @author Stephen Booth
 *
 */
public class NullTimerService extends AbstractContexed implements TimerService {

	/**
	 * @param conn
	 */
	public NullTimerService(AppContext conn) {
		super(conn);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super TimerService> getType() {
		return TimerService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
		

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.timer.TimerService#timerStats()
	 */
	@Override
	public void timerStats() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.timer.TimerService#timerStats(java.lang.Class)
	 */
	@Override
	public void timerStats(Class clazz) {
		

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.timer.TimerService#timerStats(java.lang.StringBuilder)
	 */
	@Override
	public void timerStats(StringBuilder sb) {
		

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.timer.TimerService#startTimer(java.lang.String)
	 */
	@Override
	public void startTimer(String name) {
	

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.timer.TimerService#stopTimer(java.lang.String)
	 */
	@Override
	public void stopTimer(String name) {
		

	}

}
