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


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.content.Table;

/**
 * @author Stephen Booth
 *
 */
public abstract class AbstractSetting<V> implements Setting<V> {
	private static Set<AbstractSetting> known_features = Collections.synchronizedSet( new HashSet<AbstractSetting>());
	private static Map<String,AbstractSetting> previous = Collections.synchronizedMap(new HashMap<String, AbstractSetting>());
	/** Return a set of known {@link Feature}s.
	 * 
	 * Early in application life-time this list may be incomplete as the declaring class might not be loaded yet.
	 * 
	 * @return Set.
	 */
	public static Set<AbstractSetting> getKnownFeatures(){
		synchronized (known_features) {
			return new HashSet<>(known_features);
		}
		
	}
	/**
	 * @param name
	 * @param description
	 */
	public AbstractSetting(String name, String description, V def) {
		super();
		this.name = name;
		this.description = description;
		this.def=def;
		synchronized (known_features) {


			if( ! known_features.contains(this)){
				known_features.add(this);
				previous.put(name, this);
			}else{
				// Try do detect duplicate named features.
				AbstractSetting prev=previous.get(name);
				//assert(prev == null || (description==null && prev.getDescription()==null) || (description !=null && description.equals(prev.getDescription()))) : name+":description";
				assert(prev==null || def.equals(prev.getDefault())): name+":def";
			}
		}
	}
	
	private final String name;
	
	private final V def;
	
	private final String description;
	public final String getName() {
		return name;
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
		AbstractSetting other = (AbstractSetting) obj;
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
		Table<String, AbstractSetting> t = new Table();
		// Use getKnownFeatures. This copies the list so
		// avoids any concurrent modifications if the
		// loop code queries a new one.
		for(AbstractSetting f : getKnownFeatures() ){
			t.put("Name", f, f.getName());
			t.put("Description",f,f.getDescription());
			Object d = f.getDefault();
			t.put("Default setting",f,d);
			Object current = f.getCurrent(c);
			t.put("Current Setting",f,current);
			t.setHighlight(f, ! d.equals(current));
		}

		t.sortRows();
		return t;
	}
	
	
	/** Locate a {@link Feature} by name
	 * 
	 * This is needed for transitions that operate on {@link Feature}s. However it will only
	 * return a {@link Feature} once the class that actually defines it is loaded.
	 * @param name
	 * @return Feature or null.
	 */
	public static <X extends AbstractSetting> X findFeatureByName(Class<X> clazz,String name){
		AbstractSetting s = previous.get(name);
		if( s == null || clazz.isAssignableFrom(s.getClass())) {
			return (X) s;
		}
		return null;
	}
	public V getDefault() {
		return def;
	}
	public String getText(V value) {
		return value.toString();
	}
}
