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
package uk.ac.ed.epcc.webapp.model;

import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** and interface for {@link Composite} that support anonymisation.
 * 
 * These should be called from the {@link AnonymisingFactory#anonymise()} method.
 * 
 * They can ALSO be used to erase the data for specific users within a production database for
 * data protection reasons.
 * @author spb
 *
 */
public interface AnonymisingComposite<BDO extends DataObject> {
	public void anonymise(BDO target);
	/** Add to the set of fields that contain PII data
	 * 
	 * This can be used to generate an anonymising rule for history tables
	 * 
	 * @param fields
	 */
	default public void addEraseFields(Set<String> fields) {
		
	}
}
