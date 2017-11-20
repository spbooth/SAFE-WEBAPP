//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.model;

import java.util.Map;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Interface for providing machine readable MetaData
 * 
 * This is usually implemented by a {@link DataObjectFactory} or {@link Composite}
 * and is intended to build a {@link Map} of attributes that provide meta-data information about
 * the target object.
 * These will usually be added to a XML or JSON document describing the target
 * 
 * The keys should be valid element/key names.
 * 
 * @see SummaryContributer
 * @author spb
 *
 */
public interface MetaDataContributer<T extends DataObject> {

	/** Add to the set of meta-data.
	 * 
	 * @param attributes
	 * @param target
	 */
	public void addMetaData(Map<String,String> attributes,T target);
}
