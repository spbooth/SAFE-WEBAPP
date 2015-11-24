// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
/** Abstract class for combining a sequence of iterators
 * 
 * @author spb
 *
 * @param <T>
 */
public abstract class AbstractMultiIterator<T> implements Iterator<T> {
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

}