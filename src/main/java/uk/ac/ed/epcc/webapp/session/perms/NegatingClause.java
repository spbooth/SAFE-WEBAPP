package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** A {@link PermissionClause} that negates another
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class NegatingClause<T extends DataObject> implements PermissionClause<T> {

	
	public NegatingClause(Class<T> target, PermissionClause<T> inner) {
		super();
		this.inner = inner;
	}

	@Override
	public Class<T> getTarget() {
		return inner.getTarget();
	}

	@Override
	public <X> X accept(PermissionVisitor<X,T> visitor) throws UnknownRelationshipException {
		return visitor.visitNegatingClause(this);
	}
	private final PermissionClause<T> inner;
	
	public PermissionClause<T> getInner(){
		return inner;
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NegatingClause other = (NegatingClause) obj;
		if (inner == null) {
			if (other.inner != null)
				return false;
		} else if (!inner.equals(other.inner))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NOT[" + inner + "]";
	}
}
