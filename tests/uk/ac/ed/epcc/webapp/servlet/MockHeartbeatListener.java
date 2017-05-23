//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import java.util.Date;

import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;

/**
 * @author spb
 *
 */
public class MockHeartbeatListener implements HeartbeatListener {

	public static boolean has_run=false;
	public static Date date=null;
	/**
	 * 
	 */
	public MockHeartbeatListener() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener#run()
	 */
	@Override
	public Date run() {
		has_run=true;
		return date;
	}

}
