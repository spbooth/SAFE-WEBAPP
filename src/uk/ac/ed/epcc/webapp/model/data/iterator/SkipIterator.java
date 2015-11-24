// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
/** Wrapper to select a sub-set from the results of an iterator
 * 
 * @author spb
 *
 * @param <E>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SkipIterator.java,v 1.2 2014/09/15 14:30:32 spb Exp $")

public class SkipIterator<E> implements Iterator<E> {

	private final Iterator<E> inner;
	private int count;
	public SkipIterator(Iterator<E> inner,int skip, int count){
		this.inner=inner;
		this.count=count;
		while(skip > 0 && inner.hasNext()){
			skip--;
			inner.next();
		}
	}
	public boolean hasNext() {
		return inner.hasNext() && count > 0;
	}

	public E next() {
		if( count > 0 ){
			count--;
			return inner.next();
		}
		throw new NoSuchElementException();
	}

	public void remove() {
		inner.remove();
	}

}