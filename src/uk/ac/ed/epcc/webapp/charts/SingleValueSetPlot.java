// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

/** This is a Plot where each member of the set only contain a single value.
 * e.g. a pie chart.
 * 
 * @author spb
 *
 */
public interface SingleValueSetPlot extends SetPlot {


	public abstract float get(int set);

	public abstract void set(int set, float value);

	public abstract void add(int set, float value);


}