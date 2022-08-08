package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.RemoteAccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.*;
/** A {@link PermissionVisitor} that generates 
 * filters for {@link AppUser}s that have the relation with a specified target
 * 
 * @author Stephen Booth
 *
 * @param <U> type of {@link AppUser}
 * @param <T> type of target
 */
public class PersonInRelationshipRoleToFilterPermissionVisitor<U extends AppUser,T extends DataObject> extends MakeFilterPermissionVisitor<U, T> {
	public PersonInRelationshipRoleToFilterPermissionVisitor(SessionService<U> sess, DataObjectFactory<T> fac, SQLFilter<T> fil) {
		super();
		this.sess = sess;
		this.fac = fac;
		this.fil=fil;
	}
	private final SessionService<U> sess;
	private final DataObjectFactory<T> fac;
	private final SQLFilter<T> fil;
	
	private Logger getLogger() {
		return sess.getContext().getService(LoggerService.class).getLogger(getClass());
	}
	@Override
	public BaseFilter<U> visitRemotePermissionClause(RemotePermissionClause<T> r) throws UnknownRelationshipException {
		String remote_role=r.getRelationship();
		String link_field=r.getField();
		RemoteAccessRoleProvider<U, T, ?> rarp = new RemoteAccessRoleProvider<>(sess, fac, link_field,r.getFieldOptional());
		SQLFilter<U> result=null;
		try {
			result = rarp.personInRelationToFilter(sess, remote_role, fil);
		} catch (CannotUseSQLException e) {
			throw new NonSQLRelationshipException(link_field+"->"+remote_role,e);
		}
		if( result == null ){
			throw new UnknownRelationshipException(link_field+"->"+remote_role);
		}
		return result;
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
		try {
			return provider.personInRelationToFilter(sess, a.getRole(), fil);
		} catch (CannotUseSQLException e) {
			throw new NonSQLRelationshipException(a.getRole(),e);
		}
	}
	@Override
	public BaseFilter<U> visitFilterPermissionClause(FilterPermissionClause<T> f) throws UnknownRelationshipException {
		
		boolean exists=false;
		try {
			exists = fac.exists(new AndFilter<T>(fac.getTarget(),f.getFilter(),fil));
		} catch (DataException e) {
			getLogger().info("Error checking for filter match",e);
			throw new UnknownRelationshipException(f.getName());
		}
		return new GenericBinaryFilter<U>(getFactory().getTarget(), exists);
	}
	@Override
	public DataObjectFactory<U> getFactory() {
		return sess.getLoginFactory();
	}
	
}
