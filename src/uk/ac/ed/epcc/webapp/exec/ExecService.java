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

import java.io.IOException;

import uk.ac.ed.epcc.webapp.AppContextService;

/** A service to run external commands.
 * 
 * This is encapsulated as a service to allow substitution in tests and to re-use common code.
 * @author spb
 *
 */

public interface ExecService extends AppContextService<ExecService> {

	/** Run a external command (waiting for it to complete) 
	 * 
	 * @param timeout_milliseconds
	 * @param command
	 * @return {@link ProcessProxy}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ProcessProxy exec(long timeout_milliseconds,String command) throws Exception;
	

	/** Run a external command with supplied input (waiting for it to complete) 
	 * 
	 * @param input
	 * @param timeout_milliseconds
	 * @param command
	 * @return {@link ProcessProxy}
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws Exception 
	 */
	public ProcessProxy exec(String input, long timeout_milliseconds,String command) throws Exception;
	
	
	/** Make a {@link DeferredProcessProxy} to be run later.
	 * 
	 * @param input
	 * @param command
	 * @return
	 */
	public DeferredProcessProxy exec_deferred(byte input[], String command) throws Exception;
}