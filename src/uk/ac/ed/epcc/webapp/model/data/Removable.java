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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.FilterIterator;
/** Interface for {@link DataObject}s that can be removed from a {@link FilterIterator}
 * Classes that implement this should therefore have a well defined iteration order
 * that won't per permuted if elements from the start of the sequence are removed.
 * 
 * 
 * It is also implemented by the target classes of Log Entry
 * If this interface is implemented the class provides a method to be called when the
 * parent Entry is deleted.
 * 
 * @author spb
 *
 */
public interface Removable {

	/** This method removes the record
	 * 
	 * It should delete any dependent data then call {@link DataObject#delete}
	 * 
	 * @throws DataException
	 */
  public void remove() throws DataException;
}