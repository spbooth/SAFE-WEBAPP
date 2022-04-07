package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.RemoteAccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.GlobalRoleFilter;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** a {@link PermissionVisitor} for generating a filter on the target object
 * based on the current session
 * @author Stephen Booth
 *
 * @param <A> type of {@link AppUser}
 * @param <T> type of target
 */
public class SessionRelationshipRoleFilterPermissionVisitor<A extends AppUser,T extends DataObject> extends MakeFilterPermissionVisitor<T, T> implements PermissionVisitor<BaseFilter<T>, T> {
	/** Create a visitor
	 * 
	 * @param sess {@link SessionService}
	 * @param fac2
	  */
	public SessionRelationshipRoleFilterPermissionVisitor(SessionService<A> sess, DataObjectFactory<T> fac2) {
		super();
		this.sess = sess;
		this.fac2 = fac2;
	}
	private final SessionService<A> sess;
	private final DataObjectFactory<T> fac2;
	
	@Override
	public BaseFilter<T> visitRemotePermissionClause(RemotePermissionClause<T> r) throws UnknownRelationshipException {
		String remote_role=r.getRelationship();
		String link_field=r.getField();
		RemoteAccessRoleProvider<A, T, ?> rarp = new RemoteAccessRoleProvider<>(sess, fac2, link_field,r.getFieldOptional());
		BaseFilter<T> fil = rarp.hasRelationFilter(remote_role, sess);
		if( fil == null ){
			throw new UnknownRelationshipException(link_field+"->"+remote_role+" (null filter)");
		}
		return fil;
	}
	
	@Override
	public BaseFilter<T> visitGlobalPermissionClause(GlobalPermissionClause<T> b) {
		// The global role filter can allow relationship asserted from the container.
		return new GlobalRoleFilter<>(sess, b.getRole());
	}
	@Override
	public <U extends AppUser> BaseFilter<T> visitAccessRolePermissionClause(AccessRoleProviderPermissionClause<U, T> a) {
			if( ! sess.haveCurrentUser()) {
				return new FalseFilter<T>(getFactory().getTarget());
			}
			return a.getProvider().hasRelationFilter(a.getRole(),(SessionService<U>) sess);
	}
	@Override
	public BaseFilter<T> visitFilterPermissionClause(FilterPermissionClause<T> f) {
		return f.getFilter();
	}
	@Override
	public DataObjectFactory<T> getFactory() {
		return fac2;
	}
}
