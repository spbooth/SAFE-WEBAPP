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
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter;

/** A class to hold user {@link PreferenceSetting} settings as strings.
 * 
 * This uses the {@link TypeConverter} methods in the {@link PreferenceSetting} to generate the database values so any type of
 * {@link PreferenceSetting} can be stored in the same table. For data types that can be stored natively in the database
 * a custom {@link AbstractUserSettingFactory} might be more appropriate.
 * 
 * @see UserSettingFactory
 * @author spb
 * @param <D> target type of the {@link PreferenceSetting}
 * @param <S> Type of Setting object
 *
 */

public class UserStringSettingFactory<D,S extends UserStringSettingFactory.UserSetting<D>> extends AbstractUserSettingFactory<D,String, S> {

	public static class UserSetting<D> extends AbstractUserSettingFactory.UserSetting<D>{

		/**
		 * @param r
		 */
		protected UserSetting(Record r) {
			super(r);
		}
		
		public String getValue(){
			return record.getStringProperty(VALUE_FIELD);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.preferences.AbstractUserSettingFactory.UserSetting#getValue(uk.ac.ed.epcc.webapp.preferences.PreferenceSetting)
		 */
		@Override
		public D getValue(PreferenceSetting<D> pref) {
			return pref.find(record.getStringProperty(VALUE_FIELD));
		}

		@Override
		public void setValue(PreferenceSetting<D> pref, D val) {
			record.setProperty(VALUE_FIELD, pref.getIndex(val));
		}
	}

	
	public UserStringSettingFactory(AppContext conn){
		super(conn, "UserStringSettings");
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
		return (Class) UserSetting.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.preferences.AbstractUserSettingFactory#getFieldType()
	 */
	@Override
	protected FieldType<String> getFieldType() {
		return new StringFieldType(false, null, 256);
	}

	
}