package uk.ac.ed.epcc.webapp.session;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;

/** A {@link Composite} that defines a relationship on the target object based on a role definition
 * stored in a database field.
 * Note that this will be inefficient when selecting target objects as it needs to be an {@link AcceptFilter} 
 * @author Stephen Booth
 *
 * @param <BDO>
 */
public class RoleAccessComposite<BDO extends DataObject,A extends AppUser> extends Composite<BDO, RoleAccessComposite> implements AccessRoleProvider<A, BDO>{
	protected final static String ACCESS_ROLE="AccessRole";
	public static final String ROLE_BASED_ACCESS_RELATIONSHIP ="RoleAccess";
	public RoleAccessComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}

	@Override
	protected Class<? super RoleAccessComposite> getType() {
		return RoleAccessComposite.class;
	}
	public class RoleAccessFilter implements AcceptFilter<BDO>{
		private SessionService<A> sess;
		private A user;
		protected RoleAccessFilter(SessionService<A>sess,A user) {
			this.sess=sess;
			this.user=user;
		}
		@Override
		public boolean test(BDO o) {
			String list = accessRoleList(o);
			if( list == null || list.isEmpty()) {
				return false;
			}
			String roles[] = list.split("\\s*,\\s*");
			if( user == null) {
				for(String r : roles ) {
					if( sess.hasRole(r)) {
						return true;
					}
				}
			}else {
				return sess.canHaveRoleFromList(user, roles);
			}
			return false;
		}


	}
	private String accessRoleList(BDO target){
		return getRecord(target).getStringProperty(ACCESS_ROLE);
	}
	
	public boolean hasAccessRoleList(BDO target) {
		String list  = accessRoleList(target);
		return list != null && ! list.isEmpty();
	}
	public void setAccessRoleList(BDO target,String roles) {
		// only needed by tests usually set via a form
		getRecord(target).setProperty(ACCESS_ROLE, roles);
	}

	@Override
	public BaseFilter<BDO> hasRelationFilter(String role, A user) {
		if( role.equals(ROLE_BASED_ACCESS_RELATIONSHIP)) {
			if( ! getRepository().hasField(ACCESS_ROLE)) {
				return new FalseFilter();
			}
			// Can only match targets with an access role set to narrow the selection
			return getFactory().getAndFilter(
					new NullFieldFilter<BDO>( getRepository(), ACCESS_ROLE, false),
					new RoleAccessFilter(getContext().getService(SessionService.class), user));
		}
		return null;
	}

	@Override
	public BaseFilter<A> personInRelationFilter(SessionService<A> sess, String role, BDO target) {
		if( ROLE_BASED_ACCESS_RELATIONSHIP.equals(role)) {
			if( target != null ) {
				String list = accessRoleList(target);
				AppUserFactory<A> fac =  sess.getLoginFactory();
				if( list != null && ! list.isEmpty()) {
					return sess.getGlobalRoleFilter(list.split("\\s*,\\s*"));
				}else {
					return new FalseFilter();
				}
			}
		}
		return null;
	}

	@Override
	public boolean providesRelationship(String role) {
		if( role.equals(ROLE_BASED_ACCESS_RELATIONSHIP)) {
			return true;
		}
		return false;
	}

	@Override
	public void addRelationships(Set<String> roles) {
		roles.add(ROLE_BASED_ACCESS_RELATIONSHIP);
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(ACCESS_ROLE, new StringFieldType(true, null, 32));
		return spec;
	}

	@Override
	public Set<String> addOptional(Set<String> optional) {
		optional.add(ACCESS_ROLE);
		return optional;
	}



	@Override
	public Map<String, String> addTranslations(Map<String, String> translations) {
		translations.put(ACCESS_ROLE, "Role for Group address");
		return translations;
	}
}
