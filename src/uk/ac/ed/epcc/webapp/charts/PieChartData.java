// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Color;

/**
 * A ChartData that represents a PieChart
 * 
 * @author spb
 * @param <P> 
 * 
 */
public interface PieChartData<P extends SingleValueSetPlot> extends ChartData<P> {
	public abstract P addPieChart(int nset);
	public abstract P addPieChart(int nset, Color custom_colours[]);
}