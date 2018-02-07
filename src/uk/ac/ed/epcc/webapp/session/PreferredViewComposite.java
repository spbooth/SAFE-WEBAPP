//| Copyright - The University of Edinburgh 2017                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.session;

import java.util.Map;
import java.util.Set;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Classification;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;

/** An {@link AppUserComposite} that returns an optional  preferred view for 
 * {@link AppUser}s. This is when there are multiple flavours of an applications
 * sharing a database but we want to tie certain users to a certain view.
 * This is used to customise template email messages to the user.
 * 
 * If set the parameter <b>prefered_view.default</b> defines the view URL
 * (equivalent to <b>service.saf.url</b> that should be set for users created/signing-up).
 * This should normally only be set within the view in question.
 * 
 * @author James Perry
 * @param <AU> type of {@link AppUser}
 * 
 *
 */
public class PreferredViewComposite<AU extends AppUser> extends AppUserComposite<AU, PreferredViewComposite> implements NewSignupAction<AU>,EmailParamContributor<AU> {
	/**
	 * 
	 */
	
	private static final String PREFERRED_VIEW_PROP = "prefered_view_default";
	public static final String PREFERRED_VIEW = "PreferredView";

	public PreferredViewComposite(AppUserFactory<AU> fac) {
		super(fac);
	}
	
	PreferedViewFactory pvf= null;
	private PreferedViewFactory getViewFactory() {
		if( pvf == null) {
			pvf=getContext().makeObject(PreferedViewFactory.class, PREFERRED_VIEW);
		}
		return pvf;
	}
	private IndexedTypeProducer<PreferedView, PreferedViewFactory> getProducer(){
		return new IndexedTypeProducer<PreferedView, PreferedViewFactory>(getContext(), PREFERRED_VIEW, getViewFactory());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super PreferredViewComposite> getType() {
		return PreferredViewComposite.class;
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(PREFERRED_VIEW, new IntegerFieldType(true, null));
		return spec;
	}

	@Override
	public Map<String, String> addTranslations(Map<String, String> labels) {
		labels.put(PREFERRED_VIEW, getContext().expandText("Preferred ${service.website-name} view (used in emails)"));
		return labels;
	}

	@Override
	public Set<String> addOptional(Set<String> optional) {
		optional.add(PREFERRED_VIEW);
		return optional;
	}
	
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		// don't let normal users edit this, but admin can
		SessionService sess = getContext().getService(SessionService.class);
		if( sess == null || !sess.hasRole(SessionService.ADMIN_ROLE)){
			suppress.add(PREFERRED_VIEW);
		}
		return suppress;
	}

	@Override
	public Map<String, Object> addSelectors(Map<String, Object> selectors) {
		
		selectors.put(PREFERRED_VIEW, getViewFactory());
		return selectors;
	}
	
	public PreferedView getPreferredView(AU person) {
		return  getRecord(person).getProperty(getProducer());
	}
	
	public void setPreferredView(AU person, PreferedView c) {
		getRecord(person).setOptionalProperty(getProducer(),c);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addDefaults(java.util.Map)
	 */
	@Override
	public Map<String, Object> addDefaults(Map<String, Object> defaults) {

		PreferedView c = getDefaultView();
		if( c != null ) {
			defaults.put(PREFERRED_VIEW, c.getID());
		}


		return super.addDefaults(defaults);
	}

	/**
	 * @return
	 */
	public PreferedView getDefaultView() {
		String name = getContext().getExpandedProperty(PREFERRED_VIEW_PROP,"");
		if( name == null || name.isEmpty()) {
			return null;
		}
		try {
			return getViewFactory().makeFromString(name);
		} catch (DataFault e) {
			getLogger().error("Error getting default view", e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.NewSignupAction#newSignup(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void newSignup(AU user) throws Exception {
		PreferedView dv = getDefaultView();
		setPreferredView(user, dv);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.EmailParamContributor#addParams(java.util.Map, uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void addParams(Map<String, String> params, AU user) {
		if( user == null ) {
			return;
		}
		PreferedView preferredView = getPreferredView(user);
		if( preferredView != null ) {
			// This allows sub-classes to set more params.
			preferredView.addEmailParams(params);
		}
		
	}
}
