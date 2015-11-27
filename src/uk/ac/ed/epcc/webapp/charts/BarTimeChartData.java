// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.time.TimePeriod;

public interface BarTimeChartData<P extends PeriodSetPlot> extends PeriodChartData<P> {
	public void setPeriod(TimePeriod period);
	public abstract P addBarChart(int nset);
}