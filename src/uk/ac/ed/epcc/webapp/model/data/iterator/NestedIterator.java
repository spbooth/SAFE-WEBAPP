// Copyright - The University of Edinburgh 2011
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

/**
 * Wrapper that iterates over multiple iterators in turn.
 * 
 * @author spb
 * @param <T> Type of iterator returned
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NestedIterator.java,v 1.3 2014/09/15 14:30:32 spb Exp $")

public class NestedIterator<T> implements Iterator<T> {
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


}