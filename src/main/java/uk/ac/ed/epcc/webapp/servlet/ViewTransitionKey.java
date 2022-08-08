//| Copyright - The University of Edinburgh 2018                            |
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

/** Interface for Transition keys that can
 * implement bookmarkable non-modifying direct transitions that are
 * safe to access via direct GET URLs.
 * If these reference direct transitions they will be presented as bookmarkable URLs via
 * an additional redirect
 * 
 * @see TransitionServlet#MODIFY_ON_POST_ONLY
 * 
 * @author spb
 * @param <T> type of target
 *
 */
public interface ViewTransitionKey<T> {
  /** Does this transition result in any side effects that change model state.
   * 
   * Updates to session state etc are ok.
   * 
   * @param target
   * @return
   */
  public default boolean isNonModifying(T target) {
	  return true;
  }
}
