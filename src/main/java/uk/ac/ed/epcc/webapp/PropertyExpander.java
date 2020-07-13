//| Copyright - The University of Edinburgh 2020                            |
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Recursively expand {@link Properties} into text.
 * Parameters are references in the form ${name:fallback-text}
 * @author Stephen Booth
 *
 */
public class PropertyExpander {
	
	/** A regexp for parameter expansion
	 * 
	 */
    private final Pattern expand_pattern = Pattern.compile("\\$\\{([^\\s}]+)\\}");
	/**
	 * @param prop
	 */
	public PropertyExpander(Properties prop) {
		super();
		this.prop = prop;
	}

	private final Properties prop;
	
	/** perform config parameter expansion on a text fragment.
	 * 
	 * Text of the form ${name} is replaced with the corresponding config parameter
	 * A fall-back value can be specified by using ${name:fallback}
	 * 
	 * @param text_to_expand
	 * @return expanded text
	 */
	public String expandText(String text_to_expand) {
		return expandText(new HashMap<String,String>(), text_to_expand);
	}
	/** expand text with override map.
	 * As properties are expanded the override map is populated with the resolved values.
	 * circular-definition expansion will terminate the search
	 * 
	 * @param values
	 * @param text_to_expand
	 * @return
	 */
	String expandText(Map<String,String> values,String text_to_expand) {
			if( text_to_expand == null || text_to_expand.length() == 0){
				return text_to_expand;
			}
			StringBuffer result = new StringBuffer();
			Matcher m = expand_pattern.matcher(text_to_expand);
			while(m.find()){
				String default_text ="";
				String subname = m.group(1);
				if( subname.contains(":")){
					int pos = subname.indexOf(':');
					default_text = subname.substring(pos+1);
					subname=subname.substring(0, pos);
				}
				String text = values.get(subname);
				if( text == null){
					text=getProperty(subname,default_text);
					values.put(subname, ""); // recursive go to empty string
					values.put(subname, text=expandText(values,text));
				}
				// supress unintended back subs
				text = text.replace("\\", "\\\\");
				text = text.replace("$", "\\$");
				m.appendReplacement(result, text);
			}
			m.appendTail(result);
			return result.toString();
	}
	/**
	 * @param subname
	 * @param default_text
	 * @return
	 */
	private String getProperty(String subname, String default_text) {
		if( prop == null) {
			return default_text;
		}
		return prop.getProperty(subname, default_text);
	}
	
	public final String getExpandedProperty(String name,String def){
		if( prop == null) {
			return def;
		}
		String text_to_expand = prop.getProperty(name,def);
		Map<String,String> values = new HashMap<>();
		values.put(name, "");
		return expandText(values,text_to_expand);
	}
	public final String getExpandedProperty(String name) {
		return getExpandedProperty(name, null);
	}
}
