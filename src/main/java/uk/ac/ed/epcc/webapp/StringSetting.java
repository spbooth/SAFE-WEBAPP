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

import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.SetInput;

/** A {@link StringSetting} represents optional feature in the code. 
 * Normally this should be encapsulated as a singleton constant field so we can
 * locate them by reflection and generate on-line documentation.
 * Each {@link StringSetting} object defines a <em>name</em> for the feature a default setting and descriptive documentation.
 * Some legacy features may be generated dynamically so the same feature may be constructed multiple times but {@link StringSetting}s with
 * the same name should be identical.
 * <p>
 * {@link StringSetting}s can be turned on and off using configuration parameters
 * e.g. <b>service.feature.<em>feature-name</em>=true</b>
 * 
 * The results are also cached in the {@link AppContext} as the same {@link StringSetting} may be queried a large number of times in the same request.
 * 
 * @author spb
 *
 */
public class StringSetting extends AbstractSetting<String> {
	
    private final String default_options[];
	public StringSetting(String name,String description, String ... options) {
		super(name,description,options[0]);
		this.default_options=options;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public String getCurrent(AppContext conn){
		if( conn == null ){
			return getDefault();
		}
		// Feature queries may occur often so cache the result in the context
		String b = (String) conn.getAttribute(this);
		if (b == null) {
			b = getConfigValue(conn);
			conn.setAttribute(this, b);
		}
		return b;
	}










	protected String getConfigValue(AppContext conn) {
		return conn.getInitParameter(getTag(), getDefault());
	}
	
	protected String getTag() {
		return "service.feature." + getName();
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Setting#getInput()
	 */
	@Override
	public ItemInput<String,String> getInput(AppContext conn) {
		SetInput<String> input = new SetInput<String>();
		String option_string = conn.getInitParameter(getTag()+".options");
		String options[];
		if( option_string != null ) {
			options = option_string.trim().split("\\s*,\\s*");
		}else {
			options = default_options;
		}
		for(String s : options) {
			input.addChoice(s);
		}
		return input;
	}
	
	
	@Override
	public String getText(String b) {
		return b;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#find(java.lang.Object)
	 */
	@Override
	public String find(String o) {
		return o;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#getIndex(java.lang.Object)
	 */
	@Override
	public String getIndex(String value) {
		return value;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<String> getTarget() {
		return String.class;
	}

}