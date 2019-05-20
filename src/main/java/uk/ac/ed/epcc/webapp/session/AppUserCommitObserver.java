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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** An interface for {@link Composite}s that contribute to commits.
 * @author spb
 * @param <AU> 
 *
 */

public interface AppUserCommitObserver<AU extends AppUser> {
	/**  AppUser has been edited and is about to be comitted
	 * 
	 * @param person {@link AppUser} being changed
	 * @param dirty  boolean record is known to contain changes
	 * @throws DataFault
	 */
  public void pre_commit(AU person,boolean dirty) throws DataFault;
  /** {@link AppUser} has been edited
   * 
   * @param person {@link AppUser} that was edited
   * @param changed boolean contents have changed
   * @throws DataFault
   */
  public void post_commit(AU person, boolean changed)throws DataFault;
}