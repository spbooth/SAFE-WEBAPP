// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

/**
 * an input that has some Domain Object associated with its values
 * 
 * @author spb
 * @param <T> type of item object
 * 
 */
public interface ItemInput<T>{
	/**
	 * get the domain Object associated with the current value
	 * 
	 * @return Object
	 */
	public abstract T getItem();

	/**
	 * Set the value of the input using an item
	 * 
	 * @param item
	 */
	public abstract void setItem(T item);
}