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
import uk.ac.ed.epcc.webapp.Setting;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author Stephen Booth
 *
 */
public interface PreferenceSetting<V> extends Setting<V> {

	public void setPreference(AppContext conn, V value);
	
	public boolean hasPreference(AppContext conn);
	
	public V defaultSetting(AppContext conn);

	/** Does the current user have permissions to set/view this preference.
	 * 
	 * @param sess
	 * @return
	 */
	public default boolean canView(SessionService<?> sess) {
		return true;
	}
	
	public void clearPreference(AppContext conn) throws DataFault;
}
