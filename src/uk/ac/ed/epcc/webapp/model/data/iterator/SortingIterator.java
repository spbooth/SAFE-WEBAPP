// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: SortingIterator.java,v 1.2 2014/09/15 14:30:32 spb Exp $")

public class SortingIterator<T> implements Iterator<T> {
	private TreeSet<T> set;

	Iterator<T> internal;

	public SortingIterator(Iterator<T> i, Comparator<? super T> c) {
		set = new TreeSet<T>(c);
		while (i.hasNext()) {
			set.add(i.next());
		}
		internal = set.iterator();
	}

	public boolean hasNext() {
		return internal.hasNext();
	}

	public T next() {
		return internal.next();
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"SortingIterator does not support remove");
	}

}