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
package uk.ac.ed.epcc.webapp.charts.chart2D;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.chart2d.Chart2DProperties;
import net.sourceforge.chart2d.GraphChart2DProperties;
import net.sourceforge.chart2d.GraphProperties;
import net.sourceforge.chart2d.LBChart2D;
import net.sourceforge.chart2d.LegendProperties;
import net.sourceforge.chart2d.MultiColorsProperties;
import net.sourceforge.chart2d.Object2DProperties;
import net.sourceforge.chart2d.WarningRegionProperties;
import uk.ac.ed.epcc.webapp.charts.TimeChartData;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;
import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;
import uk.ac.ed.epcc.webapp.time.SplitTimePeriod;
import uk.ac.ed.epcc.webapp.time.TimePeriod;


/** A {@link TimeChartData} implemented using the Chart2D package.
 * 
 * As the X axis labels are generated at the major boundaries of the dataset and are not
 * auto-adjusted we have to adjust the period splits to ensure a printable graph
 * @author spb
 *
 */
public class Chart2DTimeChartData extends Chart2DChartData<Chart2DSplitSetPlot> implements
		TimeChartData<Chart2DSplitSetPlot> {
	private static final int TOO_MANY_MAJOR = 12;
	SplitTimePeriod period;
	boolean force_multi=true;
	private boolean is_cumulative;
	int nsplit;
	private LinkedList<Chart2DSplitSetPlot> plots = new LinkedList<Chart2DSplitSetPlot>();
	// custom colours for use in line graphs
	private static Color custom[] = { Color.red, Color.blue, Color.yellow,
			Color.orange, Color.pink };

	public Chart2DTimeChartData() {

		// fill in default properties
		// we can retreive and modify them later if necessary.
		LBChart2D lb = new LBChart2D();
		Object2DProperties o2dprop = new Object2DProperties();
		Chart2DProperties c2dprop = new Chart2DProperties();
		LegendProperties lprop = new LegendProperties();
		lprop.setLegendExistence(false);
		GraphChart2DProperties g2dprop = new GraphChart2DProperties();
		c2dprop.setChartDataLabelsPrecision (-1);

		
		
		g2dprop.setLabelsAxisTitleText("Time");
		g2dprop.setLabelsAxisTicksOutlineExistence(false);
		g2dprop.setNumbersAxisTicksOutlineExistence(false);
		g2dprop.setLabelsAxisTicksAlignment(GraphChart2DProperties.BETWEEN);
		g2dprop.setLabelsAxisBetweenLabelsOrTicksGapExistence(false);
		// Rescale the vertical space:
		g2dprop.setChartGraphableToAvailableRatio(0.95f);
		lb.setObject2DProperties(o2dprop);
		lb.setChart2DProperties(c2dprop);
		lb.setLegendProperties(lprop);
		lb.setGraphChart2DProperties(g2dprop);
		setChart2D(lb);
	}
	public Chart2DSplitSetPlot addAreaGraph(Chart2DSplitSetPlot ds){
		return addAreaGraph(ds, null);
	}
	
	public Chart2DSplitSetPlot addAreaGraph(Chart2DSplitSetPlot ds, Color custom_colours[]) {
		LBChart2D lb = (LBChart2D) this.c2d;
		//TimeChart t = (TimeChart) getChart();
		GraphProperties gprop = new GraphProperties();
		if( useBars()){
			gprop.setGraphBarsExistence(true);
			gprop.setGraphLinesExistence(false);
		}else{
			gprop.setGraphBarsExistence(false);
			gprop.setGraphLinesExistence(true);
		}
		gprop.setGraphLinesFillInterior(true);
		gprop.setGraphLinesThicknessModel(2); // This is the line thickness
		gprop.setGraphAllowComponentAlignment(true);
		gprop.setGraphLinesWithinCategoryOverlapRatio(1f);
		gprop.setGraphNumbersLinesStyle(GraphProperties.DOTTED);
		gprop.setGraphOutlineComponentsExistence(false);
		MultiColorsProperties mcp = new MultiColorsProperties();
		if (custom_colours != null) {
			mcp.setColorsCustom(custom_colours);
			mcp.setColorsCustomize(true);
		}
		lb.addGraphProperties(gprop);
		lb.addDataset(ds.getDataset());
		lb.addMultiColorsProperties(mcp);
		plots.add(ds);
		return ds;
	}

	

	public Chart2DSplitSetPlot addLineGraph(Chart2DSplitSetPlot ds){
		return addLineGraph(ds, null);
	}

	public Chart2DSplitSetPlot addLineGraph(Chart2DSplitSetPlot ds, Color custom_colours[]) {
		if( custom_colours == null && ds.getNumSets() < custom.length){
			custom_colours = custom;
		}
		
		LBChart2D lb = (LBChart2D) this.c2d;
		//TimeChart t = (TimeChart) getChart();
		GraphProperties gprop = new GraphProperties();
		gprop.setGraphBarsExistence(false);
		gprop.setGraphLinesExistence(true);
		gprop.setGraphLinesFillInterior(false);
		gprop.setGraphLinesThicknessModel(2); // This is the line thickness
		gprop.setGraphAllowComponentAlignment(true);
		gprop.setGraphLinesWithinCategoryOverlapRatio(1f);
		gprop.setGraphNumbersLinesStyle(GraphProperties.DOTTED);
		gprop.setGraphOutlineComponentsExistence(false);
		MultiColorsProperties mcp = new MultiColorsProperties();
		mcp.setColorsType(MultiColorsProperties.NATURAL);

		if (custom_colours != null) {
			mcp.setColorsCustom(custom_colours);
			mcp.setColorsCustomize(true);
		}
		lb.addGraphProperties(gprop);
		lb.addDataset(ds.getDataset());
		lb.addMultiColorsProperties(mcp);
		plots.add(ds);
		return ds;
	}

	public Chart2DSplitSetPlot makeDataSet(int i) throws InvalidArgument {
		//TimeChart t = (TiChart2DSplitSetPlotmeChart) getChart();
		
		Chart2DSplitSetPlot p = new Chart2DSplitSetPlot(this,i,period,nsplit,force_multi);
		setPeriodLabels(p.getLabels());
		return p;
	}

	public void addWarningLevel(double value) {
		
		LBChart2D lb = (LBChart2D) this.c2d;
		GraphChart2DProperties graphChart2DProps = lb.getGraphChart2DProperties();
		WarningRegionProperties warningRegionProps1 = new WarningRegionProperties();
		warningRegionProps1.setHigh((float)value);
		warningRegionProps1.setLow( WarningRegionProperties.LOW );
		lb.addWarningRegionProperties (warningRegionProps1);
		graphChart2DProps.setChartDatasetCustomizeGreatestValue(true);
		graphChart2DProps.setChartDatasetCustomGreatestValue( 
				(float)value );
			 
	}
	public String[] getPeriodLabels() {
		LBChart2D c = (LBChart2D) c2d;
		return c.getGraphChart2DProperties().getLabelsAxisLabelsTexts();
	}

	public void setPeriodLabels(String[] lab) {
		LBChart2D c = (LBChart2D) c2d;
		c.getGraphChart2DProperties().setLabelsAxisLabelsTexts(lab);
	}
	String quantity_name;
	public void setQuantityName(String s) {
		quantity_name=s;
		LBChart2D c = (LBChart2D) c2d;
		c.getGraphChart2DProperties().setNumbersAxisTitleText(s);
	}

	public String getQuantityName(){
		return quantity_name;
	}
	public void removeGraph(Chart2DSplitSetPlot plot) {
		LBChart2D lb = (LBChart2D) this.c2d;
		lb.removeDataset(plot.getDataset());
		
	}
	protected boolean use_bars=false;
	protected boolean useBars(){
		return use_bars;
	}
	public void setUseBars(boolean val){
		use_bars=val;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#setPeriod(uk.ac.ed.epcc.webapp.time.TimePeriod, int)
	 */
	public void setPeriod(SplitTimePeriod p, int minor) {
		// In Chart2D the 
		setUseBars(minor <= 1);
		int split = p.getNsplit();
		if( split > TOO_MANY_MAJOR){
			if( p instanceof RegularSplitPeriod){
				// just scale back splits, boundary does not matter.
				RegularSplitPeriod rp = (RegularSplitPeriod) p;
				int mult = (split+TOO_MANY_MAJOR-1)/TOO_MANY_MAJOR;
				split = (split + mult -1)/mult;
				minor *= mult;
				p = new RegularSplitPeriod(p.getStart(), p.getEnd(), split);
			}else if( p instanceof CalendarFieldSplitPeriod){
				CalendarFieldSplitPeriod cp = (CalendarFieldSplitPeriod) p;
				// try dividing first to keep period
				int mult = getMult(split);
				if( split/mult <= TOO_MANY_MAJOR){
					minor *= mult;
					p = new CalendarFieldSplitPeriod(cp.getCalStart(),cp.getField(), cp.getCount()*mult, split/mult);
				}else{
					// extend period
					mult = (split+TOO_MANY_MAJOR-1)/TOO_MANY_MAJOR;
					split = (split + mult -1)/mult;
					minor *= mult;
					p = new CalendarFieldSplitPeriod(cp.getCalStart(),cp.getField(), cp.getCount()*mult, split);
				}
			}
		}
		this.period=p;
		this.nsplit=minor;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.PeriodChartData#getPeriod()
	 */
	public TimePeriod getPeriod() {
		return period;
	}
	static final int primes[] = {2,3,5,7,11,13};
	/** Find a factor of nmajor that reduces count to acceptible levels.
	 * 
	 * @param nmajor
	 * @return
	 */
	private static int getMult(int nmajor){
		int mult=1;
		int i=0;
		while(nmajor > TOO_MANY_MAJOR && i<primes.length){
			
			for(i=0;i<primes.length; i++){
				int p = primes[i];
				if( nmajor % p == 0 && (nmajor/p) >= TOO_MANY_MAJOR){
					mult *=p;
					nmajor /= p;
					break;
				}
			}
		}
		return mult;
	}
	/** in Chart2D the legends are part of the chart so we need to
	 * re-create this from the legends stored in the Plot objects.
	 * 
	 */
	protected void updateLegends(){
		LinkedList<String> l = new LinkedList<String>();
		for(Chart2DSplitSetPlot p : plots){
			String l2[]= p.getLegends();
			if( l2 != null && l2.length > 0){
				for(String s : l2){
					l.add(s);
				}
			}
		}
		setLegends(l.toArray(new String[l.size()]));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#getPlots()
	 */
	public List<Chart2DSplitSetPlot> getPlots() {
		return plots;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.ChartData#setGraphical(boolean)
	 */
	public void setGraphical(boolean val) {
		force_multi=val;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#setCumulative(boolean)
	 */
	@Override
	public void setCumulative(boolean value) {
		is_cumulative=value;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.TimeChartData#isCumulative()
	 */
	@Override
	public boolean isCumulative() {
		return is_cumulative;
	}
}