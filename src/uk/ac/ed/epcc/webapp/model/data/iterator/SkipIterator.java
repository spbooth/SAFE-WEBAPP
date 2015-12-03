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
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
/** Wrapper to select a sub-set from the results of an iterator
 * 
 * @author spb
 *
 * @param <E>
 */


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