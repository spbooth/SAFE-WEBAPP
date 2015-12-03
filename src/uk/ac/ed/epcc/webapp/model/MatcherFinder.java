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
package uk.ac.ed.epcc.webapp.model;

import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Similar to {@link NameFinder} this interface supports a 
 * many-to-one name lookup. Where {@linkNameFinder} is intended to lookup
 * an object by name. This is intended to looup an owning object by the name of
 * a client object.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface MatcherFinder<T extends DataObject & Matcher> {
	public abstract T findOwner(String clientName);
	public Set<T> getOwners();
}