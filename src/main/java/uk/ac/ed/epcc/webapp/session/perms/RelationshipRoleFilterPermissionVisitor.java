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
 * 
 * @author Stephen Booth
 *
 * @param <A> type of {@link AppUser}
 * @param <T> type of target
 */
public class RelationshipRoleFilterPermissionVisitor<A extends AppUser,T extends DataObject> extends MakeFilterPermissionVisitor<T, T> implements PermissionVisitor<BaseFilter<T>, T> {
	/** Create a visitor
	 * 
	 * @param sess {@link SessionService}
	 * @param fac2
	 * @param person {@link AppUser} to check. This should be null to check the current user
	 */
	public RelationshipRoleFilterPermissionVisitor(SessionService<A> sess, DataObjectFactory<T> fac2, A person) {
		super();
		this.sess = sess;
		this.fac2 = fac2;
		this.person = person;
	}
	private final SessionService<A> sess;
	private final DataObjectFactory<T> fac2;
	private final A person;
	
	@Override
	public BaseFilter<T> visitRemotePermissionClause(RemotePermissionClause<T> r) throws UnknownRelationshipException {
		String remote_role=r.getRelationship();
		String link_field=r.getField();
		RemoteAccessRoleProvider<A, T, ?> rarp = new RemoteAccessRoleProvider<>(sess, fac2, link_field,r.getFieldOptional());
		BaseFilter<T> fil = rarp.hasRelationFilter(remote_role, person);
		if( fil == null ){
			throw new UnknownRelationshipException(link_field+"->"+remote_role+" (null filter)");
		}
		return fil;
	}
	
	@Override
	public BaseFilter<T> visitGlobalPermissionClause(GlobalPermissionClause<T> b) {
		if( person == null){
			// Only the global role filter can allow relationship without a current person
			// as roles may be asserted from the container.
			return new GlobalRoleFilter<>(sess, b.getRole());
		}else{

			return new GenericBinaryFilter<>(fac2.getTarget(),sess.canHaveRole(person, b.getRole()));
		}
	}
	@Override
	public <U extends AppUser> BaseFilter<T> visitAccessRolePermissionClause(AccessRoleProviderPermissionClause<U, T> a) {
		if( person == null) {
			if( ! sess.haveCurrentUser()) {
				return new FalseFilter<T>(getFactory().getTarget());
			}
			return a.getProvider().hasRelationFilter(a.getRole(), (U) sess.getCurrentPerson());
		}
		return a.getProvider().hasRelationFilter(a.getRole(), (U) person);
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
