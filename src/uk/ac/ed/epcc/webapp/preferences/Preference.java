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
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.preferences.UserSettingFactory.UserSetting;

/** A {@link Preference} is a {@link Feature} that only affects presentation logic so a user is allowed to
 * override the system-wide value with their own setting.
 * @author spb
 *
 */

public class Preference extends Feature {

	/**
	 * @param name
	 * @param def
	 * @param description
	 */
	public Preference(String name, boolean def, String description) {
		super(name, def, description);
	}

	@Override
	public boolean isEnabled(AppContext conn) {
		// Preference queries may occur often so cache the result in the context
		Boolean b = (Boolean) conn.getAttribute(this);
		if (b == null) {
			UserSettingFactory<UserSetting> fac = new UserSettingFactory<UserSetting>(conn);
			b = new Boolean(fac.getPreference(this));
			conn.setAttribute(this, b);
		}
		return b.booleanValue();
	}

	public boolean defaultSetting(AppContext conn){
		return getConfigValue(conn);
	}
	
	public boolean hasPreference(AppContext conn){
		UserSettingFactory<UserSetting> fac = new UserSettingFactory<UserSetting>(conn);
		return fac.hasPreference(this);
	}
	
	
	public void clearPreference(AppContext conn) throws DataFault{
		UserSettingFactory<UserSetting> fac = new UserSettingFactory<UserSetting>(conn);
		fac.clearPreference(this);
		conn.removeAttribute(this);
		
	}
	public void setPreference(AppContext conn, boolean value){
		UserSettingFactory<UserSetting> fac = new UserSettingFactory<UserSetting>(conn);
		fac.setPreference(this, value);
		conn.removeAttribute(this);
	}
	
}