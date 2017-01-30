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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.data.Composite;

/** An Interface for an {@link AppUserFactory} or its {@link Composite}s that can permit access to a role.
 * 
 * This is intended for roles that rely on the object state.
 * 
 * Implementing classes should return false for any role they don't recognise
 * @author spb
 *
 */
public interface StateRoleProvider<A extends AppUser> {

	public boolean testRole(A user, String role);
}
