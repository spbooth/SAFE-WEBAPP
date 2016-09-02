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
package uk.ac.ed.epcc.webapp.messages;

import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;

/** A subclass of {@link Properties} that performs {@link AppContext} text expansion when properties are set.
 * 
 * This has to expand on property set to ensure that all access methods (including iterating over Entry values) see the expanded text
 * @author spb
 *
 */
public class SubstitutionProperties extends Properties {
	

	/* (non-Javadoc)
	 * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized Object put(Object key, Object value) {
		if( value != null && value instanceof String){
			return super.put(key, conn.expandText((String)value));
		}
		return super.put(key, value);
	}

	

	private final AppContext conn;

	/**
	 * 
	 */
	public SubstitutionProperties(AppContext conn) {
		super();
		this.conn=conn;
	}

	/**
	 * @param arg0
	 */
	public SubstitutionProperties(AppContext conn,Properties arg0) {
		super(arg0);
		this.conn=conn;
	}

}
