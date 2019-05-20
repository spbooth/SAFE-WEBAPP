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
package uk.ac.ed.epcc.webapp.messages;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/** A {@link ResourceBundle} implemented as a {@link Properties} collection.
 * @author spb
 *
 */

public class PropResourceBundle extends ResourceBundle {

	private final Properties props;
	
	/**
	 * 
	 */
	public PropResourceBundle(Properties p) {
		this.props=p;
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#getKeys()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getKeys() {
		return props.keys();
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	@Override
	protected Object handleGetObject(String key) {
		return props.getProperty(key);
	}

}