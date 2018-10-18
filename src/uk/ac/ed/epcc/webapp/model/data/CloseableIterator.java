//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.model.data.iterator.AbstractMultiIterator;

/** A combination of {@link AutoCloseable} and {@link Iterator}
 * 
 * The contract of this interface is that the the {@link #close()} should be invokable
 * multiple times without additional error.
 * If the {@link Iterator} is run to completion than the object should automatically close. An additional
 * call to close should only be required for an incomplete iterator (for example if an exception is thrown).
 * @see AbstractMultiIterator
 * 
 * @author Stephen Booth
 * @param <E> type of iterator
 *
 */
public interface CloseableIterator<E> extends Iterator<E>, AutoCloseable {

}
