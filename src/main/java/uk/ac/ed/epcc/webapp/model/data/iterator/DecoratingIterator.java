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
 * Created on 08-Feb-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import uk.ac.ed.epcc.webapp.model.data.CloseableIterator;

/**
 * DecoratingIterator Base class for a Decorator applied to an Iterator. Create
 * subclasses that overide the next and/or accept method to modify the returned
 * object.
 * 
 * @author spb
 * @param <R> Result typeof iterator
 * @param <S> a superclass of incoming iterator
 * 
 */
public abstract class DecoratingIterator<R,S> implements CloseableIterator<R> {
	private Iterator<? extends S> it;

	private S n = null;

	/**
	 * @param i
	 * 
	 */
	public DecoratingIterator(Iterator< ? extends S> i) {
		it = i;
	}

	/**
	 * Do we want this object in the output stream Override to change behavour.
	 * This method will only be called once per object in the stream
	 * 
	 * @param o
	 * @return boolean
	 */
	protected boolean accept(S o) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public final boolean hasNext() {
		if (n != null) {
			return true;
		}
		while (it.hasNext()) {
			n = it.next();
			if (accept(n)) {
				return true;
			}
		}
		n = null;
		return false;
	}

	
	@SuppressWarnings("unchecked")
	public R next() {
		// default implementation for when R is the same as S
		return (R) nextInput();
	}
	/**
	 * Method for superclasses to get the next object they plan to override.
	 * 
	 * @return Object
	 */
	protected final S nextInput(){
		if (hasNext()) {
			S ret = n;
			n = null;
			return ret;
		}
		throw new NoSuchElementException("DecoratedIterator exhausted");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public final void remove() {
		throw new UnsupportedOperationException(
				"DecoratedIterator does not support remove");

	}

	@Override
	public void close() throws Exception {
		if( it instanceof AutoCloseable) {
			((AutoCloseable)it).close();
		}
		
	}

}