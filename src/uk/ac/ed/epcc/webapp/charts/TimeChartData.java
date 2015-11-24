// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Color;
import java.util.List;

import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;

/**
 * A ChartData that represents a TimeChart
 * 
 * @author spb
 * @param <P> Type of Plot
 * 
 */
public interface TimeChartData<P extends PeriodSequencePlot> extends PeriodChartData<P> {
	
	public void setPeriod(SplitTimePeriod period, int nsplit);
	
	
	/** Add a Plot as an area graph.
	 * 
	 * @param plot
	 * @return Plot
	 * @throws Exception 
	 */
	public abstract P addAreaGraph(P plot) throws Exception;
	
	/** Add a Plot as an area graph with custom colours 
	 * 
	 * @param plot
	 * @param custom_colours
	 * @return Plot
	 */
	public abstract P addAreaGraph(P plot, Color custom_colours[])throws Exception;
    

	/** Add a Plot as a line graph
	 * 
	 * @param plot
	 * @return Plot
	 */
	public abstract P addLineGraph(P plot)throws Exception;
	
	
	/** add a Plot as a line graph
	 * 
	 * @param plot
	 * @param custom_colors
	 * @return Plot
	 */
	public abstract P addLineGraph(P plot, Color custom_colors[])throws Exception;
	
	public abstract void addWarningLevel(double value);
	
	public List<P> getPlots();
	
}