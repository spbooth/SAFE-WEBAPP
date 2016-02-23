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
package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.Set;

import uk.ac.ed.epcc.webapp.Tagged;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.AppUser;
/** Interface for stand-alone classes that provide relationship roles between {@link AppUser}s
 * and a target object.
 * These can be constructed dynamically.
 * @author spb
 *
 * @param <A> AppUser type
 * @param <B> Target type
 */
public interface RelationshipProvider<A extends AppUser, B extends DataObject> extends AccessRoleProvider<A,B>, Tagged{
	
	/** Get the set of relationships supported by this type.
	 * 
	 * @return Set
	 */
	public abstract Set<String> getRelationships();

	/** Get the factory for the target object.
	 * 
	 * @return {@link DataObjectFactory}
	 */
	public abstract DataObjectFactory<B> getTargetFactory();
}