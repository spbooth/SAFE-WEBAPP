package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.RemoteAccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;
/** A {@link PermissionVisitor} that generates 
 * filters for {@link AppUser}s that have the relation with a specified target
 * 
 * @author Stephen Booth
 *
 * @param <U> type of {@link AppUser}
 * @param <T> type of target
 */
public class PersonInRelationshipRoleFilterPermissionVisitor<U extends AppUser,T extends DataObject> extends MakeFilterPermissionVisitor<U, T> {
	public PersonInRelationshipRoleFilterPermissionVisitor(SessionService<U> sess, DataObjectFactory<T> fac, T target) {
		super();
		this.sess = sess;
		this.fac = fac;
		this.target = target;
	}
	private final SessionService<U> sess;
	private final DataObjectFactory<T> fac;
	private final T target;
	@Override
	public BaseFilter<U> visitRemotePermissionClause(RemotePermissionClause<T> r) throws UnknownRelationshipException {
		String remote_role=r.getRelationship();
		String link_field=r.getField();
		RemoteAccessRoleProvider<U, T, ?> rarp = new RemoteAccessRoleProvider<>(sess, fac, link_field,r.getFieldOptional());
		BaseFilter<U> fil = rarp.personInRelationFilter(sess, remote_role, target);
		if( fil == null ){
			throw new UnknownRelationshipException(link_field+"->"+remote_role);
		}
		return fil;
	}
	@Override
	public BaseFilter<U> visitGlobalPermissionClause(GlobalPermissionClause<T> b) throws UnknownRelationshipException {
		// roles don't enumerate
		return new GenericBinaryFilter<>(getFactory().getTarget(),false);
	}
	@Override
	public <A extends AppUser> BaseFilter<U> visitAccessRolePermissionClause(AccessRoleProviderPermissionClause<A, T> a)
			throws UnknownRelationshipException {
		
		AccessRoleProvider<U, T> provider = (AccessRoleProvider<U, T>) a.getProvider();
		return provider.personInRelationFilter(sess, a.getRole(), target);
	}
	@Override
	public BaseFilter<U> visitFilterPermissionClause(FilterPermissionClause<T> f) throws UnknownRelationshipException {
		if( target == null ) {
			try {
				// check that some target object matches the filter
				return new GenericBinaryFilter<U>(getFactory().getTarget(), fac.exists(f.getFilter()));
			} catch (DataException e) {
				throw new UnknownRelationshipException(f.getName());
			}
		}
		return new GenericBinaryFilter<U>(getFactory().getTarget(), fac.matches(f.getFilter(), target));
	}
	@Override
	public DataObjectFactory<U> getFactory() {
		return sess.getLoginFactory();
	}
	
}
