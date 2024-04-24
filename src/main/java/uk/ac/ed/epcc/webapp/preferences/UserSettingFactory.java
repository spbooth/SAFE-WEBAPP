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
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A class to hold user {@link Preference} settings.
 * @author spb
 * @param <S> Type of Setting object
 *
 */

public class UserSettingFactory<S extends UserSettingFactory.UserSetting> extends AbstractUserSettingFactory<Boolean, Boolean, S> {

	public static final String DEFAULT_TABLE = "UserSettings";


	public static class UserSetting extends AbstractUserSettingFactory.UserSetting<Boolean>{

		/**
		 * @param r
		 */
		protected UserSetting(Record r) {
			super(r);
		}
		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.preferences.AbstractUserSettingFactory.UserSetting#getValue(uk.ac.ed.epcc.webapp.preferences.PreferenceSetting)
		 */
		@Override
		public Boolean getValue(PreferenceSetting<Boolean> pref) {
			return record.getBooleanProperty(VALUE_FIELD);
		}
	}

	public static UserSettingFactory getFactory(AppContext conn) {
		return conn.makeObject(UserSettingFactory.class, DEFAULT_TABLE);
	}
	public UserSettingFactory(AppContext conn){
		super(conn, DEFAULT_TABLE);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected S makeBDO(Record res) throws DataFault {
		return (S) new UserSetting(res);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.preferences.AbstractUserSettingFactory#getFieldType()
	 */
	@Override
	protected FieldType<Boolean> getFieldType() {
		return new BooleanFieldType(false,false);
	}

	
}