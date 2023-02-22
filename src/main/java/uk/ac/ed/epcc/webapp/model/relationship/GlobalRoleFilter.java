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
package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An {@link AcceptFilter} that accepts or rejects all targets based on whether the current
 * user possesses a global role.
 * This can be used to map global roles (such as Admin) into the filter returned by a {@link AccessRoleProvider}.
 * @author spb
 *
 */
public class GlobalRoleFilter<T> implements BinaryFilter<T> {

	/**
	 * @param session
	 * @param role
	 */
	public GlobalRoleFilter(SessionService<?> session, String role) {
		super();
		this.session = session;
		this.role = role;
	}

	private final SessionService<?> session;
	private final String role;
	

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlobalRoleFilter other = (GlobalRoleFilter) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter#getBooleanResult()
	 */
	@Override
	public boolean getBooleanResult() {
		return session.hasRole(role);
	}

	public String toString() {
		return "GlobalRoleFilter("+role+")";
	}
}
