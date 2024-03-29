package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** A {@link PermissionClause} importing a relationship from a referenced table
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class RemotePermissionClause<T extends DataObject> implements PermissionClause<T> {
	public RemotePermissionClause(DataObjectFactory<T> fac, String field, String relationship,boolean optional_field) {
		super();
		this.fac = fac;
		this.field = field;
		this.relationship = relationship;
		this.is_optional= optional_field;
	}
	private final DataObjectFactory<T> fac;
	public DataObjectFactory<T> getFac() {
		return fac;
	}
	private final String field;
	public String getField() {
		return field;
	}
	private final String relationship;
	public String getRelationship() {
		return relationship;
	}
	private final boolean is_optional;
	public boolean getFieldOptional() {
		return is_optional;
	}
	
	@Override
	public <X> X accept(PermissionVisitor<X,T> visitor) throws UnknownRelationshipException {
		return visitor.visitRemotePermissionClause(this);
	}
	
	public String toString() {
		return "RemotePermissionClause("+field+"->"+relationship+")";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fac == null) ? 0 : fac.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + (is_optional ? 1231 : 1237);
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
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
		RemotePermissionClause other = (RemotePermissionClause) obj;
		if (fac == null) {
			if (other.fac != null)
				return false;
		} else if (!fac.equals(other.fac))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (is_optional != other.is_optional)
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		return true;
	}
}
