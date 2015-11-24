// Copyright - The University of Edinburgh 2011
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
import uk.ac.ed.epcc.webapp.charts.chart2D.PieChart2DChartData;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
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
@uk.ac.ed.epcc.webapp.Version("$Id: PieTimeChart.java,v 1.38 2014/09/15 14:30:12 spb Exp $")

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
		PieTimeChart ptc = new PieTimeChart(c,p);
		Class<? extends PieTimeChartData> clazz = c.getPropertyClass(PieTimeChartData.class, PieChart2DChartData.class, "PieTimeChartData");
		PieTimeChartData chart2;
		try {
			chart2 = c.makeObject(clazz);
			chart2.setPeriod(p);
			ptc.setChartData(chart2);
		} catch (Exception e) {
			c.getService(LoggerService.class).getLogger(PieTimeChart.class).error("Error making chart data",e);
			return null;
		}
		
		
		return ptc;
	}

	public static  PieTimeChart getInstance(AppContext c, Calendar s, Calendar e) {
		return getInstance(c, new Period(s.getTime(),e.getTime()));
	}

	@Override
	public P getPlot() {
		return plot;
	}

}