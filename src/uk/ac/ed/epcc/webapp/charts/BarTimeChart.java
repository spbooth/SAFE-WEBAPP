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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.preferences.Preference;
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

public final class BarTimeChart<P extends PeriodSetPlot> extends SetPeriodChart<P> {
	private P plot=null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.report.QuantByCategory#getData(uk.ac.hpcx.AppContext)
	 */
	protected BarTimeChart(AppContext c,Period p) {
		super(c,p);
	}

	

	@SuppressWarnings("unchecked")
	public P addBarChart(int nset) {
		BarTimeChartData<P> chart = (BarTimeChartData) getChartData();
		return plot=chart.addBarChart(nset);
	}


	public static  BarTimeChart getInstance(AppContext c, Period p) throws Exception {
		return c.getService(GraphService.class).getBarTimeChart(p);

	}

	public static  BarTimeChart getInstance(AppContext c, Calendar s, Calendar e) throws Exception {
		return getInstance(c, new Period(s.getTime(),e.getTime()));
	}

	@Override
	public P getPlot() {
		return plot;
	}
	@Override
    public void setPlot(P ds) {
    	this.plot=ds;
    }
}