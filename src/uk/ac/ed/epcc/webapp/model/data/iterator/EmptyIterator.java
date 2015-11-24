// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
@uk.ac.ed.epcc.webapp.Version("$Id: EmptyIterator.java,v 1.3 2014/12/05 08:06:11 spb Exp $")


public class EmptyIterator<E> implements Iterator<E> {

	public boolean hasNext() {
		return false;
	}

	public E next() {
		
		throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from Empty iterator");
	}

}