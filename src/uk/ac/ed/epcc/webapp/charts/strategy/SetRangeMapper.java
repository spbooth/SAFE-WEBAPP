// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.strategy;



/**
 * This interface extends RangeMapper but adds the capability of specifying
 * which dataset/plot the object contributes to.
 * @param <T> type of object being mapped
 */
public interface SetRangeMapper<T> extends RangeMapper<T> {

	/**
	 * which dataset/plot does this object contribute to.
	 * 
	 * @param o
	 * @return int
	 */
	public int getSet(T o);

}