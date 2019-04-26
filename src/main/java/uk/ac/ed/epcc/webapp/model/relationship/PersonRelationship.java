//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A relationship between different users of the system
 * @author spb
 *
 */

public class PersonRelationship<A extends AppUser> extends Relationship<A, A>{

	/** role to allow sudo access
	 * 
	 */
	public static final String SUDO_ROLE = "Sudo";

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public PersonRelationship(AppContext c, String tag) {
		super(c,tag,c.getService(SessionService.class).getLoginFactory());
	}

	@Override
	protected String[] getDefaultRoles(AppContext c, String table) {
		return new String[] {SUDO_ROLE};
	}

}