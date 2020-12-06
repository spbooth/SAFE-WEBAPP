package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

public interface PermissionVisitor<X, T extends DataObject> {

	public X visitAndPermissionClause(AndPermissionClause<T> and)throws UnknownRelationshipException;
	public X visitOrPermissionClause(OrPermissionClause<T> or)throws UnknownRelationshipException;
	public X visitNegatingClause(NegatingClause<T> n)throws UnknownRelationshipException;
	public X visitRemotePermissionClause(RemotePermissionClause<T> r)throws UnknownRelationshipException;
	public X visitBinaryPermissionClause(BinaryPermissionClause<T> b)throws UnknownRelationshipException;
	public X visitGlobalPermissionClause(GlobalPermissionClause<T> b)throws UnknownRelationshipException;
	public <A extends AppUser> X visitAccessRolePermissionClause(AccessRoleProviderPermissionClause<A, T> a)throws UnknownRelationshipException;
	public X visitFilterPermissionClause(FilterPermissionClause<T> f)throws UnknownRelationshipException;
}
