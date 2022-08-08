//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataCache;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** Interface for factory classes that can look-up or create an entry by name.
 * This extends {@link ParseFactory} but is specific to {@link DataObject}s rather than general {@link Indexed}s.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface NameFinder<T extends DataObject> extends ParseFactory<T> {
	
	/** Check formatting constraints on the  input string.
	 * 
	 * @param name
	 * @throws ParseException
	 */
	public abstract void validateNameFormat(String name) throws ParseException;
    /** Find an existing entry by name
     * 
     * This should use the same logic as {@link #getStringFinderFilter(String)}.
     * 
     * @param name
     * @return Matching T or null
     */
	public abstract T findFromString(String name);

	/** Same as {@link #findFromString(String)} but attempts to create a matching entry if one does not exist and this
	 * is supported by the implementing class.
	 * 
	 * @param name
	 * @return T or null
	 * @throws DataFault 
	 */
	public abstract T makeFromString(String name) throws DataFault, ParseException;
	
	/** Can this class make new values via the {@link #makeFromString(String)} method
	 * 
	 * @return
	 */
	public default boolean canMakeFromString() {
		return true;
	}

	/** get a filter than locates the target object from a String.
	 * 
	 * This should use the same logic as {@link #findFromString(String)}
	 * 
	 * @param name
	 * @return
	 */
	public SQLFilter<T> getStringFinderFilter(String name);
	/** Get a {@link BaseFilter} for objects that have a non-null 
	 * canonical name
	 * 
	 * @return
	 */
	public SQLFilter<T> hasCanonicalNameFilter();
	
	/** get a DataCache for fetching the target
	 * 
	 * @return DataCache
	 */
	public abstract DataCache<String, T> getDataCache(boolean auto_create);

}