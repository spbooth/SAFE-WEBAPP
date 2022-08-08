package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** A {@link PermissionClause} based on a global role
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class GlobalPermissionClause<T extends DataObject> implements PermissionClause<T> {

	public GlobalPermissionClause(Class<T> target, String role) {
		super();
		this.target = target;
		this.role = role;
	}

	private final Class<T> target;
	private final String role;
	
	@Override
	public Class<T> getTarget() {
		return target;
	}

	@Override
	public <X> X accept(PermissionVisitor<X, T> visitor) throws UnknownRelationshipException {
		return visitor.visitGlobalPermissionClause(this);
	}

	public String getRole() {
		return role;
	}

	@Override
	public int hashCode() {
		return role.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlobalPermissionClause other = (GlobalPermissionClause) obj;
		return other.role.equals(role) && other.target==target;
	}

	@Override
	public String toString() {
		return "GlobalPermissionClause[" + role + "]";
	}
}
