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

import java.awt.Color;

import net.sourceforge.chart2d.Chart2DProperties;
import net.sourceforge.chart2d.LBChart2D;
import net.sourceforge.chart2d.LegendProperties;
import net.sourceforge.chart2d.MultiColorsProperties;
import net.sourceforge.chart2d.Object2DProperties;
import net.sourceforge.chart2d.PieChart2D;
import net.sourceforge.chart2d.PieChart2DProperties;
import uk.ac.ed.epcc.webapp.charts.Chart;
import uk.ac.ed.epcc.webapp.charts.PieChartData;
import uk.ac.ed.epcc.webapp.charts.PieTimeChartData;
import uk.ac.ed.epcc.webapp.time.TimePeriod;



public class PieChart2DChartData extends Chart2DChartData<Chart2DSetPlot> implements
		PieTimeChartData<Chart2DSetPlot>{

	private TimePeriod period;
	public TimePeriod getPeriod() {
		return period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}

	public PieChart2DChartData(){
		// General object properties:
		Object2DProperties object2DProps = new Object2DProperties();

		// Configure chart properties
		Chart2DProperties chart2DProps = new Chart2DProperties();
		chart2DProps.setChartDataLabelsPrecision(-2);

		// Configure legend properties
		LegendProperties legendProps = new LegendProperties();

		

		// Configure chart
		PieChart2D piechart2D = new PieChart2D();
		piechart2D.setObject2DProperties(object2DProps);
		piechart2D.setChart2DProperties(chart2DProps);
		piechart2D.setLegendProperties(legendProps);

		
		
		// Optional validation: Prints debug messages if invalid only.
		// if (!piechart2D.validate (false)) piechart2D.validate (true);
		// and store:
		setChart2D(piechart2D);

	}

	public Chart2DSetPlot addPieChart(int nset, Color custom_colour[]) {
		Chart2DSetPlot ds = new Chart2DSetPlot(this,period, nset);
		PieChart2D piechart2D = (PieChart2D) c2d;
		// Configure graph component colors
		MultiColorsProperties multiColorsProps = new MultiColorsProperties();
		if( custom_colour != null ){
			multiColorsProps.setColorsCustom(custom_colour);
			multiColorsProps.setColorsCustomize(true);
		}
		// Configure pie area
		PieChart2DProperties pieChart2DProps = new PieChart2DProperties();
		pieChart2DProps.setPieLabelsExistence(true);
		pieChart2DProps.setPieLabelsType(PieChart2DProperties.PERCENT);
		pieChart2DProps.setPiePreferredSize(1000);
		piechart2D.setMultiColorsProperties(multiColorsProps);
		piechart2D.setPieChart2DProperties(pieChart2DProps);
		piechart2D.setDataset(ds.getDataset());
		return ds;
	}

	public Chart2DSetPlot makeDataSet(int i) {
		return new Chart2DSetPlot(this,period, i);
	}

	String quantity_name;
	public void setQuantityName(String s) {
		quantity_name=s;
//		LBChart2D c = (LBChart2D) c2d;
//		if( s != null ){
//			c.getGraphChart2DProperties().setNumbersAxisTitleText(s);
//			c.getGraphChart2DProperties().setNumbersAxisTitleExistence(true);
//		}else{
//			c.getGraphChart2DProperties().setNumbersAxisTitleExistence(false);
//		}
	}
	public String getQuantityName(){
		return quantity_name;
	}

	public Chart2DSetPlot addPieChart(int nset) {
		return addPieChart(nset, null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ChartData#setGraphical(boolean)
	 */
	public void setGraphical(boolean val) {
		// nothing needed.
		
	}



}