//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Abstract factory for {@link PartOwner}s
 * @author spb
 *
 */

public abstract class PartOwnerFactory<O extends PartOwner> extends DataObjectFactory<O> {

	/** Get the {@link PartManager} for the next level of 
	 * the hierarchy. This always returns null for
	 * the bottom layer.
	 * 
	 * @return {@link PartManager} or null
	 */
	public abstract PartManager getChildManager();
}