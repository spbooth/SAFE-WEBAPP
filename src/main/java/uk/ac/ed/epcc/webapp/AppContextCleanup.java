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
package uk.ac.ed.epcc.webapp;

/** Objects stored in an {@link AppContext} that call a cleanup
 * as part of {@link AppContext#close()}
 * These can be attributes or {@link AppContextService}
 * @author spb
 *
 * 
 */
public interface AppContextCleanup {

	/** {@link AppContext} is being closed.
	 * Only use this for cleanup that can't be handled by
	 * normal garbage collection or for state which is never returned by reference.
	 * 
	 */
	void cleanup();

}