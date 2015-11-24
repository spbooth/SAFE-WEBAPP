// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.util.Date;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.charts.strategy.QueryMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.SetRangeMapper;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.time.Period;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

public abstract class SetPeriodChart<P extends PeriodSetPlot> extends PeriodChart<P> {

	protected SetPeriodChart(AppContext c, Period p) {
		super(c, p);
	}

	

	@Override
	public Table getTable() {
		Table t = new Table();
		
		P ds = (P) getPlot();
		if( ds != null ){
			t.add(ds.getTable(getChartData().getQuantityName()));
			if( ds.hasLegends()){
				t.setKeyName(getLegendName());
			}
		}
		return t;

	}

	public abstract  P getPlot();
	

}