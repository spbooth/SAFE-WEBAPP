//| Copyright - The University of Edinburgh 2014                            |
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



/** Simple interface for factories that can find objects by name.
 * 
 * In this context the name is the default short identifying string for the object.
 * 
 * Each valid name must uniquely map to a single result. 
 * 
 * It is permissible for multiple names to result in the same object but only 
 * one of them will be returned by {@link #getCanonicalName(Object)}. The equivalent names should therefore come from
 * different pattern spaces and either be mappings between different representations of the same name or correspond to
 * different unique fields on the target object.
 * 
 * @see NameFinder
 * @author spb
 * @param <T> Type of object to be found
 *
 */

public interface ParseFactory<T> {

	/** Find an existing entry by name.
     * 
     * 
     * @param name
     * @return Matching T or null
     */
	public abstract T findFromString(String name);
	
	
	/** Get a canonical name of an object in a format that can be parsed by the {@link #findFromString(String)} method.
	 * 
	 * @param object
	 * @return The canonical name.
	 */
	public String getCanonicalName(T object);

}