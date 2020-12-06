package uk.ac.ed.epcc.webapp.session.perms;

import java.util.LinkedHashSet;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** an AND combination of {@link PermissionClause}s
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class AndPermissionClause<T extends DataObject> extends LinkedHashSet<PermissionClause<T>> implements PermissionClause<T>{

	public AndPermissionClause(Class<T> target) {
		super();
		this.target = target;
	}

	private final Class<T> target;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public <X> X accept(PermissionVisitor<X,T> visitor) throws UnknownRelationshipException {
		return visitor.visitAndPermissionClause(this);
	}
	
	@Override
	public Class<T> getTarget() {
		return target;
	}
	public String toString() {
		return "AND("+super.toString()+")";
	}
}
