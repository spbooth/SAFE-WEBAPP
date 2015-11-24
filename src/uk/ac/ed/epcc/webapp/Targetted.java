// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;
/** Object with a target type that can be queried at run-time
 * 
 * @author spb
 *
 * @param <T>
 */
public interface Targetted<T> {
	 /** Get the type of the returned object as far as it is known.
	   * This method is used for run-time type checking
	   * The result objects will always be assignable to the type returned by
	   * this method.
	   * 
	   * @return Class object for return type
	   */
	  public Class<? super T> getTarget();
}