// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PeriodChartData.java,v 1.3 2014/09/15 14:30:12 spb Exp $")
public interface PeriodChartData<P extends PeriodPlot> extends ChartData<P> {

	public TimePeriod getPeriod();
}
