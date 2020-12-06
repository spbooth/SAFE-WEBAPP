package uk.ac.ed.epcc.webapp.session.perms;

import java.util.LinkedHashSet;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** An OR combination of  {@link PermissionClause}s
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class OrPermissionClause<T extends DataObject> extends LinkedHashSet<PermissionClause<T>> implements PermissionClause<T>{

	public OrPermissionClause(Class<T> target,DataObjectFactory<T> fac) {
		super();
		this.target = target;
		this.fac=fac;
	}

	private final Class<T> target;
	private final DataObjectFactory<T> fac;
	public DataObjectFactory<T> getFac() {
		return fac;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public <X> X accept(PermissionVisitor<X,T> visitor) throws UnknownRelationshipException {
		return visitor.visitOrPermissionClause(this);
	}

	@Override
	public Class<T> getTarget() {
		return target;
	}
	public String toString() {
		return "OR("+super.toString()+")";
	}
}
