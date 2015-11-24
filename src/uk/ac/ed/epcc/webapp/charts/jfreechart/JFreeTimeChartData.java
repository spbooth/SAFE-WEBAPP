// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import uk.ac.ed.epcc.webapp.charts.TimeChartData;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;
@uk.ac.ed.epcc.webapp.Version("$Id: JFreeTimeChartData.java,v 1.13 2014/09/15 14:30:13 spb Exp $")


public class JFreeTimeChartData extends JFreeChartData<TimeChartDataSet> implements TimeChartData<TimeChartDataSet> {

	JFreeChart chart;
	SplitTimePeriod period;
	int nsplits;
	int ndatasets=0;
	LinkedList<TimeChartDataSet> plots = new LinkedList<TimeChartDataSet>();
	boolean use_bar=false;

	@Override
	public JFreeChart getJFreeChart() {
		return chart;
	}



	public void addWarningLevel(double value) {
		// TODO Auto-generated method stub
		
	}

	public TimeChartDataSet makeDataSet(int i) throws InvalidArgument {
		return new TimeChartDataSet(i, period, nsplits);
	}

	

	
	public TimeChartDataSet addAreaGraph(TimeChartDataSet plot) throws InvalidArgument {
		TimeChartDataSet myplot = addTimeSeries(plot);


		if( ! use_bar ){
			XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);

			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			((XYPlot)chart.getPlot()).setRenderer(myplot.getDatasetId(), renderer, false);
		}else{
			// plot as bar chart
			XYBarRenderer renderer = new XYBarRenderer();
			renderer.setBarPainter(new StandardXYBarPainter()); // colour blocks not shaded bars
			renderer.setDrawBarOutline(false);
			renderer.setShadowVisible(false);
			renderer.setUseYInterval(false);

			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			((XYPlot)chart.getPlot()).setRenderer(myplot.getDatasetId(), renderer, false);
		}


		return myplot;

	}

    private DateTickUnit getUnit(CalendarFieldSplitPeriod period){
    	int field = period.getField();
    	for( DateTickUnitType unit : new DateTickUnitType[]{ DateTickUnitType.SECOND,DateTickUnitType.MINUTE,DateTickUnitType.HOUR,DateTickUnitType.DAY,DateTickUnitType.MONTH,DateTickUnitType.YEAR}){
    		if( field == unit.getCalendarField()){
    			return new DateTickUnit(unit, period.getCount());
    		}
    	}
    	return null;
    }

	private TimeChartDataSet addTimeSeries(TimeChartDataSet dataset) throws InvalidArgument {
		if( dataset == null){
			dataset = makeDataSet(1);
		}
		if( chart == null ){
			chart = ChartFactory.createTimeSeriesChart(title, "Date", quantity, dataset, true, false, false); 
			DateAxis axis= (DateAxis) ((XYPlot)chart.getPlot()).getDomainAxis();
			axis.setRange(period.getStart(), period.getEnd());
			if( period instanceof CalendarFieldSplitPeriod){
				DateTickUnit u = getUnit((CalendarFieldSplitPeriod)period);
				if(u != null ){
					axis.setTickUnit(u, false, false);
				}
			}
		}else{
			((XYPlot)chart.getPlot()).setDataset(ndatasets, dataset);
		}
		dataset.setDatasetId(ndatasets);
		ndatasets++;
		return dataset;
	}

	public TimeChartDataSet addAreaGraph(TimeChartDataSet plot,
			Color[] custom_colours) throws InvalidArgument {
		return addAreaGraph(plot);
	}

	public TimeChartDataSet addLineGraph(TimeChartDataSet plot) throws InvalidArgument {
		return addTimeSeries(plot);
	}

	public TimeChartDataSet addLineGraph(TimeChartDataSet plot,
			Color[] custom_colors) throws InvalidArgument {
		return addTimeSeries(plot);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#setPeriod(uk.ac.ed.epcc.webapp.time.SplitTimePeriod, int)
	 */
	public void setPeriod(SplitTimePeriod period, int nsplit) {
		this.period=period;
		this.nsplits=nsplit;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.PeriodChartData#getPeriod()
	 */
	public TimePeriod getPeriod() {
		return period;
	}



	



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#getPlots()
	 */
	public List<TimeChartDataSet> getPlots() {
		// TODO Auto-generated method stub
		return null;
	}

}