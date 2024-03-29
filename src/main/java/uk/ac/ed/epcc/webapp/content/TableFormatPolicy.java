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
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;

/** Policy object to map a {@link Table} to a {@link SimpleXMLBuilder}
 * 
 * assumes a standard constructor signature with arguments
 * ( {@link SimpleXMLBuilder}, {@link NumberFormat} )
 * @author spb
 *
 * @param <C>
 * @param <R>
 */

public interface TableFormatPolicy<C, R> {

	public abstract void add(Table<C, R> t);

	public abstract void addColumn(Table<C, R> t, C key);
	
	 public abstract void setAllowSpan(boolean value);

}