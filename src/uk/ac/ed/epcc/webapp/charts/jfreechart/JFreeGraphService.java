//| Copyright - The University of Edinburgh 2017                            |
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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.charts.BarTimeChartData;
import uk.ac.ed.epcc.webapp.charts.ChartData;
import uk.ac.ed.epcc.webapp.charts.GraphService;
import uk.ac.ed.epcc.webapp.charts.PieTimeChartData;
import uk.ac.ed.epcc.webapp.charts.ScatterPeriodChartData;
import uk.ac.ed.epcc.webapp.charts.TimeChartData;

/**
 * @author spb
 *
 */
public class JFreeGraphService extends GraphService{
	
	
	/**
	 * 
	 */
	public JFreeGraphService(AppContext conn) {
		super(conn);
	}
	
	


/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.charts.GraphService#getTimeChartData()
 */
@Override
protected TimeChartData getTimeChartData() {
	return new JFreeTimeChartData();
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.charts.GraphService#getPieChartData()
 */
@Override
protected ChartData getPieChartData() {
	return new JFreePieChartData();
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.charts.GraphService#getPieTimeChartData()
 */
@Override
protected PieTimeChartData getPieTimeChartData() {
	return new JFreePieChartData();
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.charts.GraphService#getScatterPeriodChartData()
 */
@Override
protected ScatterPeriodChartData getScatterPeriodChartData() {
	return new JFreeScatterPeriodChartData();
}




/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.charts.GraphService#getBarTimeChartData()
 */
@Override
protected BarTimeChartData getBarTimeChartData() {
	return new JFreeBarTimeChartData();
} 

}
