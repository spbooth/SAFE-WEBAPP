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
package uk.ac.ed.epcc.webapp;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter;

/** A {@link Setting} is a configuration parameter with a fixed set of valid values and encapsulted in a model object
 * (usually a singleton)
 * 
 * This can either be a configuration parameter that changes the or a user preference of some sort.
 * These can always be set as values in the config but user preferences may also be overidden 
 * by database settings in on a per-user basis.
 * 
 *  The {@link Setting} interface is primarily used by the user preference transition but this can also be used by
 *  an admin to change  system parameters.
 * 
 * @author Stephen Booth
 * @param <V> type of Setting
 *
 */
public interface Setting<V> extends Comparable<Setting>, TypeConverter<V, String>{

	/** Name of the setting.
	 * Other than a type-specific prefix this is also the name of the configuration parameter corresponding to the setting
	 * 
	 * @return
	 */
	public String getName();
	
	
	/**Descriptive text of the purpose/meaning of the setting.
	 * 
	 * @return
	 */
	public String getDescription();
	
	/** and {@link Input} to select the setting
	 * 
	 * @return
	 */
	public ItemInput<String,V> getInput();


	@Override
	default int compareTo(Setting o) {
		return getName().compareTo(o.getName());
	}
	
	/** get the default (compiled) value of the setting.
	 * 
	 * @return
	 */
	public V getDefault();
	 
	/** get the current value of the setting
	 * @param conn AppContext
	 * 
	 * @return
	 */
	public V getCurrent(AppContext conn);
}
