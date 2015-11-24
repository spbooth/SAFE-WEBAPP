// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.convert;

import uk.ac.ed.epcc.webapp.Targetted;


/** Interface for objects that implement a type conversion
 * from an underlying data representation.
 * e.g. converting an integer from a DB field into a reference.
 * 
 * @author spb
 *
 *  @param <T> Type of object produced.
 * @param <D> Type of underlying Object.
 */
public interface TypeConverter<T, D> extends Targetted<T> {
	/** Find the required object.
	   * 
	   * @param o Value of the database field
	   * @return Target value or null if invalid
	   */
	  public T find(D o);
	  /** Get the underlying data representation corresponding to the value
	   * 
	   * @param value
	   * @return Field value or null if invalid
	   */
	  public D getIndex(T value);
	  
}