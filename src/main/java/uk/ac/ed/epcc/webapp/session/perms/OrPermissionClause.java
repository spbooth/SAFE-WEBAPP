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

	public OrPermissionClause(DataObjectFactory<T> fac) {
		super();
		this.fac=fac;
	}


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

	
	public String toString() {
		return "OR("+super.toString()+")";
	}
}
