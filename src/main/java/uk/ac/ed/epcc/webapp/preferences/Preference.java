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
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.preferences.UserSettingFactory.UserSetting;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link Preference} is a {@link Feature} that only affects presentation logic so a user is allowed to
 * override the system-wide value with their own setting.
 * 
 * This can be dynamically degraded to a non-settable {@link Feature} busing the boolean config parameter
 * <b>service.feature.<i>name</i>.settable</b>
 * @author spb
 *
 */

public class Preference extends Feature implements PreferenceSetting<Boolean>{

	private String[] required_roles=null;
	
	/**
	 * @param name
	 * @param def
	 * @param description
	 */
	public Preference(String name, boolean def, String description) {
		this(name,def,description,null);
	}
	public Preference(String name, boolean def, String description, String ... required_roles) {
		super(name, def, description);
		this.required_roles=required_roles;
	}

	@Override
	public boolean isEnabled(AppContext conn) {
		if( conn == null ){
			return isDef();
		}
		// Preference queries may occur often so cache the result in the context
		Boolean b = (Boolean) conn.getAttribute(this);
		if (b == null) {
			// In case of recursion store default first
			// The preference lookup will then use the global default
			// This allows preferences to be set in low level functions used in the lookup
			// once lookup is complete the user preference will be applied.
			b = new Boolean(defaultSetting(conn));
			conn.setAttribute(this, b);
			if( canView(conn.getService(SessionService.class))){
				UserSettingFactory<UserSetting> fac = new UserSettingFactory<>(conn);
				b = new Boolean(fac.getPreference(this));
				conn.setAttribute(this, b);
			}
		}
		return b.booleanValue();
	}

	public boolean canUserSet(AppContext conn){
		return conn.getBooleanParameter(getTag()+".settable", true);
	}
	@Override
	public Boolean defaultSetting(AppContext conn){
		return getConfigValue(conn);
	}
	
	@Override
	public boolean hasPreference(AppContext conn){
		UserSettingFactory<UserSetting> fac = new UserSettingFactory<>(conn);
		return fac.hasPreference(this);
	}
	
	
	public void clearPreference(AppContext conn) throws DataFault{
		UserSettingFactory<UserSetting> fac = new UserSettingFactory<>(conn);
		fac.clearPreference(this);
		conn.removeAttribute(this);
		
	}
	@Override
	public void setPreference(AppContext conn, Boolean value){
		UserSettingFactory<UserSetting> fac = new UserSettingFactory<>(conn);
		fac.setPreference(this, value);
		conn.removeAttribute(this);
	}
	
	/** Does the current user have permissions to set/view this preference.
	 * 
	 * @param sess
	 * @return
	 */
	public boolean canView(SessionService<?> sess){
		if( canUserSet(sess.getContext())){
			if( required_roles == null || required_roles.length == 0){
				return true;
			}
			return sess.hasRoleFromList(required_roles);
		}
		return false;
	}
	/** checks a {@link Preference} that is only defined by name (ie the name is generated dynamically)
	 * 
	 * @param conn
	 * @param name
	 * @return
	 */
	public static boolean checkDynamicPreference(AppContext conn, String name, boolean def,String desc){
		Feature f = Feature.findFeatureByName(Feature.class,name);
		if( f == null ){
			f= new Preference(name,def,desc);
		}
		return f.isEnabled(conn);
	}
	
}