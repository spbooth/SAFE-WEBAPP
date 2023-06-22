//| Copyright - The University of Edinburgh 2019                            |
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
import uk.ac.ed.epcc.webapp.StringSetting;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;



/** An {@link String} valued {@link PreferenceSetting}
 * @author Stephen Booth
 *
 */
public class StringPreference extends StringSetting implements PreferenceSetting<String> {

	
	public StringPreference(String name, String description, String ... options) {
		super(name, description, options);
	}

	@Override
	public String getCurrent(AppContext conn) {
		if( conn == null ){
			return getDefault();
		}
		// Preference queries may occur often so cache the result in the context
		String b = (String) conn.getAttribute(this);
		if (b == null) {
			if( canUserSet(conn)){
				UserStringSettingFactory<String,UserStringSettingFactory.UserSetting<String>> fac = new UserStringSettingFactory<>(conn);
				b = fac.getPreference(this);
			}else{
				b = defaultSetting(conn);
			}
			conn.setAttribute(this, b);
		}
		return b;
	}

	public boolean canUserSet(AppContext conn){
		return conn.getBooleanParameter(getTag()+".settable", true);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.preferences.PreferenceSetting#setPreference(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object)
	 */
	@Override
	public void setPreference(AppContext conn, String value) {
		UserStringSettingFactory<String,UserStringSettingFactory.UserSetting<String>> fac = new UserStringSettingFactory<>(conn);
		fac.setPreference(this, value);
		conn.removeAttribute(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.preferences.PreferenceSetting#hasPreference(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public boolean hasPreference(AppContext conn) {
		UserStringSettingFactory<String,UserStringSettingFactory.UserSetting<String>> fac = new UserStringSettingFactory<>(conn);
		return fac.hasPreference(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.preferences.PreferenceSetting#defaultSetting(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public String defaultSetting(AppContext conn) {
		return getConfigValue(conn);
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.preferences.PreferenceSetting#clearPreference(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public void clearPreference(AppContext conn) throws DataFault {
		UserStringSettingFactory<String,UserStringSettingFactory.UserSetting<String>> fac = new UserStringSettingFactory<>(conn);
		fac.clearPreference(this);
		conn.removeAttribute(this);
	}


}
