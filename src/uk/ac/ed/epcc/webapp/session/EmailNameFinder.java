//| Copyright - The University of Edinburgh 2015                            |
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

import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** A {@link AppUserNameFinder} to handle users canonical Email
 * 
 * The email address is required not to resolve to an existing name on the factory unless it
 * corresponds to the current object.
 * (we query the full factory in case we have a {@link NameFinder} for known aliases as well.
 * @author spb
 * @param <AU> type of AppUser
 *
 */

public class EmailNameFinder<AU extends AppUser> extends AppUserNameFinder<AU,EmailNameFinder<AU>> {

	/** property to set the email input box width
	 * 
	 */
	public static final String EMAIL_MAXWIDTH_PROP = "email.maxwidth";
	public static final String EMAIL = "Email";
	
	@Override
	public TableSpecification modifyDefaultTableSpecification(
			TableSpecification spec, String table) {
		spec.setField(EmailNameFinder.EMAIL, new StringFieldType(true, null, EmailInput.MAX_EMAIL_LENGTH));
		try{
			spec.new Index("Email_index", true,EMAIL);
		}catch(Exception e){
			getLogger().error("Error making index",e);
		}
		return spec;
	}

	

	@Override
	public Map<String, Object> addSelectors(Map<String, Object> selectors) {
		EmailInput email = new EmailInput();
		email.setBoxWidth(getContext().getIntegerParameter(EMAIL_MAXWIDTH_PROP, 32));
		selectors.put(EMAIL, email);
		return super.addSelectors(selectors);
	}

	

	

	/**
	 * @param factory
	 */
	public EmailNameFinder(AppUserFactory<AU> factory) {
		super(factory,EMAIL);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		return getRecord(object).getStringProperty(EMAIL);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getNameLabel()
	 */
	@Override
	public String getNameLabel() {
		return EMAIL;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getStringFinderFilter(java.lang.Class, java.lang.String)
	 */
	@Override
	public SQLFilter<AU> getStringFinderFilter(Class<? super AU> target,
			String name) {
		return new SQLValueFilter<AU>(getFactory().getTarget(), getRepository(), EMAIL, name);
	}



	@Override
	public void customiseForm(Form f) {
		f.getField(EMAIL).setValidator(new ParseFactoryValidator<AU>(this, null));
	}



	@Override
	public void customiseUpdateForm(Form f, AU target, SessionService operator) {
		
		Field field = f.getField(EMAIL);
		// Current target is allowed.
		field.setValidator(new ParseFactoryValidator<AU>(this, target));
		if( ! operator.hasRole(SessionService.ADMIN_ROLE)){
			// only admin can edit in update
			field.lock();
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#setName(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setName(AU user, String name) {
		getRecord(user).setOptionalProperty(EMAIL, name);
		
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#active()
	 */
	@Override
	public boolean active() {
		return getRepository().hasField(EMAIL);
	}


}