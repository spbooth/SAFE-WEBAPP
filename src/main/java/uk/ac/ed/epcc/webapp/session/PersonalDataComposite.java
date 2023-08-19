package uk.ac.ed.epcc.webapp.session;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** Abstact superclass for {@link Composite}s that capture 
 * potentially sensitive attributes provided by the user. As they are potentially sensitive
 * we want to implement access control over who can view the data.
 * We also have the option to only request the attributes from specific people based on a policy
 * @author Stephen Booth
 *
 * @param <AU>
 * @param <X>
 */
public abstract class PersonalDataComposite<AU extends AppUser,X extends PersonalDataComposite> extends AppUserComposite<AU,X> implements SignupCustomiser, SummaryContributer<AU>{
	public static final String PREFER_NOT_TO_SPECIFY = "Prefer not to specify";
	public PersonalDataComposite(AppUserFactory<AU> fac,String tag) {
		super(fac,tag);
	
	}
	/** The name of the characteristic
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/** Add the set of form fields that should be suppressed if the
	 * characteristic is not requested
	 * 
	 */
	public abstract void  addFields(Set<String> fields);
	/** Get the set of form fields that should be suppressed if the
	 * characteristic is not requested
	 * 
	 * @return
	 */
	public final Set<String> getFields(){
		HashSet<String> fields = new HashSet<String>();
		addFields(fields);
		return fields;
	}

	/** The relationship needed to view the attributes.
	 * 
	 * @return
	 */
	public final String getViewRelationship() {
		return "View"+getName();
	}
	/** Do we request globally from all {@link AppUser}s
	 * 
	 * @return
	 */
	public boolean requestGlobally() {
		return Feature.checkDynamicFeature(getContext(), "person.request_global."+getName(), false);
	}
	/** Has the user provided personal information and should retain edit capability 
	 * (if they are not required to provide the information any more)
	 * in order to be able to remove it. 
	 * A {@link #PREFER_NOT_TO_SPECIFY} value does not count as having provided data.
	 * 
	 * @param user
	 * @return
	 */
	public abstract boolean hasData(AU user);
	/** Are there any requestable fields
	 * 
	 * @return
	 */
	public final boolean canRequest() {
		for(String field : getFields()) {
			if( getRepository().hasField(field)) {
				return true;
			}
		}
		return false;
	}
	/** Should the characteristic be requested for a particular user. 
	 * 
	 * @param user
	 * @return
	 */
	public abstract boolean requestCharacteristic(AU user);

	public boolean hasNotSpecify() {
    	return true;
    }
	@Override
	public Set<String> addOptional(Set<String> optional) {
		
		SessionService sess = getContext().getService(SessionService.class);
		if( (sess != null && sess.hasRole(SessionService.ADMIN_ROLE)) || ! hasNotSpecify()){
			// optional when root creating new people these should always be filled
			// in by the user as they can decline
			addFields(optional);
		}
		return optional;
	}
	
	@Override
	public final void addAttributes(Map<String, Object> attributes, AU target) {
		SessionService sess = getContext().getService(SessionService.class);
		if( sess.isSU()) {
			return;
		}
		if( sess.isCurrentPerson(target) || sess.hasRelationship(getFactory(), target, getViewRelationship(),false)) {

			addProtectedAttributes(attributes, target);
		}
	}
	protected abstract void addProtectedAttributes(Map<String, Object> attributes, AU target);
}
