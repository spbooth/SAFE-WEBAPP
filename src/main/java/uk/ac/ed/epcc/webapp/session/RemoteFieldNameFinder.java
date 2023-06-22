package uk.ac.ed.epcc.webapp.session;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.RemoteAuthServlet;

/** A {@link FieldNameFinder} representing a remote identity set by external authentication
 * 
 * @author Stephen Booth
 *
 * @param <AU>
 * @param <F>
 */
public class RemoteFieldNameFinder<AU extends AppUser, F extends FieldNameFinder> extends FieldNameFinder<AU, F>implements AnonymisingComposite<AU>, NewSignupAction<AU> {

	public RemoteFieldNameFinder(AppUserFactory factory, String realm) {
		super(factory, realm);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingComposite#anonymise(uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void anonymise(AU target) {
		getRecord(target).setOptionalProperty(getField(), null);
	}

	@Override
	public void addEraseFields(Set<String> fields) {
		fields.add(getField());
	}
	@Override
	public void newSignup(AU user) throws Exception {
		if( active() ) {
			// Check for a remote identity generated before registration
			AppContext conn = getContext();
			if( conn.getBooleanParameter("register_pre_id_link."+getRealm(), true)) {
				SessionService<AU> sess = conn.getService(SessionService.class);
				String key = RemoteAuthServlet.REMOTE_AUTH_NAME_PREFIX+getRealm();
				String id = (String) sess.getAttribute(key);
				if( id != null && ! id.trim().isEmpty()) {
					setName(user, id);
					sess.removeAttribute(key);
					try {
						user.commit();
					} catch (DataFault e) {
						getLogger().error("Error setting saved webname for "+getRealm(),e);
					}
				}
			}
			
		}
		
	}

}
