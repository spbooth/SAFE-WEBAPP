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
package uk.ac.ed.epcc.webapp;
/** Object with a target type that can be queried at run-time
 * 
 * @author spb
 *
 * @param <T>
 */
public interface Targetted<T> {
	 /** Get the type of the returned object as far as it is known.
	   * This method is used for run-time type checking
	   * The result objects will always be assignable to the type returned by
	   * this method.
	   * 
	   * @return Class object for return type
	   */
	  public Class<? super T> getTarget();
}