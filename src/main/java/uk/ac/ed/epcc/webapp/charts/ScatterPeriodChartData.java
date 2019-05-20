//| Copyright - The University of Edinburgh 2014                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Color;

import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;

/**
 * @author spb
 *
 */

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