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
package uk.ac.ed.epcc.webapp.session.twofactor;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserComposite;

/** Interface for {@link AppUserComposite}s that
 * implement 2-factor work-flow
 * @author Stephen Booth
 *
 */
public interface TwoFactorComposite<A extends AppUser> {

	/** Check if two factor authentication is required.
	 * If this method returns null, two factor authentication is
	 * not enabled for the current user or the user has recently validated 
	 * this authentication within this session. Otherwise a {@link FormResult} is returned
	 * to validate the two-factor credential.
	 * <p> 
	 * If authorisation in required this might send out a token via an independent communication channel
	 * so use {@link #needAuth(AppUser)} unless you intent to proceed.
	 * 
	 * @param user {@link AppUser} to check.
	 * @return {@link FormResult} or null
	 */
	public FormResult requireAuth(A user);
	
	/** would a call to {@link #requireAuth(AppUser)} return a {@link FormResult}
	 * 
	 * @param user
	 * @return
	 */
	public boolean needAuth(A user);
}
