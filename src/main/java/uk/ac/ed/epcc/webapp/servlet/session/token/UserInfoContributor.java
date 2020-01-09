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

import java.util.Map;

import uk.ac.ed.epcc.webapp.model.DataContributor;
import uk.ac.ed.epcc.webapp.session.AppUser;

/** A {@link DataContributor} that adds data to the Oauth user info endpoint
 * 
 * 
 * Add a {@link Scopes} annotation to set global access control. Fine grained control can be added by overriding 
 * {@link #addMetaData(ScopeQuery, Map, AppUser)}
 * @author Stephen Booth
 *
 */
public interface UserInfoContributor<T extends AppUser> extends DataContributor<T> {

	/** add metadata with fine grained access control
	 * 
	 * @param scopes   {@link ScopeQuery} representing the access permissions of the request.
	 * @param attributes
	 * @param target
	 */
	public default void addMetaData(ScopeQuery scopes, Map<String, Object> attributes, T target) {
		// Default behaviour is to only do global access control
		addMetaData(attributes, target);
	}
	
}
