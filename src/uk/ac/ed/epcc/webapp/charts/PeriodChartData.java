// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * @author spb
 *
 */
public interface PeriodChartData<P extends PeriodPlot> extends ChartData<P> {

	public TimePeriod getPeriod();
}
