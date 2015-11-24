// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.charts.strategy.RangeMapper;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ScatterPeriodPlot.java,v 1.2 2014/09/15 14:30:12 spb Exp $")
public interface ScatterPeriodPlot  extends Plot{
	/**
	 * Basic method for mapping an object into the plot. 
	 * <p>
	 * Note that the same interface can
	 * be used to map individual records or to query a factory for a required period.
	 * 
	 * @param x A {@link RangeMapper} to generate the X coordinate
	 * @param y A {@link RangeMapper} to generate the Y coordinate
	 * @param object
	 * @throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException
	 */
	public <D> void addData(RangeMapper<D> x, RangeMapper<D> y, D object)
			throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException;

	public void addPoint(float x, float y);
	public void setLabel(String key);
}
