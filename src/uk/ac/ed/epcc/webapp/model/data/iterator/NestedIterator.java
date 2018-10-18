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
/*
 * Created on 23-Aug-2004
 *
 */
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.model.data.CloseableIterator;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * Wrapper that iterates over multiple iterators in turn.
 * 
 * @author spb
 * @param <T> Type of iterator returned
 * 
 */


public class NestedIterator<T> implements CloseableIterator<T> {
	Vector<Iterator<? extends T>> list;

	int count = 0;

	/**
	 * 
	 */
	public NestedIterator() {
		list = new Vector<Iterator<? extends T>>();
		count = 0;
	}

	public void add(Iterator<? extends T> i) {
		list.add(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		for (;;) {
			if (count >= list.size()) {
				return false;
			}
			if (((Iterator) list.get(count)).hasNext()) {
				return true;
			} else {
				count++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public T next() {
		if (hasNext()) {
			return list.get(count).next();
		} else {
			throw new NoSuchElementException("NestedIterator exhausted");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		// tricky to do right as may have switched underlying iterator since
		// last call
		throw new UnsupportedOperationException(
				"NestedIterator does not support remove");

	}

	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DataFault f=null;
		for(Iterator it : list) {
			if( it instanceof AutoCloseable) {
				try {
					((AutoCloseable)it).close();
				}catch(Exception e) {
					if( f == null ) {
						f = new DataFault("Close exception", e);
					}else {
						f.addSuppressed(e);
					}
				}
			}
		}
		list.clear();
		list=null;
		if( f != null ) {
			throw f;
		}
	}


}