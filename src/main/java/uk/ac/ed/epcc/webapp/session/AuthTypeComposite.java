package uk.ac.ed.epcc.webapp.session;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.inputs.RegexpInput;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.twofactor.TwoFactorHandler;
/** A {@link Composite} that implements an authentication policy on an object and
 * exposes this as a relationship.
 * 
 * We use a regular expression to match the authentication type as this will allow
 * multiple types and/or resulting bearer tokens to match.
 * 
 * As we can only check the authentication data for a full session any bare users
 * will only match objects with no policy set or vice versa.
 * 
 * @param <BDO>
 */
public class AuthTypeComposite<BDO extends DataObject> extends Composite<BDO, AuthTypeComposite> implements AccessRoleProvider<AppUser, BDO> {

	public static final String POLICY_REL = "AuthPolicy";
	
	public static final String REQUIRE_MFA = "RequireMFA";
	public static final String REQUIRE_AUTH = "RequireAuthType";
	
	public AuthTypeComposite(DataObjectFactory<BDO> fac, String composite_tag) {
		super(fac, composite_tag);
	}

	@Override
	protected Class<? super AuthTypeComposite> getType() {
		return AuthTypeComposite.class;
	}
	public boolean passPolicy(SessionService sess,BDO target) {
		if( getRecord(target).getBooleanProperty(REQUIRE_MFA, false)) {
			if( ! TwoFactorHandler.usedTwoFactor(sess)) {
				return false;
			}
		}
		String regexp = getRecord(target).getStringProperty(REQUIRE_AUTH);
		if( regexp != null && ! regexp.isEmpty()) {
			try {
				Pattern p = Pattern.compile(regexp);
				String auth = sess.getAuthenticationType();
				if( ! p.matcher(auth).matches()) {
					return false;
				}
			}catch(Exception e) {
				getLogger().error("Error checking auth pattern", e);
			}
		}
		return true;
	}

	@Override
	public BaseFilter<BDO> hasRelationFilter(String role, AppUser user) {
		if( role.equals(POLICY_REL)) {
			Repository res = getRepository();
			if( res.hasField(REQUIRE_MFA) || res.hasField(REQUIRE_AUTH)) {
				// for a bare user match targets whith no policy required.
				// exclude all targets with a policy
				AndFilter<BDO> fil = getFactory().getAndFilter();
				if( res.hasField(REQUIRE_MFA)) {
					fil.addFilter(new SQLValueFilter<>(res, REQUIRE_MFA, Boolean.FALSE));
				}
				if( res.hasField(REQUIRE_AUTH)) {
					fil.addFilter(new NullFieldFilter<>(res, REQUIRE_AUTH, true));
				}
				return fil;
			}
			return new GenericBinaryFilter<>(true);
		}
		return null;
	}

	@Override
	public BaseFilter<AppUser> personInRelationFilter(SessionService<AppUser> sess, String role, BDO target) {
		if( role.equals(POLICY_REL)) {
			// match all users by default unless a policy is in place
			boolean match=true;
			if( getRecord(target).getBooleanProperty(REQUIRE_MFA, false)) {
				match=false;
			} else {
				String pattern = getRecord(target).getStringProperty(REQUIRE_AUTH);
				if( pattern != null && ! pattern.isEmpty()) {
					match=false;
				}
			}
			return new GenericBinaryFilter<>(match);
		}
		return null;
	}

	@Override
	public boolean providesRelationship(String role) {
		if( role != null && role.equals(POLICY_REL)) {
			return true;
		}
		return false;
	}

	@Override
	public void addRelationships(Set<String> roles) {
		roles.add(POLICY_REL);
		
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(REQUIRE_MFA, new BooleanFieldType(false, false));
		spec.setOptionalField(REQUIRE_AUTH, new StringFieldType(true, null, 64));
		return spec;
	}

	@Override
	public Map<String, Selector> addSelectors(Map<String, Selector> selectors) {
		selectors.put(REQUIRE_AUTH, RegexpInput::new);
		return selectors;
	}

	@Override
	public BaseFilter<BDO> hasRelationFilter(String role, SessionService<AppUser> sess) {
		if( role.equals(POLICY_REL)) {
			if( getRepository().hasField(REQUIRE_MFA) || getRepository().hasField(REQUIRE_AUTH)) {
				return new AcceptFilter<BDO>() {

					@Override
					public boolean test(BDO t) {
						return passPolicy(sess, t);
					}
				};
			}
			return new GenericBinaryFilter<>(true);
		}
		return null;
	}

}
