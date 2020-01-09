//| Copyright - The University of Edinburgh 2013                            |
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

import uk.ac.ed.epcc.webapp.forms.inputs.EnumInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;

/** A {@link EnumSetting} represents optional feature in the code. 
 * Normally this should be encapsulated as a singleton constant field so we can
 * locate them by reflection and generate on-line documentation.
 * Each {@link EnumSetting} object defines a <em>name</em> for the feature a default setting and descriptive documentation.
 * Some legacy features may be generated dynamically so the same feature may be constructed multiple times but {@link EnumSetting}s with
 * the same name should be identical.
 * <p>
 * {@link EnumSetting}s can be turned on and off using configuration parameters
 * e.g. <b>service.feature.<em>feature-name</em>=true</b>
 * 
 * The results are also cached in the {@link AppContext} as the same {@link EnumSetting} may be queried a large number of times in the same request.
 * 
 * @author spb
 *
 */
public class EnumSetting<E extends Enum> extends AbstractSetting<E> {
	
	protected final Class<E> clazz;
	public EnumSetting(Class<E> clazz,String name,E  def, String description) {
		super(name,description,def);
		this.clazz=clazz;
		
	}
	
	
	
	
	
	
	
	
	
	
	public E getCurrent(AppContext conn){
		if( conn == null ){
			return getDefault();
		}
		// Feature queries may occur often so cache the result in the context
		E b = (E) conn.getAttribute(this);
		if (b == null) {
			b = getConfigValue(conn);
			conn.setAttribute(this, b);
		}
		return b;
	}










	protected E getConfigValue(AppContext conn) {
		return conn.getEnumParameter(clazz,getTag(), getDefault());
	}
	
	protected String getTag() {
		return "service.feature." + getName();
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Setting#getInput()
	 */
	@Override
	public ItemInput<String,E> getInput() {
		return new EnumInput<>(clazz);
	}
	
	
	public String getText(E b) {
		return b.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#find(java.lang.Object)
	 */
	@Override
	public E find(String o) {
		return (E) Enum.valueOf(clazz, o);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#getIndex(java.lang.Object)
	 */
	@Override
	public String getIndex(E value) {
		return value.name();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<E> getTarget() {
		return clazz;
	}

}