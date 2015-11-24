// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.chart2D;

import net.sourceforge.chart2d.Dataset;
import uk.ac.ed.epcc.webapp.charts.AbstractPeriodSetPlot;
import uk.ac.ed.epcc.webapp.time.TimePeriod;
@uk.ac.ed.epcc.webapp.Version("$Id: Chart2DSetPlot.java,v 1.7 2014/09/15 14:30:13 spb Exp $")


public class Chart2DSetPlot extends AbstractPeriodSetPlot {
	
	private net.sourceforge.chart2d.Dataset ds;
	protected Chart2DSetPlot(Chart2DChartData chart_data,TimePeriod period,int nsets) {
		super(period);
		ds = new Dataset(nsets,1,1);
		this.chart_data=chart_data;
	}

	@Override
	public String[] getLegends() {
		// in chart2d legends are set on the chart itself 
		// this is easy for setplots as there is only one Plot object per chart
		return chart_data.getLegends();
	}

	@Override
	public void setLegends(String[] leg) {
		super.setLegends(leg);
		// in chart2d legends are set on the chart itself 
		// this is easy for setplots as there is only one Plot object per chart
		chart_data.setLegends(leg);
	}
	private final Chart2DChartData chart_data;
	public void add(int set, float value){

	}

	public float get(int set) {
		return ds.get(set,0,0);
	}

	public void set(int set, float value) {
		ds.set(set,0,0,value);
	}


	public int getNumSets() {
		return ds.getNumSets();
	}


	public void setNumSets(int nsets) {
		ds.setSize(nsets, 1, 1);
	}
	protected net.sourceforge.chart2d.Dataset getDataset() {

		return ds;
	}
}