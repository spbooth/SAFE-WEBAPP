package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.content.ContentList;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

/**  a {@link PermissionVisitor} to generate an explanation operation for a {@link PermissionClause}
 * This might be a String but Object is returned to allow a 
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class ExplainPermissionVisitor<T extends DataObject> implements PermissionVisitor<Object, T> {

	@Override
	public Object visitAndPermissionClause(AndPermissionClause<T> and) throws UnknownRelationshipException {
		ContentList res = new ContentList("AND");
		for( PermissionClause<T> c : and) {
			res.add(c.accept(this));
		}
		return res;
	}

	@Override
	public Object visitOrPermissionClause(OrPermissionClause<T> or) throws UnknownRelationshipException {
		ContentList res = new ContentList("OR");
		for( PermissionClause<T> c : or) {
			res.add(c.accept(this));
		}
		return res;
	}

	@Override
	public Object visitNegatingClause(NegatingClause<T> n) throws UnknownRelationshipException {
		ContentList not = new ContentList("NOT");
		not.add(n.getInner().accept(this));
		return not;
	}

	@Override
	public Object visitRemotePermissionClause(RemotePermissionClause<T> r) {
		if( r.getFieldOptional()) {
			return "OptionalRemoteRelationship("+r.getField()+"->"+r.getRelationship()+")";
		}
		return "RemoteRelationship("+r.getField()+"->"+r.getRelationship()+")";
	}

	@Override
	public Object visitBinaryPermissionClause(BinaryPermissionClause<T> b) {
		return b.getValue();
	}

	@Override
	public Object visitGlobalPermissionClause(GlobalPermissionClause<T> b) {
		return "GlobalRole("+b.getRole()+")";
	}

	@Override
	public <A extends AppUser> Object visitAccessRolePermissionClause(AccessRoleProviderPermissionClause<A, T> a) {
		return "AccessRoleProvider("+a.getName()+")";
	}

	@Override
	public Object visitFilterPermissionClause(FilterPermissionClause<T> f) {
		
		return "NamedFilter("+f.getName()+")";
	}

}
