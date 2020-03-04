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

import java.util.Properties;

import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OnOffInput;

/** A {@link Feature} represents optional feature in the code. 
 * Normally this should be encapsulated as a singleton constant field so we can
 * locate them by reflection and generate on-line documentation.
 * Each {@link Feature} object defines a <em>name</em> for the feature a default setting and descriptive documentation.
 * Some legacy features may be generated dynamically so the same feature may be constructed multiple times but {@link Feature}s with
 * the same name should be identical.
 * <p>
 * {@link Feature}s can be turned on and off using configuration parameters
 * e.g. <b>service.feature.<em>feature-name</em>=true</b>
 * 
 * The results are also cached in the {@link AppContext} as the same {@link Feature} may be queried a large number of times in the same request.
 * 
 * @author spb
 *
 */
public class Feature extends AbstractSetting<Boolean> {
	
	public Feature(String name, boolean def, String description) {
		super(name,description,def);
		
	}
	public Feature(String name){
		this(name,false,null);
	}
	
	
	public final boolean isDef() {
		return getDefault();
	}
	
	
	
	
	
	
	
	public boolean isEnabled(AppContext conn){
		if( conn == null ){
			return isDef();
		}
		// Feature queries may occur often so cache the result in the context
		Boolean b = (Boolean) conn.getAttribute(this);
		if (b == null) {
			b = new Boolean(getConfigValue(conn));
			conn.setAttribute(this, b);
		}
		return b.booleanValue();
	}
	protected boolean getConfigValue(AppContext conn) {
		return conn.getBooleanParameter(getTag(),isDef());
	}
	protected String getTag() {
		return "service.feature." + getName();
	}
	
	/** query a property bundle not the {@link AppContext}. The result is NOT cached.
	 * 
	 * @param prop
	 * @return boolean
	 */
	public boolean isEnabled(Properties prop){
		String tag = getTag();
		return AppContext.parseBooleanParam(prop.getProperty(tag), isDef());
	}
	
	/** Test a feature by name.
	 * 
	 * This is needed for testing pre-requisite features specified in a config file
	 * but will always return false until the class that actually defines the feature is loaded. 
	 * 
	 * @param conn
	 * @param name
	 * @return
	 */
	public static boolean checkFeature(AppContext conn, String name){
		Feature f = findFeatureByName(Feature.class,name);
		if( f == null ){
			return false;
		}
		return f.isEnabled(conn);
	}
	
	/** checks a feature that is only defined by name (ie the name is generated dynamically)
	 * 
	 * @param conn
	 * @param name
	 * @return
	 */
	public static boolean checkDynamicFeature(AppContext conn, String name, boolean def){
		Feature f = findFeatureByName(Feature.class,name);
		if( f == null ){
			f= new Feature(name,def,"dynamic feature");
		}
		return f.isEnabled(conn);
	}

	public static void setTempFeature(AppContext conn, Feature f, boolean val) {
		conn.setAttribute(f, Boolean.valueOf(val));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Setting#getInput()
	 */
	@Override
	public ItemInput<String,Boolean> getInput() {
		return new OnOffInput();
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Setting#getCurrent(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public Boolean getCurrent(AppContext conn) {
		return isEnabled(conn);
	}
	public String getText(Boolean b) {
		if( b.booleanValue()) {
			return "On";
		}
		return "Off";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#find(java.lang.Object)
	 */
	@Override
	public Boolean find(String o) {
		return Boolean.valueOf(o);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter#getIndex(java.lang.Object)
	 */
	@Override
	public String getIndex(Boolean value) {
		return value.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<Boolean> getTarget() {
		return Boolean.class;
	}
}