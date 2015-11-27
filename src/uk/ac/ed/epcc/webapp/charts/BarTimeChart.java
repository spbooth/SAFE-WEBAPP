// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.util.Calendar;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.charts.chart2D.BarChart2DChartData;
import uk.ac.ed.epcc.webapp.time.Period;



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
		BarTimeChart ptc = new BarTimeChart(c,p);
		Class<? extends BarTimeChartData> clazz = c.getPropertyClass(BarTimeChartData.class, BarChart2DChartData.class, "BarChartData");
		
		
		BarTimeChartData chartData = c.makeObject(clazz);
		chartData.setPeriod(p);
		ptc.setChartData(chartData);
		
		
		return ptc;
	}

	public static  BarTimeChart getInstance(AppContext c, Calendar s, Calendar e) throws Exception {
		return getInstance(c, new Period(s.getTime(),e.getTime()));
	}



	@Override
	public P getPlot() {
		return plot;
	}

}