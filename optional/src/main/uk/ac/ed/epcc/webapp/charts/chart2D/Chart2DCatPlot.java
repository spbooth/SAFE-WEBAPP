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
package uk.ac.ed.epcc.webapp.charts.chart2D;

import net.sourceforge.chart2d.Dataset;
import uk.ac.ed.epcc.webapp.charts.AbstractPeriodSetPlot;
import uk.ac.ed.epcc.webapp.charts.AbstractSingleValueSetPlot;
import uk.ac.ed.epcc.webapp.time.TimePeriod;


/** A SetPlot where the sets are mapped to the Category axis of Chart2D
 * For use in bar-charts where the data is sub-divided by categories.
 * 
 * @author spb
 *
 */


public class Chart2DCatPlot extends AbstractPeriodSetPlot {
	@Override
	public String[] getLegends() {
		// in chart2d legends are set on the chart itself 
		// this is easy for setplots as there is only one Plot object per chart
		return chart_data.getLegends();
	}

	@Override
	public void setLegends(String[] leg) {
		super.setLegends(leg);
		// in chart2d legends are set on the chart itself 
		// this is easy for setplots as there is only one Plot object per chart
		chart_data.setLegends(leg);
	}
	private final Chart2DChartData chart_data;
	private net.sourceforge.chart2d.Dataset ds;
	protected Chart2DCatPlot(Chart2DChartData chart_data,TimePeriod period,int nset) {
		super(period);
		ds = new Dataset(1,nset,1);
		this.chart_data=chart_data;
	}

	public void add(int set, float value) {
		set(set,get(set)+value);
	}

	public float get(int set) {
		return ds.get(0,set,0);
	}

	public void set(int set, float value) {
		ds.set(0,set,0,value);	
	}

	public int getNumSets() {
		return ds.getNumCats();
	}


	public void setNumSets(int nsets) {
		ds.setSize(1, nsets, 1);
	}
	protected net.sourceforge.chart2d.Dataset getDataset() {

		return ds;
	}
}