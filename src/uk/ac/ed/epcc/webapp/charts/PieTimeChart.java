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
/*
 * Created on 22-Mar-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.charts;

import java.awt.Color;
import java.util.Calendar;

import uk.ac.ed.epcc.webapp.AppContext;
//import uk.ac.ed.epcc.webapp.charts.jfreechart.JFreePieChartData;
import uk.ac.ed.epcc.webapp.time.Period;

/**
 * PieTimeChart This is the PieChart equivalent of a TimeChart It plots the sum
 * of quantities over a period and uses the same mapping classes the intention
 * is that you can use this to generate piecharts and timecharts interchangably.
 * 
 * @author spb
 * @param <P> type of Plot object
 * 
 */


public final class PieTimeChart<P extends PeriodSetPlot> extends SetPeriodChart<P> {
	

	private P plot=null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.report.QuantByCategory#getData(uk.ac.hpcx.AppContext)
	 */
	protected PieTimeChart(AppContext c,Period p) {
		super(c,p);
	}

	@SuppressWarnings("unchecked")
	public P addPieChart(int nset) {
		PieTimeChartData<P> chart = (PieTimeChartData) getChartData();
		return plot=chart.addPieChart(nset);
	}
	@SuppressWarnings("unchecked")
	public P addPieChart(int nset,Color custom[]) {
		PieChartData<P> chart = (PieChartData) getChartData();
		return plot=chart.addPieChart(nset,custom);
	}

	public static  PieTimeChart getInstance(AppContext c, Period p) {
		return c.getService(GraphService.class).getPieTimeChart(p);
	}

	public static  PieTimeChart getInstance(AppContext c, Calendar s, Calendar e) {
		return getInstance(c, new Period(s.getTime(),e.getTime()));
	}

	@Override
	public P getPlot() {
		return plot;
	}

}