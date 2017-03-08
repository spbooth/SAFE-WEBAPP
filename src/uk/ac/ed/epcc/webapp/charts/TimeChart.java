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

import java.awt.Color;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.charts.chart2D.Chart2DTimeChartData;
import uk.ac.ed.epcc.webapp.charts.jfreechart.JFreeTimeChartData;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.preferences.Preference;
//import uk.ac.ed.epcc.webapp.charts.jfreechart.JFreeChartData;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;
import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/**
 * A helper class used to make graphs
 * 
 * Basic constructors define the time range, We then use call methods that add
 * additional plots these return the {@link PeriodSequencePlot} so we can add data with the addData
 * methods.
 * <p>
 * The number of points to plot is ultimately chosen by the implementing package but the the {@link TimePeriod}
 * is specified as a {@link SplitTimePeriod} and a guidance parameter indicates roughly the number of points that should be within each 
 * split of the period.
 * The period splits should also give an indication of the desired frequency of the time axis labels and of columns in the table representation
 * 
 * <p>
 * The model is that Object classes can implement a method to add additional
 * plots to a TimeChart
 * 
 * Note that we don't assume the major time ticks are necessarily the same
 * length this allows us to plot on month boundaries etc.
 * 
 * @param <P> Type of Plot

 */
// TODO move to contructors that auto configure the number of major slices
// to avoid label squash.


public class TimeChart<P extends PeriodSequencePlot> extends PeriodChart<P>{
	
	public static final Feature JFREE_TIMECHART_FEATURE = new Preference("chart.timechart.use_jfreechart", false, "Use JFreechart for time-charts");
	public static final Feature JFREE_TIMECHART_STEP_FEATURE = new Feature("chart.jfreechart.timechart.use_step", false, "Use step rendering for timecharts");

	
   
	protected TimeChart(AppContext c) {
        super(c);
	}

	public TimeChartData<P> getChartData(){
		return (TimeChartData<P>) super.getChartData();
	}
	/**
	 * add a solid area graph to the timechart and return the dataset so values
	 * can be added
	 * 
	 * @param nSet
	 * @return Dataset
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public P addAreaGraph(int nSet) throws Exception {
		TimeChartData<P> chart = (TimeChartData) getChartData();
		P p = chart.makeDataSet(nSet);
		return addAreaGraph(p,null);
	}
	@SuppressWarnings("unchecked")
	public P addAreaGraph(P p, Color custom[]) throws Exception{
		TimeChartData<P> chart = (TimeChartData) getChartData();
		chart.addAreaGraph(p,custom);
		return p;
	}
	@SuppressWarnings("unchecked")
	public P addAreaGraph(int nSet,Color custom[]) throws Exception {
		TimeChartData<P> chart = (TimeChartData) getChartData();
		P p = chart.makeDataSet(nSet);
		return addAreaGraph(p, custom);
	}

	
	
	/**
	 * add a solid area graph with lables to the timechart and return the
	 * dataset so values can be added
	 * 
	
	 * @param new_legends
	 * @return DataSet
	 * @throws Exception 
	 */
	public P addAreaGraph(String new_legends[]) throws Exception {
		int nSet = new_legends.length;
		P ds = addAreaGraph(nSet);
		ds.setLegends(new_legends);
		return ds;
	}

	
	@SuppressWarnings("unchecked")
	public P addLineGraph(int nSet) throws Exception {
		TimeChartData<P> chart = (TimeChartData) getChartData();
		P p = chart.makeDataSet(nSet);
		return addLineGraph(p, null);
	}
	
	public P addLineGraph(P p, Color custom[]) throws Exception{
		TimeChartData<P> chart = (TimeChartData) getChartData();
		return chart.addLineGraph(p, custom);
	}

	@SuppressWarnings("unchecked")
	public P addLineGraph(int nSet, Color custom[]) throws Exception {
		TimeChartData<P> chart = (TimeChartData) getChartData();
		P p = chart.makeDataSet(nSet);
		return chart.addLineGraph(p, custom);
	}

	/**
	 * add a line graph returning the dataset so data can be added
	 * 
	 * @param new_legends
	 * @return Dataset
	 * @throws Exception 
	 */
	public P addLineGraph(String new_legends[]) throws Exception {
		return addLineGraph(new_legends,null);
	}
	/**
	 * add a line graph returning the dataset so data can be added
	 * 
	 * @param new_legends
	 * @param colours
	 * @return Dataset
	 * @throws Exception 
	 */
	public P addLineGraph(String new_legends[],Color colours[]) throws Exception {
		int nSet = new_legends.length;
		
		P p = addLineGraph(nSet,colours);
		p.setLegends( new_legends);
		return p;
	}


	

	
   
	@Override
	public Table getTable() {
		
		Table t = new Table();
		String legendName = getLegendName();
		for(SetPlot p : getChartData().getPlots()){
			t.add(p.getTable(getChartData().getQuantityName()));
			if( p.hasLegends()){
				t.setKeyName(legendName);
			}
		}
		
		
		return t;
	}
	
	
	public void addWarningLevel(double value){
		((TimeChartData)getChartData()).addWarningLevel(value);
	}
	
	/* {{{ public TimeChart(){ */
	private static TimeChart getInstance(AppContext c) {
		TimeChart t = new TimeChart(c);
		
		if( JFREE_TIMECHART_FEATURE.isEnabled(c)){
			JFreeTimeChartData chart2 = new JFreeTimeChartData();
			chart2.setUseStep(JFREE_TIMECHART_STEP_FEATURE.isEnabled(c));
			t.setChartData(chart2);
		}else{
			t.setChartData(new Chart2DTimeChartData());
		}
		
		
		return t;
	}

	public static TimeChart getInstance(AppContext c, Calendar s, Calendar e,
			int major, int minor) throws InvalidArgument {
		return getInstance(c, s.getTime(), e.getTime(), major, minor);
	}

	/**
	 * generate a timechart with splits at multiples of a Calendar field number
	 * of major splits is calculated so as to include the end time.
	 * 
	 * @param conn
	 * 
	 * @param start
	 *            start time
	 * @param end
	 *            minimum end time
	 * @param field
	 *            field to use
	 * @param count
	 *            multiples of field in major step
	 * @param minor
	 *            number of minor steps.
	 * @return TimeChart
	 * @throws InvalidArgument
	 */
	public static TimeChart getInstance(AppContext conn, Calendar start,
			Calendar end, int field, int count, int minor)
			throws InvalidArgument {

		int major = 0;
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(start.getTime());
		do {
			major++;
			tmp.add(field, count);
		} while (tmp.before(end));
		if (major < 2) {
			// just default to regular period
			return getInstance(conn, start, end, 1, minor);
		}
		
		// make sure we still include the end point
		tmp.setTime(start.getTime());
		tmp.add(field, count * major);
		while (tmp.before(end)) {
			major++;
			tmp.add(field, count);
		}
		return getInstance(conn, start, field, count, major, minor);
	}

	/**
	 * Create TimeChart with splits at multiples of a Calendar field
	 * 
	 * @param conn
	 * 
	 * @param c
	 *            start time
	 * @param field
	 *            which Calendar field to use
	 * @param count
	 *            multiples of field per major division
	 * @param major
	 *            number of major divisions
	 * @param minor
	 *            number of minor divisions.
	 * @return TimeChart
	 * @throws InvalidArgument
	 */
	public static TimeChart getInstance(AppContext conn, Calendar c, int field,
			int count, int major, int minor) throws InvalidArgument {

		
		return getInstance(conn,new CalendarFieldSplitPeriod(c,field,count,major),minor);
	}

	public static TimeChart getInstance(AppContext c, Date s, Date e,
			int major, int minor) throws InvalidArgument {
		return getInstance(c, new RegularSplitPeriod(s,e,major), minor);
	}

	

	/**
	 * @param c AppContext
	 * @param p SplitPeriod
	 * @param minor 
	
	 * @return TimeChart
	 * @throws InvalidArgument
	 */
	/* }}} */
	public static TimeChart getInstance(AppContext c, SplitTimePeriod p, int minor) throws InvalidArgument {
		assert(p!=null);
		TimeChart t = getInstance(c);
	
		TimeChartData chartData = t.getChartData();
	
		
		chartData.setPeriod(p, minor);
		return t;
	}


}