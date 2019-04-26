//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Color;
import java.util.List;

import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;

/**
 * A ChartData that represents a TimeChart
 * 
 * @author spb
 * @param <P> Type of Plot
 * 
 */
public interface TimeChartData<P extends PeriodSequencePlot> extends PeriodChartData<P> {
	
	/** Set a hint that we are going to use this data in a cummulative plot
	 * 
	 * @param value
	 */
	public void setCumulative(boolean value);
	
	public boolean isCumulative();
	
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
	
	/** mark a threshold/warning level
	 * 
	 * @param value
	 * @param col optional Color to mark threshold with
	 */
	public abstract void addWarningLevel(double value,Color col);
	
	public List<P> getPlots();
	
}