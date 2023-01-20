package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** A {@link PermissionClause} representing a true/false value
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class BinaryPermissionClause<T extends DataObject> implements PermissionClause<T> {

	public BinaryPermissionClause( boolean value) {
		super();
		this.value = value;
	}

	private final boolean value;
	
	
	@Override
	public <X> X accept(PermissionVisitor<X, T> visitor) throws UnknownRelationshipException {
		return visitor.visitBinaryPermissionClause(this);
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
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
		BinaryPermissionClause other = (BinaryPermissionClause) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BinaryPermissionClause[" + value + "]";
	}
}
