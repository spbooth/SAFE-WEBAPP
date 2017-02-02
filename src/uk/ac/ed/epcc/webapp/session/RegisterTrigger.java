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

/** An interface for objects (Usually the {@link AppUserFactory} or its
 * {@link Composite}s that can trigger an existing user to visit the
 * registration page.
 * 
 * This is intended for auto-created accounts that may be populated before the
 * actual user visits the application for the first time. It only makes sense
 * when used with mandatory external authentication where the registration form
 * can locate the existing record via the externally provided identity.
 * @author spb
 *
 */
public interface RegisterTrigger<AU extends AppUser> {
	
	/** Should this person register
	 * 
	 * @param user
	 * @return
	 */
	public boolean mustRegister(AU user);
	
	/** user has registered.
	 * This should record a state change so the {@link #mustRegister(AppUser)}
	 * method returns false in the future.
	 * 
	 * @param user
	 */
	public void postRegister(AU user);

}
