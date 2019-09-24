//| Copyright - The University of Edinburgh 2016                            |
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

import uk.ac.ed.epcc.webapp.AbstractSetting;
import uk.ac.ed.epcc.webapp.PreferenceSetting;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Permitted actions on Preferences.
 * @author spb
 *
 */
public enum PreferenceAction {

  SET_SYSTEM_DEFAULT(){

	@Override
	public boolean allow(SessionService sess, AbstractSetting f) {
		return f != null && sess != null && sess.hasRole(SET_FEATURES_ROLE);
	}

	@Override
	public String getHelp() {
		return "Set the global feature setting for this system";
	}
  },
  SET_PREFERENCE(){

		@Override
		public boolean allow(SessionService sess, AbstractSetting f) {
			return f!= null && 
					sess != null && sess.haveCurrentUser() && 
					f instanceof PreferenceSetting &&
					UserSettingFactory.PER_USER_SETTINGS_FEATURE.isEnabled(sess.getContext());
		}

		@Override
		public String getHelp() {
			return "Set/change your preferred setting.";
		}
		  
	  },
  CLEAR_PREFERENCE(){

		@Override
		public boolean allow(SessionService sess, AbstractSetting f) {
			return f!= null && sess != null && sess.haveCurrentUser() && f instanceof PreferenceSetting &&
					((PreferenceSetting)f).hasPreference(sess.getContext()) &&
					UserSettingFactory.PER_USER_SETTINGS_FEATURE.isEnabled(sess.getContext());
		}

		@Override
		public String getHelp() {
			return "Revert to using the system default setting.";
		}
		  
	  },
  LIST(){

	@Override
	public boolean allow(SessionService sess, AbstractSetting f) {
		return f == null && sess != null && sess.haveCurrentUser();
	}

	@Override
	public String getHelp() {
		return "List preferences";
	}
		  
	  }
  ;
  /**
	 * 
	 */
	public static final String SET_FEATURES_ROLE = "SetFeatures";
public abstract boolean allow(SessionService sess, AbstractSetting f);
  public abstract String getHelp();
}
