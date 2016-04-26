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
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.SortOrder;

import uk.ac.ed.epcc.webapp.charts.TimeChartData;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;



public class JFreeTimeChartData extends JFreeChartData<TimeChartDataSet> implements TimeChartData<TimeChartDataSet> {

	/** If there are less then this number of datapoints in the time series show
	 * as a step chart
	 */
	private static final int STEP_THRESHOLD = 10;
	JFreeChart chart;
	DrawingSupplier drawing;
	SplitTimePeriod period;
	int nsplits;
	int ndatasets=0;
	LinkedList<TimeChartDataSet> plots = new LinkedList<TimeChartDataSet>();
	boolean use_bar=false;
	private boolean use_step=false;

	@Override
	public JFreeChart getJFreeChart() {
		return chart;
	}



	public void addWarningLevel(double value) {
		((XYPlot)chart.getPlot()).addRangeMarker(new ValueMarker(value,Color.RED,new BasicStroke(2f)));
		
	}

	public TimeChartDataSet makeDataSet(int i) throws InvalidArgument {
		return new TimeChartDataSet(i, period, nsplits);
	}

	

	
	public TimeChartDataSet addAreaGraph(TimeChartDataSet plot) throws InvalidArgument {
		TimeChartDataSet myplot = addTimeSeries(plot);


		if( ! use_bar ){
			if( getItems() < STEP_THRESHOLD || useStep()){
				//XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
				XYStepAreaRenderer renderer = new XYStepAreaRenderer(XYStepAreaRenderer.AREA);

				renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				renderer.setStepPoint(0.5); // change step half way between points, dataset set points in middle
				((XYPlot)chart.getPlot()).setRenderer(myplot.getDatasetId(), renderer, false);
			}else{
				XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
				//XYStepAreaRenderer renderer = new XYStepAreaRenderer(XYStepAreaRenderer.AREA);
				
				renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				
				((XYPlot)chart.getPlot()).setRenderer(myplot.getDatasetId(), renderer, false);
			}
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
	
    private TickUnits getUnits(CalendarFieldSplitPeriod period){
    	int field = period.getField();
    	for( DateTickUnitType unit : new DateTickUnitType[]{ DateTickUnitType.SECOND,DateTickUnitType.MINUTE,DateTickUnitType.HOUR,DateTickUnitType.DAY,DateTickUnitType.MONTH,DateTickUnitType.YEAR}){
    		if( field == unit.getCalendarField()){
    			TickUnits units = new TickUnits();
    			int count = period.getCount();
    			int nsplit = period.getNsplit();
    			if( count == 1 && nsplit == 1){
    				return null; // let jfree work it out.
    			}
    			if( nsplit > 50 ){
    				return null;  // period unit is too small
    			}
				// include all multiples of period that divide count
				for( int i = 1; i<= count; i++){
					if( count % i == 0){
						units.add(new DateTickUnit(unit, i));
					}
				}
				
				
				// now larger multiples of count that factor nsplit
				for( int i=2 ; i< nsplit && i < 50 ; i++){
					if( nsplit % i == 0 ){
						units.add(new DateTickUnit(unit, i*count));
					}
				}
				return units;
    		}
    	}
    	return null;
    }

	private TimeChartDataSet addTimeSeries(TimeChartDataSet dataset) throws InvalidArgument {
		if( dataset == null){
			dataset = makeDataSet(1);
		}
		
		if( chart == null ){
			chart = ChartFactory.createTimeSeriesChart(title, "Time", quantity, dataset, true, false, false); 
			XYPlot xyPlot = (XYPlot)chart.getPlot();
			
			DateAxis axis= (DateAxis) xyPlot.getDomainAxis();
			//axis.setRange(period.getStart(), period.getEnd());
			//axis.setLowerMargin(0.0);
			//axis.setUpperMargin(0.0);
			
			if( period instanceof CalendarFieldSplitPeriod){
				TickUnits u = getUnits((CalendarFieldSplitPeriod)period);
				if(u != null ){
					axis.setStandardTickUnits(u);
				}
			}
			LegendTitle leg = chart.getLegend();
			leg.setSortOrder(SortOrder.DESCENDING);
			leg.setPosition(RectangleEdge.RIGHT);
		}else{
			XYPlot xyPlot = (XYPlot)chart.getPlot();
			xyPlot.setDataset(ndatasets, dataset);
		}
		dataset.setDatasetId(ndatasets);
		ndatasets++;
		return dataset;
	}

	public TimeChartDataSet addAreaGraph(TimeChartDataSet plot,
			Color[] custom_colours) throws InvalidArgument {
		TimeChartDataSet ds = addAreaGraph(plot);
		setColours(custom_colours, ds);
		return ds;
	}



	protected void setColours(Color[] custom_colours, TimeChartDataSet ds) {
		if( custom_colours == null ){
			return;
		}
		XYItemRenderer rend = ((XYPlot)chart.getPlot()).getRenderer(ds.getDatasetId());
		if( rend == null ){
			rend = ((XYPlot)chart.getPlot()).getRenderer();
		}
		if( rend == null ){
			return;
		}
		for(int i=0; i< custom_colours.length;i++){
			if( custom_colours[i] != null){
				rend.setSeriesPaint(i, custom_colours[i]);
			}
		}
	}

	public TimeChartDataSet addLineGraph(TimeChartDataSet plot) throws InvalidArgument {
		TimeChartDataSet myplot = addTimeSeries(plot);
		if( use_bar || getItems() < STEP_THRESHOLD || use_step){
			XYStepRenderer renderer = new XYStepRenderer();
			renderer.setAutoPopulateSeriesFillPaint(true);
			((XYPlot)chart.getPlot()).setRenderer(myplot.getDatasetId(), renderer, false);
		}else{
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
			renderer.setAutoPopulateSeriesFillPaint(true);
			((XYPlot)chart.getPlot()).setRenderer(myplot.getDatasetId(), renderer, false);
		}
		return myplot;
	}

	public TimeChartDataSet addLineGraph(TimeChartDataSet plot,
			Color[] custom_colors) throws InvalidArgument {
	
		TimeChartDataSet ds = addLineGraph(plot);
		setColours(custom_colors, ds);
		return ds;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#setPeriod(uk.ac.ed.epcc.webapp.time.SplitTimePeriod, int)
	 */
	public void setPeriod(SplitTimePeriod period, int nsplit) {
		this.period=period;
		if( nsplit <= 1){
			use_bar=true;
		}
		if( period.getNsplit() == 1){
			// old chart2D classes had to have 2 splits
			// keep minimum number of plots the same
			this.nsplits=2*nsplit;
		}else{
			this.nsplits=nsplit;
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.PeriodChartData#getPeriod()
	 */
	public TimePeriod getPeriod() {
		return period;
	}



	public int getItems(){
		return period.getNsplit()*nsplits;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#getPlots()
	 */
	public List<TimeChartDataSet> getPlots() {
		return plots;
	}
	@Override
	public void setQuantityName(String s) {
		super.setQuantityName(s);
		if( chart != null ){
			NumberAxis n_axis= (NumberAxis) ((XYPlot)chart.getPlot()).getRangeAxis();
			n_axis.setLabel(s);
		}
	}
	@Override
	public void setTitle(String s) {
		super.setTitle(s);
		if( chart != null ){
			chart.setTitle(s);
		}
	}



	/**
	 * @return the use_step
	 */
	public boolean useStep() {
		return use_step;
	}



	/**
	 * @param use_step the use_step to set
	 */
	public void setUseStep(boolean use_step) {
		this.use_step = use_step;
	}

}