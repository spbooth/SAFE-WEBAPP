//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.servlet.session.token;

/** Interface for objects that can be queried for scope values
 * @author Stephen Booth
 *
 */
public interface ScopeQuery {

	/** Does the authenticated token have the specified scope.
	 * 
	 * This is for fine tuing access control where the url permits multiple scopes.
	 * 
	 * @param scope
	 * @return
	 */
	boolean hasScope(String scope);

	/** Does the token have one of the scope from a list
	 * 
	 * @param scopes
	 * @return
	 */
	public default boolean hasScope(String scopes[]) {
		for(String s : scopes) {
			if(hasScope(s)) {
				return true;
			}
		}
		return false;
	}

}