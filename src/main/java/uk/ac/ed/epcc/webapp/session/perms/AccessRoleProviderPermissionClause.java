package uk.ac.ed.epcc.webapp.session.perms;

import java.util.function.Supplier;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** A {@link PermissionClause} wrapping an {@link AccessRoleProvider}
 * 
 * @author Stephen Booth
 *
 * @param <U>
 * @param <T>
 */
public class AccessRoleProviderPermissionClause<U extends AppUser,T extends DataObject> implements PermissionClause<T> {

	public AccessRoleProviderPermissionClause(AccessRoleProvider<U, T> provider, Supplier<String> name,String role) {
		super();
		this.provider = provider;
		this.name = name;
		this.role = role;
	}
	
	private final AccessRoleProvider<U, T> provider;
	public AccessRoleProvider<U, T> getProvider() {
		return provider;
	}
	private final Supplier<String> name;
	public String getName() {
		return name.get();
	}
	private final String role;
	public String getRole() {
		return role;
	}

	
	@Override
	public <X> X accept(PermissionVisitor<X, T> visitor) throws UnknownRelationshipException {
		return visitor.visitAccessRolePermissionClause(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessRoleProviderPermissionClause other = (AccessRoleProviderPermissionClause) obj;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}
	public String toString() {
		return "AccessRolePermissionClause("+getName()+")";
	}
}
