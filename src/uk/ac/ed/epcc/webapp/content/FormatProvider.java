// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

/** A class that generates a {@link Labeller} object.
 * 
 * @author spb
 *
 *  @param <T> type of input object
 * @param <R> type of return object.
 */
public interface FormatProvider<T,R> {
	/** Get a labeller to format the target objects.
	 * This can return null.
	 * 
	 * @return Labeller
	 */
	public Labeller<T,R> getLabeller(); 
	
}