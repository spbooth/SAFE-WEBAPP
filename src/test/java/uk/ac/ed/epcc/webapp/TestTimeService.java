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
package uk.ac.ed.epcc.webapp;

import java.util.Date;

/** A {@link CurrentTimeService}
 * that allows the time to be set for tests.
 * @author spb
 *
 */

public class TestTimeService extends CurrentTimeService {
  /* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.CurrentTimeService#getCurrentTime()
	 */
	@Override
	public Date getCurrentTime() {
		if( result != null ){
			return result;
		}
		return super.getCurrentTime();
	}

private Date result;

/**
 * @return the result
 */
public Date getResult() {
	return result;
}

/**
 * @param result the result to set
 */
public void setResult(Date result) {
	this.result = result;
}
  
  
}