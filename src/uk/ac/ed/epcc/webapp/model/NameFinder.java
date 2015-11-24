// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.Indexed;
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
	public abstract T makeFromString(String name) throws DataFault;

	/** get a filter than locates the target object from a String.
	 * 
	 * This should use the same logic as {@link #findFromString(String)}
	 * 
	 * @param name
	 * @return
	 */
	public SQLFilter<T> getStringFinderFilter(String name);
	/** get a DataCache for fetching the target
	 * 
	 * @return DataCache
	 */
	public abstract DataCache<String, T> getDataCache();

}