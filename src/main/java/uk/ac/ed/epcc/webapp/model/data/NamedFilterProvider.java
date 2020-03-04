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

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;

/** Interface for classes (Normally {@link DataObjectFactory}s or {@link Composite}s) that can generate named filters.
 * 
 * This is intended as a standard mechanism to give access to standard un-parameterised filters
 * (that would normally be generated by methods) 
 * in text driven code.
 * 
 * It is primarily used to dynamically define relationships that should only apply when the target object is
 * in a particular state.  
 * 
 * @see AccessRoleProvider
 * @author spb
 *
 */
public interface NamedFilterProvider<T> {

	/** Lookup a filter by name
	 * 
	 * An unrecognised filter always returns null
	 * 
	 * @param name
	 * @return {@link BaseFilter} or null
	 */
	public BaseFilter<T> getNamedFilter(String name);
	
	/** Add names of all the directly supported filters.
	 * 
	 * This does not include any qualified name filters referenced through a {@link NamedFilterWrapper}. 
	 * Normally used to generate per-class online documentation or to build interactive search forms.
	 * 
	 * @param names
	 */
	public void addFilterNames(Set<String> names);
}
