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
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import uk.ac.ed.epcc.webapp.model.data.CloseableIterator;
/** Abstract class for combining a sequence of iterators.
 * 
 * If the nested iterators implement {@link AutoCloseable} we assume only the current iterator
 * needs closing
 * @see CloseableIterator
 * @author spb
 *
 * @param <T>
 */
public abstract class AbstractMultiIterator<T> implements CloseableIterator<T> {
    private Iterator<T> inner=null;

    /** return the next Iterator in the sequence or null if no more iterators
     * are available
     * 
     * @return Iterator or null
     */
    protected abstract Iterator<T> nextIterator();
    
    /** update the current inner iterator until we have a next value or
     * the sequence is exhausted.
     * 
     */
    private void update(){
    	while( inner == null || ! inner.hasNext()){
    		inner=nextIterator();
    		if( inner == null ){
    			// null result from nextIterator we can stop now
    			return;
    		}
    	}
    }
    public boolean hasNext() {
		update();
		return inner != null && inner.hasNext();
	}

	public T next() {
		update();
		if( inner != null && inner.hasNext()){
			return inner.next();
		}
		throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException(
		"MultiIterator does not support remove");
	}

	@Override
	public void close() throws Exception {
		if( inner instanceof AutoCloseable) {
			((AutoCloseable)inner).close();
		}
		
	}

}