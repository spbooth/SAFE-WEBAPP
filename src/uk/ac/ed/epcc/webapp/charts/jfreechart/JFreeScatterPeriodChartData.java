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
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.ScatterRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;

/**
 * @author spb
 *
 */

public class JFreeScatterPeriodChartData extends JFreeChartData<JFreeScatterPlot> implements ScatterPeriodChartData<JFreeScatterPlot>{
	private SplitTimePeriod period;
	private int nsplit;
	JFreeChart chart;
	int nseries=0;
	String xaxis;
	String yaxis;
	XYSeriesCollection coll = new XYSeriesCollection();
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ChartData#makeDataSet(int)
	 */
	public JFreeScatterPlot makeDataSet(int i) throws Exception {
		String label="Series"+nseries++;
		JFreeScatterPlot plot = new JFreeScatterPlot(label, period, nsplit);
		
		coll.addSeries(plot.series);
		return plot;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.jfreechart.JFreeChartData#getJFreeChart()
	 */
	@Override
	public JFreeChart getJFreeChart() {
		if( chart == null ){
			chart =ChartFactory.createScatterPlot(title, xaxis, yaxis, coll, PlotOrientation.VERTICAL, false, false, false);
			XYDotRenderer render = new XYDotRenderer();
			render.setDotHeight(2);
			render.setDotWidth(2);
			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setRenderer(render);
		}
		return chart;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData#setPeriod(uk.ac.ed.epcc.webapp.time.SplitTimePeriod, int)
	 */
	public void setPeriod(SplitTimePeriod period, int nsplit) {
		this.period=period;
		this.nsplit=nsplit;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData#getPeriod()
	 */
	public SplitTimePeriod getPeriod() {
		return period;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData#getNSplits()
	 */
	public int getNSplits() {
		return nsplit;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData#addSeries(java.lang.String, java.awt.Color)
	 */
	public JFreeScatterPlot addSeries(String label, Color c) {
		JFreeScatterPlot plot = new JFreeScatterPlot(label, period, nsplit);
		coll.addSeries(plot.series);
		return plot;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData#setXAxis(java.lang.String)
	 */
	public void setXAxis(String text) {
		this.xaxis=text;
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData#setYAxis(java.lang.String)
	 */
	public void setYAxis(String text) {
		this.yaxis=text;
		
	}

}