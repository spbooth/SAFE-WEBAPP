package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.content.TemplateContributor;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** Policy object to add an auto-generated global name to an {@link AppUser}.
 * 
 * @author spb
 *
 */
public abstract class GlobalNamePolicy<AU extends AppUser, X extends GlobalNamePolicy> extends FieldNameFinder<AU,X> implements AppUserCommitObserver<AU>, AnonymisingComposite<AU>, TemplateContributor<AU>{
	private final boolean anonymise;
	public GlobalNamePolicy(AppUserFactory<AU> factory, String realm) {
		super(factory, realm);
		anonymise=factory.getContext().getBooleanParameter("person.anonymise."+realm, true);
	}

	/** Generate a unique global name for the uncommitted person.
	 * 
	 * @param p uncommitted person.
	 * @return
	 */
	public abstract  String getName(AU p);

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserCommitObserver#pre_commit(uk.ac.ed.epcc.webapp.session.AppUser, boolean)
	 */
	@Override
	public void pre_commit(AU person, boolean dirty) throws DataFault {
		if( active()){
			String global_name = getCanonicalName(person);
			if( global_name == null || global_name.isEmpty()){
				getRecord(person).setProperty(getRealm(), getName(person));
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserCommitObserver#post_commit(uk.ac.ed.epcc.webapp.session.AppUser, boolean)
	 */
	@Override
	public void post_commit(AU person, boolean changed) throws DataFault {
		
		
	}
	protected final boolean userSet() {
		return false;
	}
	
	@Override
	public void anonymise(AU target) {
		if( anonymise) {
			getRecord(target).setProperty(getRealm(), "Person-"+target.getID());
		}
	}

	@Override
	public void setTemplateContent(TemplateFile template, String prefix, AU target) {
		String globalname = getCanonicalName(target);
		if( globalname != null ){
			template.setProperty(prefix+getRealm(), globalname);
		}
		
	}

}