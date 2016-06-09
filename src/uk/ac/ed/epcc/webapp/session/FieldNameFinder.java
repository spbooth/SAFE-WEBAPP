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
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** A generic {@link AppUserNameFinder} that stores the name in a database field.
 * 
 * Configuration parameters:
 * <ul>
 * <li> <em><b>NameFinder.</b>realm<b>.field</b></em> field name defaults to realm.</li> 
 * <li> <em><b>NameFinder.</b>realm<b>.label</b></em> label defaults to realm.</li> 
 * <li> <em><b>NameFinder.</b>realm<b>.user_supplied</b></em> Is name user supplied default false</li> 
 * <li> <em><b>NameFinder.</b>realm<b>.length</b></em> field width.</li> 
 * @author spb
 *
 */

public class FieldNameFinder<AU extends AppUser, F extends FieldNameFinder> extends AppUserNameFinder<AU,F> {

	/**
	 * 
	 */
	private static final String PROPERTY_PREFIX = "NameFinder.";

	
    //private final boolean user_supplied;
	
	/**
	 * @param factory
	 * @param realm
	 */
	public FieldNameFinder(AppUserFactory factory, String realm) {
		super(factory, realm);	
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		return getRecord(object).getStringProperty(getField());
	}
	/** If there are varient forms of the names this normalises the name to the form stored in the database
	 * 
	 * @param name
	 * @return
	 */
	protected String normalizeName(String name){
		return name;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getNameLabel()
	 */
	@Override
	public String getNameLabel() {
		return getContext().getInitParameter(PROPERTY_PREFIX+getRealm()+".label", getRealm());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getStringFinderFilter(java.lang.Class, java.lang.String)
	 */
	@Override
	public SQLFilter getStringFinderFilter(Class target, String name) {
		return new SQLValueFilter<AU>(getFactory().getTarget(), getRepository(), getField(), normalizeName(name));
	}

	

	@Override
	public boolean userVisible() {
		return getContext().getBooleanParameter(PROPERTY_PREFIX+getRealm()+".user_supplied", false);
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(getField(), new StringFieldType(true, null, getContext().getIntegerParameter(PROPERTY_PREFIX+getRealm()+".length", 128)));
		try{
			spec.new Index(getRealm()+"_index", true, getField());
		}catch(InvalidArgument e){
			getLogger().error("Problem making index ",e);
		}
		return spec;
	}

	@Override
	public Map<String, String> addTranslations(Map<String, String> translations) {
		String label = getNameLabel();
		if( label != null && label.trim().length() > 0){
			translations.put(getField(), label);
		}
		return super.addTranslations(translations);
	}

	

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		if( ! userSet() ){
			suppress.add(getField());
		}
		return suppress;
	}

	protected boolean userSet() {
		return userVisible();
	}

	@Override
	public void customiseUpdateForm(Form f, AU target, SessionService operator) {
		Field ff = f.getField(getField());
		if( ff != null ){
			ff.addValidator(new ParseFactoryValidator<AU>(this, target));
			if( ! operator.hasRole(SessionService.ADMIN_ROLE)){
				// only admin can edit in update
				ff.lock();
			}
		}
	}

	@Override
	public void customiseForm(Form f) {
		Field ff = f.getField(getField());
		if( ff != null ){
			ff.addValidator(new ParseFactoryValidator<AU>(this, null));
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#setName(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setName(AU user, String name) {
		getRecord(user).setOptionalProperty(getField(), normalizeName(name));
	}

	/**
	 * @return the field
	 */
	private String getField() {
		return getContext().getInitParameter(PROPERTY_PREFIX+getRealm()+".field", getRealm());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#active()
	 */
	@Override
	public boolean active() {
		return getRepository().hasField(getField());
	}

}