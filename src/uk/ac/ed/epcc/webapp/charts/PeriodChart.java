// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.util.Date;
import java.util.Iterator;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.charts.strategy.QueryMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.SetRangeMapper;
import uk.ac.ed.epcc.webapp.content.Table;
//import uk.ac.ed.epcc.webapp.time.Period;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** PeriodChart is a {@link Chart} where the data represents a plot over some period of time.
 * Various PlotStrategies are provided to map the data onto time periods.
 * @see TimeChart PieTimeChart
 * 
 * @author spb
 * @param <P> Type of Plot
 *
 */
public abstract class PeriodChart<P extends PeriodPlot> extends Chart {
	
	protected PeriodChart(AppContext c) {
		super(c);
		
	}

	
	
	protected PeriodChart(AppContext c,TimePeriod p) {
		super(c);
	}
	
	/**
	 * Basic method for mapping an object into a Plot Keep this as a method on
	 * Chart not Plot as this prevents us needing to subclass Plot for Pie and
	 * Time Charts
	 * 
	 * @param ds
	 * @param t
	 * @param object
	 * @throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException
	 */
	protected final <D> void addData(P ds, SetRangeMapper<D> t, D object)
			throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException{
		ds.addData(t, object);
	}

	/** Populate chart using a QueryMapper and a factory 
	 * 
	 * @param <F> type of factory
	 * @param ps Plot to add data to
	 * @param t  QueryMapper to use
	 * @param fac Factory to query
	 * @return true if data added
	 * @throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException 
	 */
	public final <F> boolean addMapData(P ps,QueryMapper<F> t, F fac)
	        throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException{
		return ps.addMapData(t, fac);
	}
	
	/**
	 * Add data from an Iterator over objects using a strategy transform.
	 * @param <D> type of object being added
	 * @param ds 
	 * 
	 * 
	 * @param t
	 * @param i
	 * @throws InvalidTransformException
	 */
	public final <D> void addDataIterator(P ds,SetRangeMapper<D> t, Iterator<D> i)
			throws InvalidTransformException {
		// Note we can't call this method addData or there may be an ambiguity against
		// this single addData because the type erases to Object  
		while (i.hasNext()) {
			addData(ds, t, i.next());
		}
	}

	/**
	 * add data from a set of objects using strategy transform
	 * @param <D> type of object being added
	 * @param ds 
	 * 
	 * @param t
	 * @param o
	 * @throws InvalidTransformException
	 */
	public final <D> void addDataArray(P ds, SetRangeMapper<D> t, D[] o)
			throws InvalidTransformException {
		for (int i = 0; i < o.length; i++) {
			addData(ds, t, o[i]);
		}
	}
	public PeriodChartData<P> getChartData(){
		return (PeriodChartData<P>) super.getChartData();
	}
	public final TimePeriod getPeriod(){
		return getChartData().getPeriod();
	}
	public Date getStartDate(){
		return getPeriod().getStart();
	}
	public Date getEndDate(){
		return getPeriod().getEnd();
	}
	
	/**
	 * Generate a table representing the Chart
	 * 
	 * @return a Table
	 */
	public abstract Table getTable();
	
}