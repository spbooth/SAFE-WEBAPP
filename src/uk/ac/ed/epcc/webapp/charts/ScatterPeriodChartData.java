// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Color;

import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ScatterPeriodChartData.java,v 1.4 2014/09/15 14:30:12 spb Exp $")
public interface ScatterPeriodChartData<P extends ScatterPeriodPlot> extends ChartData<P> {
	
	public void setPeriod(SplitTimePeriod period, int nsplit);
	
	public SplitTimePeriod getPeriod();
	
	public int getNSplits();
	
	/** Add a data series to the plot.
	 * 
	 * @param label {@link String} label for series.
	 * @param c {@link Color} can be null.
	 * @return {@link ScatterPeriodPlot}
	 */
	public P addSeries(String label,Color c);
	
	public void setXAxis(String text);
	
	public void setYAxis(String text);
}
