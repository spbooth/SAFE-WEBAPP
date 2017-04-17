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
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.time.Period;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;

/** An {@link AppContextService} that selects the desired chart implementation classes
 * @author spb
 *
 */
public abstract class GraphService implements AppContextService<GraphService>, Contexed{
	
	protected final AppContext conn;
	/**
	 * 
	 */
	public GraphService(AppContext conn) {
		this.conn=conn;
	}
	
	public TimeChart getTimeChart(){
		TimeChart t = new TimeChart<>(conn);
		t.setChartData(getTimeChartData());
		return t;
	}
	protected abstract TimeChartData getTimeChartData();
	
	public  final PieChart getPieChart() {
		PieChart ptc = new PieChart(conn);
		ptc.setChartData(getPieChartData());
		return ptc;
	}
	/**
	 * @return
	 */
	protected abstract ChartData getPieChartData();
	public final PieTimeChart getPieTimeChart(Period p) {
		PieTimeChart ptc = new PieTimeChart(conn,p);
		PieTimeChartData chart = getPieTimeChartData();
		
		chart.setPeriod(p);
		ptc.setChartData(chart);
		return ptc;
	}
	
	/**
	 * @return
	 */
	protected abstract PieTimeChartData getPieTimeChartData();
	public final ScatterPeriodChart getScatterPeriodChart(SplitTimePeriod period, int nsplit){
		ScatterPeriodChart chart = new ScatterPeriodChart(conn);

		ScatterPeriodChartData chart_data = getScatterPeriodChartData();

		chart_data.setPeriod(period, nsplit);
		chart.setChartData(chart_data);

		
		return chart;
	}
/**
	 * @return
	 */
	protected abstract  ScatterPeriodChartData getScatterPeriodChartData() ;

	public final BarTimeChart getBarTimeChart( Period p) throws Exception {
		BarTimeChart ptc = new BarTimeChart(conn,p);
		BarTimeChartData chartData=getBarTimeChartData();
		
		chartData.setPeriod(p);
		ptc.setChartData(chartData);
		
		
		return ptc;
	}	
	
/**
	 * @return
	 */
	protected abstract BarTimeChartData getBarTimeChartData() ;
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
 */
@Override
public void cleanup() {


	
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
 */
@Override
public final  Class<? super GraphService> getType() {
	return GraphService.class;
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
 */
@Override
public AppContext getContext() {
	return conn;
} 

}

