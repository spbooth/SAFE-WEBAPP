//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class AdminOperationKey<T extends TableStructureTransitionTarget> extends AccessControlTransitionKey<T> {

	/**
	 * @param t
	 * @param name
	 * @param help
	 */
	public AdminOperationKey( String name, String help) {
		super(TableStructureTransitionTarget.class, name, help);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.AccessControlTransitionKey#allow(uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean allow(SessionService serv) {
		return serv.hasRole(SessionService.ADMIN_ROLE);
	}

}
