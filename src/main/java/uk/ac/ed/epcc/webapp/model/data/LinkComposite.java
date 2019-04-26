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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;

/** An interface for {@link Composite}s that wish
 * to take part in a {@link IndexedLinkManager.Link#setup()} operation
 * 
 * The implementing Link class must explicitly look for composites that implement 
 * this interface in its {@link IndexedLinkManager.Link#setup()} method.
 * 
 * @author spb
 * @param <L> 
 *
 */
public interface LinkComposite<L extends IndexedLinkManager.Link> {

	public void setup(L target);
	
	/** Add Tracking fields to the history table default specification
	 * 
	 * @param spec
	 */
	public void modifyHistoryTable(TableSpecification spec);
}
