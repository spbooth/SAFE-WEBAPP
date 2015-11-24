// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.strategy;

import java.util.Date;

/**
 * Formatting object for mapping objects to time series. Objects are assumed
 * to map onto a finite time range of the plot these methods query the
 * overlap of an object to a time range and extract the appropriate
 * contribution to the graph.
 * Note we use the convention that time periods come after the start time and contain the end time.
 * @param <T> type of object being mapped
 */
public interface RangeMapper<T> extends PlotStrategy{
	
	/**
	 * get the contribution of the object to the specified time period.
	 * 
	 * @param o
	 * @param start
	 *            period start as Date
	 * @param end
	 *            period end as Date
	 * @return value of overlapp.
	 */
	public float getOverlapp(T o, Date start, Date end);

	/**
	 * does this object overlap with the specified time period.
	 * 
	 * @param o
	 * @param start
	 *            period start as Date
	 * @param end
	 *            period end as Date
	 * @return true if overlapps
	 */
	public boolean overlapps(T o, Date start, Date end);
}