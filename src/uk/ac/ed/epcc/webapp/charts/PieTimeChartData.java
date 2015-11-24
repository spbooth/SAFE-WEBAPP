// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * @author spb
 * @param <C> 
 * @param <P> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PieTimeChartData.java,v 1.3 2014/09/15 14:30:12 spb Exp $")
public interface PieTimeChartData<P extends PeriodSetPlot> extends PieChartData<P>, PeriodChartData<P> {

	public void setPeriod(TimePeriod period);
}
