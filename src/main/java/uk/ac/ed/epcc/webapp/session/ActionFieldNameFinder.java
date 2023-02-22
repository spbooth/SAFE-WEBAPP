package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.lifecycle.ActionList;

/** A {@link FieldNameFinder} that runs actions when a canonical name is set
 * 
 * @author Stephen Booth
 *
 * @param <AU>
 */
public class ActionFieldNameFinder<AU extends AppUser> extends FieldNameFinder<AU, ActionFieldNameFinder> {

	public ActionFieldNameFinder(AppUserFactory<AU> factory, String realm) {
		super(factory, realm);
	}

	@Override
	public void setName(AU user, String name) {
		super.setName(user, name);
		try {
			getSetActions().action(user);
		} catch (Exception e) {
			getLogger().error("Error applying set actions", e);
		}
	}

	
	public ActionList<AU> getSetActions() {
		return new ActionList<AU>(getAppUserFactory().getTarget(),getAppUserFactory(), getRealm()+"-set");
	}
}
