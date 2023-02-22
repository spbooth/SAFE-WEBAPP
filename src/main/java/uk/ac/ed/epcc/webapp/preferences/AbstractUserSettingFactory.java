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
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
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

/** An abstract class to hold user {@link Preference} settings.
 * 
 * This can be sub-classed to store settings with different datatypes.
 * @author spb
 * @param <D> Type of the setting value
 * @param <T> Database persistance type
 * @param <S> Type of Setting object
 *
 */

public abstract class AbstractUserSettingFactory<D,T, S extends AbstractUserSettingFactory.UserSetting<D>> extends DataObjectFactory<S> {
	public static final Feature PER_USER_SETTINGS_FEATURE = new Feature("user.preferences",true,"Allow per user preferences");
	public static final String PERSON_FIELD="PersonID";
	public static final String SETTING_FIELD="Setting";
	public static final String VALUE_FIELD="Value";
	public abstract  static class UserSetting<D> extends DataObject{

		/**
		 * @param r
		 */
		protected UserSetting(Record r) {
			super(r);
		}
		
		public final void setName(String name){
			record.setProperty(SETTING_FIELD, name);
		}
		public final void setPerson(AppUser u){
			record.setProperty(PERSON_FIELD, u.getID());
		}
		public  void setValue(PreferenceSetting<D> pref,D val){
			record.setProperty(VALUE_FIELD, val);
		}
		public abstract  D getValue(PreferenceSetting<D> pref);
	}

	public D getPreference(PreferenceSetting<D> pref){
		S setting = makeSetting(pref,false);
		if( setting == null ){
			return pref.defaultSetting(getContext());
		}
		return setting.getValue(pref);
	}
	public boolean hasPreference(PreferenceSetting<D> pref){
		S setting = makeSetting(pref,false);
		if( setting == null ){
			return false;
		}
		return true;
	}
	
	public void clearPreference(PreferenceSetting<D> pref) throws DataFault{
		S setting = makeSetting(pref,false);
		if( setting != null){
			setting.delete();
		}
	}
	public void setPreference(PreferenceSetting<D> pref, D value){
		S setting = makeSetting(pref,true);

		setting.setValue(pref,value);
		try {
			setting.commit();
		} catch (DataFault e) {
			getContext().error(e,"Error setting value");
		}

	}
	
	
	public S makeSetting(PreferenceSetting<D> pref,boolean create){
		SessionService serv = getContext().getService(SessionService.class);
		if( serv == null || ! serv.haveCurrentUser() || ! PER_USER_SETTINGS_FEATURE.isEnabled(getContext())){
			return null;
		}
		SQLAndFilter<S> fil = getSQLAndFilter(
				new SQLValueFilter<>(res, SETTING_FIELD, pref.getName()),
				new SQLValueFilter<>(res, PERSON_FIELD, serv.getCurrentPerson().getID()));
		try {
			S result = find(fil,true);
			if( result == null ){
				if( create ){
					result = makeBDO();
					result.setName(pref.getName());
					result.setPerson(serv.getCurrentPerson());
					result.setValue(pref,pref.defaultSetting(getContext()));
					result.commit();
				}
			}
			return result;
		} catch (DataException e) {
			getContext().error(e,"Error getting setting");
		}
		return null;
	}
	
	public AbstractUserSettingFactory(AppContext conn,String table){
		setContext(conn, table);
	}
	


	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("SettingID");
		spec.setField(PERSON_FIELD, new IntegerFieldType());
		spec.setField(SETTING_FIELD, new StringFieldType(false, "", 128));
		spec.setField(VALUE_FIELD, getFieldType());
		return spec;
	}

	protected abstract FieldType<T> getFieldType();
}