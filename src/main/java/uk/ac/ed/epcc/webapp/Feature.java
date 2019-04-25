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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import uk.ac.ed.epcc.webapp.content.Table;

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
public class Feature implements Comparable<Feature>{
	private static Set<Feature> known_features = Collections.synchronizedSet( new HashSet<Feature>());
	private static Map<String,Feature> previous = Collections.synchronizedMap(new HashMap<String, Feature>());
	/** Return a set of known {@link Feature}s.
	 * 
	 * Early in application life-time this list may be incomplete as the declaring class might not be loaded yet.
	 * 
	 * @return Set.
	 */
	public static Set<Feature> getKnownFeatures(){
		synchronized (known_features) {
			return new HashSet<>(known_features);
		}
		
	}
	public Feature(String name, boolean def, String description) {
		super();
		this.name = name;
		this.def = def;
		this.description = description;
		synchronized (known_features) {


			if( ! known_features.contains(this)){
				known_features.add(this);
				previous.put(name, this);
			}else{
				// Try do detect duplicate named features.
				Feature prev=previous.get(name);
				//assert(prev == null || (description==null && prev.getDescription()==null) || (description !=null && description.equals(prev.getDescription()))) : name+":description";
				assert(prev==null || def == prev.isDef()): name+":def";
			}
		}
	}
	public Feature(String name){
		this(name,false,null);
	}
	private final String name;
	private final boolean def;
	private final String description;
	public final String getName() {
		return name;
	}
	public final boolean isDef() {
		return def;
	}
	public final String getDescription() {
		return description;
	}
	@Override
	public final int hashCode() {
		return name.hashCode();
	}
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public final String toString(){
		return name;
	}
	public static Table getFeatureTable(AppContext c){
		Table<String, Feature> t = new Table();
		// Use getKnownFeatures. This copies the list so
		// avoids any concurrent modifications if the
		// loop code queries a new one.
		for(Feature f : getKnownFeatures() ){
			t.put("Name", f, f.getName());
			t.put("Description",f,f.getDescription());
			t.put("Default setting",f,f.isDef());
			t.put("Current Setting",f,f.isEnabled(c));
			t.setHighlight(f, f.isEnabled(c) != f.isDef());
		}

		t.sortRows();
		return t;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public final int compareTo(Feature o) {
		return name.compareTo(o.name);
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
		Feature f = previous.get(name);
		if( f == null ){
			return false;
		}
		return f.isEnabled(conn);
	}
	/** Locate a {@link Feature} by name
	 * 
	 * This is needed for transitions that operate on {@link Feature}s. However it will only
	 * return a {@link Feature} once the class that actually defines it is loaded.
	 * @param name
	 * @return Feature or null.
	 */
	public static Feature findFeatureByName(String name){
		return previous.get(name);
	}
	/** checks a feature that is only defined by name (ie the name is generated dynamically)
	 * 
	 * @param conn
	 * @param name
	 * @return
	 */
	public static boolean checkDynamicFeature(AppContext conn, String name, boolean def){
		Feature f = previous.get(name);
		if( f == null ){
			f= new Feature(name,def,"dynamic feature");
		}
		return f.isEnabled(conn);
	}
}