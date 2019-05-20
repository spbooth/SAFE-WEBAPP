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
package uk.ac.ed.epcc.webapp.model.data.convert;

import java.util.Iterator;
import java.util.Set;


public interface EnumeratingTypeConverter<T,D> extends TypeConverter<T, D> {
	 /** Get an iterator over all supported targets.
	   * 
	   * @return Iterator
	   */
	  public Iterator<T> getValues();
	  /** Add all supported targets to a set 
	   * 
	   * @param <X>
	   * @param set Set to be modified
	   * @return reference to set
	   */
	  public <X extends Set<T>> X getValues(X set);
}