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
package uk.ac.ed.epcc.webapp.exec;

/** A timeout thread that will interrupt another after a specified length of time.
 * 
 *This thread will just exit if interrupted itself.
 * @author spb
 *
 */

public class TimeoutThread extends Thread {
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			sleep(timeout);
			parent.interrupt();
		} catch (InterruptedException e) {
			
		}
		
	}
	/**
	 * @param timeout
	 * @param parent
	 */
	public TimeoutThread(long timeout, Thread parent) {
		super();
		this.timeout = timeout;
		this.parent = parent;
	}
	private final long timeout;
	private final Thread parent;

}