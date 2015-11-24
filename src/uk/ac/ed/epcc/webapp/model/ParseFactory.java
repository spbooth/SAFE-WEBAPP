// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.Version;

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
@Version("$Revision: 1.3 $")
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
