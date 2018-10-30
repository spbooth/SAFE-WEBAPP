//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.preferences;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A class to hold user {@link Preference} settings.
 * @author spb
 * @param <S> Type of Setting object
 *
 */

public class UserSettingFactory<S extends UserSettingFactory.UserSetting> extends DataObjectFactory<S> {
	public static final Feature PER_USER_SETTINGS_FEATURE = new Feature("user.preferences",true,"Allow per user preferences");
	public static final String PERSON_FIELD="PersonID";
	public static final String SETTING_FIELD="Setting";
	public static final String VALUE_FIELD="Value";
	public static class UserSetting extends DataObject{

		/**
		 * @param r
		 */
		protected UserSetting(Record r) {
			super(r);
		}
		
		public void setName(String name){
			record.setProperty(SETTING_FIELD, name);
		}
		public void setPerson(AppUser u){
			record.setProperty(PERSON_FIELD, u.getID());
		}
		public void setValue(boolean val){
			record.setProperty(VALUE_FIELD, val);
		}
		public boolean getValue(){
			return record.getBooleanProperty(VALUE_FIELD);
		}
	}

	public boolean getPreference(Preference pref){
		S setting = makeSetting(pref,false);
		if( setting == null ){
			return pref.defaultSetting(getContext());
		}
		return setting.getValue();
	}
	public boolean hasPreference(Preference pref){
		S setting = makeSetting(pref,false);
		if( setting == null ){
			return false;
		}
		return true;
	}
	
	public void clearPreference(Preference pref) throws DataFault{
		S setting = makeSetting(pref,false);
		if( setting != null){
			setting.delete();
		}
	}
	public void setPreference(Preference pref, boolean value){
		S setting = makeSetting(pref,true);

		setting.setValue(value);
		try {
			setting.commit();
		} catch (DataFault e) {
			getContext().error(e,"Error setting value");
		}

	}
	
	
	public S makeSetting(Preference pref,boolean create){
		SessionService serv = getContext().getService(SessionService.class);
		if( serv == null || ! serv.haveCurrentUser() || ! PER_USER_SETTINGS_FEATURE.isEnabled(getContext())){
			return null;
		}
		SQLAndFilter<S> fil = new SQLAndFilter<S>(getTarget());
		fil.addFilter(new SQLValueFilter<S>(getTarget(),res, SETTING_FIELD, pref.getName()));
		fil.addFilter(new SQLValueFilter<S>(getTarget(),res, PERSON_FIELD, serv.getCurrentPerson().getID()));
		try {
			S result = find(fil,true);
			if( result == null ){
				if( create ){
					result = makeBDO();
					result.setName(pref.getName());
					result.setPerson(serv.getCurrentPerson());
					result.setValue(pref.defaultSetting(getContext()));
					result.commit();
				}
			}
			return result;
		} catch (DataException e) {
			getContext().error(e,"Error getting setting");
		}
		return null;
	}
	
	public UserSettingFactory(AppContext conn){
		setContext(conn, "UserSettings");
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new UserSetting(res);
	}

	@Override
	public Class<S> getTarget() {
		return (Class<S>) UserSetting.class;
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("SettingID");
		spec.setField(PERSON_FIELD, new IntegerFieldType());
		spec.setField(SETTING_FIELD, new StringFieldType(false, "", 128));
		spec.setField(VALUE_FIELD, new BooleanFieldType(false,false));
		return spec;
	}

	
}