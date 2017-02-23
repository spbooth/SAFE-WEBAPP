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
 * @param <T> target type
 *
 */
public class TableStructureAdminOperationKey<T extends TableStructureTransitionTarget> extends TableTransitionKey<T> {
	
	
	
	

	public TableStructureAdminOperationKey(String name, String help) {
		super(TableStructureTransitionTarget.class, name, help);
	}

	public TableStructureAdminOperationKey( String name) {
		super(TableStructureTransitionTarget.class, name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.AccessControlTransitionKey#allow(uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean allow(SessionService serv,T target) {
		return serv.hasRoleFromList(SessionService.ADMIN_ROLE,TableDeveloperKey.CHANGE_TABLE_STRUCTURE_ROLE);
	}

}
