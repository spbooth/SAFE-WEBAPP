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

import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Interface for providing summary information.
 * 
 * This is usually implemented by a {@link DataObjectFactory} or {@link Composite}
 * and is intended to build a {@link Map} of attributes that provide summary information about
 * the target object.
 * These will usually be added to a {@link Table} so the data should be of a
 * type that can be displayed by {@link Table}s
 * 
 * The keys should be display text.
 * 
 * @see MetaDataContributer
 * @author spb
 *
 */
public interface SummaryContributer<T extends DataObject> {

	/** Add to the set of attributes.
	 * 
	 * @param attributes
	 * @param target
	 */
	public void addAttributes(Map<String,Object> attributes,T target);
}
