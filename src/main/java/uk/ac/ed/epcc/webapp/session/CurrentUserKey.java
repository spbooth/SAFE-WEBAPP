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
package uk.ac.ed.epcc.webapp.session;

/** An {@link AppUserKey} for operations that (primarily) act on the current user.
 * 
 * Optionally an additional role may be specified that
 * 
 * These will be added automatically as navigation menu items.
 * 
 * @author Stephen Booth
 *
 */
public class CurrentUserKey extends AppUserKey {

	private final String additional_role;
	/**
	 * @param name
	 * @param text
	 * @param help
	 */
	public CurrentUserKey(String name, String text, String help) {
		this(name, text, help,null);
	}
	public CurrentUserKey(String name, String text, String help,String additional_role) {
		super(name, text, help);
		this.additional_role=additional_role;
	}

	/**
	 * @param name
	 * @param help
	 */
	public CurrentUserKey(String name, String help) {
		this(name, null,help,null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public CurrentUserKey(String name) {
		this(name,null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserKey#allow(uk.ac.ed.epcc.webapp.session.AppUser, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean allow(AppUser user, SessionService op) {
		return op != null && user != null && (op.isCurrentPerson(user) || (additional_role != null && op.hasRelationship(op.getLoginFactory(), user, additional_role,false)))&& allowState(user, op);
	}

	public boolean allowState(AppUser user, SessionService op) {
		return true;
	}
	
	public boolean addMenu(AppUser user) {
		return true;
	}
}
