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

import java.util.Calendar;
import java.util.Map.Entry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.time.Period;

/**
 * BarTimeChart This is the bar chart equivalent of a TimeChart 
 * and is functionally equivalent to {@link PieTimeChart}.
 * It plots the sum
 * of quantities over a period and uses the same mapping classes the intention
 * is that you can use this to generate barcharts and timecharts interchangably.
 * 
 * Unlike {@link TimeChart}s only a single dataset is plotted. This defaults to the
 * last created {@link Plot} but can be reset using the {@link #setPlot(PeriodSetPlot)} method.
 * 
 * @author spb
 * @param <P> type of Plot object
 * 
 */

public final class BarTimeChart<S extends PeriodSetPlot> extends PeriodChart<S> {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.report.QuantByCategory#getData(uk.ac.hpcx.AppContext)
	 */
	protected BarTimeChart(AppContext c) {
		super(c);
	}

	

	@SuppressWarnings("unchecked")
	public S getBarChartSeries(String series,int nset) {
		BarTimeChartData<S> chart = (BarTimeChartData) getChartData();
		return chart.getBarChartSeries(series, nset);
	}


	public static  BarTimeChart getInstance(AppContext c, Period p) throws Exception {
		return c.getService(GraphService.class).getBarTimeChart(p);

	}

	public static  BarTimeChart getInstance(AppContext c, Calendar s, Calendar e) throws Exception {
		return getInstance(c, new Period(s.getTime(),e.getTime()));
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.PeriodChart#getTable()
	 */
	@Override
	public Table getTable() {
		Table t = new Table();
		BarTimeChartData<S> data = (BarTimeChartData<S>) getChartData();
		for(Entry<String, S> e : data.getSeries().entrySet()) {
			S ds = e.getValue();
			String quant = e.getKey();
			if( ds != null ){
				t.add(ds.getTable(quant));
				if( ds.hasLegends()){
					t.setKeyName(getLegendName());
				}
			}
		}
		return t;
	}

}