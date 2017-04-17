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

import net.sourceforge.chart2d.Chart2DProperties;
import net.sourceforge.chart2d.GraphChart2DProperties;
import net.sourceforge.chart2d.GraphProperties;
import net.sourceforge.chart2d.LBChart2D;
import net.sourceforge.chart2d.LegendProperties;
import net.sourceforge.chart2d.MultiColorsProperties;
import net.sourceforge.chart2d.Object2DProperties;
import uk.ac.ed.epcc.webapp.charts.BarTimeChartData;
import uk.ac.ed.epcc.webapp.charts.BarTimeChart;
import uk.ac.ed.epcc.webapp.time.TimePeriod;



public class BarChart2DChartData extends Chart2DChartData<Chart2DCatPlot> implements
BarTimeChartData<Chart2DCatPlot>{

	private TimePeriod period;
public TimePeriod getPeriod() {
		return period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}

public BarChart2DChartData() {

// fill in default properties
// we can retreive and modify them later if necessary.
LBChart2D lb = new LBChart2D();
Object2DProperties o2dprop = new Object2DProperties();
Chart2DProperties c2dprop = new Chart2DProperties();
LegendProperties lprop = new LegendProperties();
lprop.setLegendExistence(false);
GraphChart2DProperties g2dprop = new GraphChart2DProperties();
//c2dprop.setChartDataLabelsPrecision (-1);



//g2dprop.setLabelsAxisTitleText("Time");
//g2dprop.setLabelsAxisTicksOutlineExistence(false);
//g2dprop.setNumbersAxisTicksOutlineExistence(false);
//g2dprop.setLabelsAxisTicksAlignment(GraphChart2DProperties.BETWEEN);
//g2dprop.setLabelsAxisBetweenLabelsOrTicksGapExistence(false);
//// Rescale the vertical space:
//g2dprop.setChartGraphableToAvailableRatio(0.95f);
lb.setObject2DProperties(o2dprop);
lb.setChart2DProperties(c2dprop);
lb.setLegendProperties(lprop);
lb.setGraphChart2DProperties(g2dprop);
setChart2D(lb);

}

public Chart2DCatPlot addBarChart(int nset) {
	LBChart2D lb = (LBChart2D) this.c2d;
	
	Chart2DCatPlot ds = new Chart2DCatPlot(this,period,nset);
	GraphProperties gprop = new GraphProperties();
	//gprop.setGraphBarsExistence(true);
	//gprop.setGraphLinesExistence(false);
	
	MultiColorsProperties mcp = new MultiColorsProperties();
	lb.addGraphProperties(gprop);
	lb.addDataset(ds.getDataset());
	lb.addMultiColorsProperties(mcp);

	


	return ds;
}

public Chart2DCatPlot makeDataSet(int i) {
return new Chart2DCatPlot(this, period,i);
}

@Override
public String[] getLegends() {
	LBChart2D c = (LBChart2D) c2d;
	return c.getGraphChart2DProperties().getLabelsAxisLabelsTexts();
}

@Override
public void setLegends(String[] lab) {
	LBChart2D c = (LBChart2D) c2d;
	c.getGraphChart2DProperties().setLabelsAxisLabelsTexts(lab);
}
String quantity_name;
public void setQuantityName(String s) {
	quantity_name=s;
	LBChart2D c = (LBChart2D) c2d;
	if( s != null ){
		c.getGraphChart2DProperties().setNumbersAxisTitleText(s);
		c.getGraphChart2DProperties().setNumbersAxisTitleExistence(true);
	}else{
		c.getGraphChart2DProperties().setNumbersAxisTitleExistence(false);
	}
}
public String getQuantityName(){
	return quantity_name;
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.charts.ChartData#setGraphical(boolean)
 */
public void setGraphical(boolean val) {
	// nothing needed
	
	
}
}