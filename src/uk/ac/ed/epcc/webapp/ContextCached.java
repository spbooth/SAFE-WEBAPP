// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;
/** Marker interface for objects where only a single instance should exist 
 * per AppContext.
 * 
 * Objects that implement this interface will be cached in the AppContext and
 * the same instance will be returned for each equivalent call to makeObject
 * 
 * 
 * 
 * @author spb
 *
 */
public interface ContextCached {

}