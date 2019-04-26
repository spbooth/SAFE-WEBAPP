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

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Iterate over the unique elements fom one iterator in a specified order.
 * unfortunatly this will result in all the elements being stores in a
 * Collection first.
 * 
 * @author spb
 * @param <T> type of iterator
 * 
 */


public class SortingIterator<T> implements Iterator<T> {
	private TreeSet<T> set;

	Iterator<T> internal;

	public SortingIterator(Iterator<T> i, Comparator<? super T> c) {
		set = new TreeSet<>(c);
		while (i.hasNext()) {
			set.add(i.next());
		}
		internal = set.iterator();
	}

	@Override
	public boolean hasNext() {
		return internal.hasNext();
	}

	@Override
	public T next() {
		return internal.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"SortingIterator does not support remove");
	}

}