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