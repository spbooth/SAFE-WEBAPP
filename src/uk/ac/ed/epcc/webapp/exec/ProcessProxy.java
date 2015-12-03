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

/** Interface for the result of an external process.
 * @author spb
 *
 */

public interface ProcessProxy {

	/** get the output of the process
	 * 
	 * @return String
	 */
	public abstract String getOutput();

	/** get the erro output of the process
	 * 
	 * @return
	 */
	public abstract String getErr();

	/** get the return code
	 * 
	 * @return
	 */
	public abstract Integer getExit();
	/** did the process time-out or killed via interrupt
	 * 
	 * @return
	 */
	public abstract boolean wasTerminated();
}