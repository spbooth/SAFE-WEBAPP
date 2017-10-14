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
package uk.ac.ed.epcc.webapp.exec;

/** A {@link ProcessProxy} with deferred execution.
 * 
 * The process is not actually started till the {@link #execute(long)} method is called.
 * @author spb
 *
 */
public interface DeferredProcessProxy extends ProcessProxy {
	/** Blocking call to execute the deferred process. The process will be destoyed if the timeout is exceeded.
	 * 
	 * This sh
	 * 
	 * @param timeout_millis 
	 * @return exit code
	 * @throws Exception 
	 * 
	 */
	public Integer execute(long timeout_millis) throws Exception;
	
	/** Start the deferred process
	 * 
	 */
	public void start();
	
	/** wait for the process to complete. If the timeout is exceeded 
	 * the process is forcebly destroyed.
	 * 
	 * @param timeout_millis
	 * @throws InterruptedException
	 */
	public void complete(long timeout_millis) throws InterruptedException;


	/** Set an automatic timeout for the {@link DeferredProcessProxy} implemented by a thread.
	 * This can be used if the spawning thread is not going to wait for completion.
	 * 
	 * @param timeout
	 */
	public void setTimeout(long timeout);
}
