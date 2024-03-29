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

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.SetInput;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/** A {@link NamedFilterProvider} that selects {@link AppUser}s via their role names (as defined in the role table)
 * @author Stephen Booth
 *
 */
public class RoleFilterProvider<T extends AppUser> implements NamedFilterProvider<T>,Contexed,Selector<Input<String>> {


	private final AppUserFactory<T> fac;
	
	/**
	 * 
	 */
	public RoleFilterProvider(AppContext conn) {
		fac = conn.getService(SessionService.class).getLoginFactory();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#getNamedFilter(java.lang.String)
	 */
	@Override
	public BaseFilter<T> getNamedFilter(String name) {
		DatabaseService db_service = getContext().getService(DatabaseService.class);
		try {
			
			return fac.new RoleFilter(db_service.getSQLContext(), name);
		} catch (SQLException e) {
			db_service.logError("Error making role filter",e);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#addFilterNames(java.util.Set)
	 */
	@Override
	public void addFilterNames(Set<String> names) {
		names.addAll(getContext().getService(SessionService.class).getStandardRoles());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return fac.getContext();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
	 */
	@Override
	public Input<String> getInput() {
		Set<String> roles = new LinkedHashSet<>();
		addFilterNames(roles);
		SetInput<String> input = new SetInput<>(roles);
		return input;
	}

}
