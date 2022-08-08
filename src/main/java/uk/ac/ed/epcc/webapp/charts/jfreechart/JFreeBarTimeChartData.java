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

import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import uk.ac.ed.epcc.webapp.charts.BarTimeChartData;
import uk.ac.ed.epcc.webapp.charts.GenericSetPlot;
import uk.ac.ed.epcc.webapp.time.TimePeriod;



public class JFreeBarTimeChartData extends JFreeChartData<GenericSetPlot> implements BarTimeChartData<GenericSetPlot> {

	private boolean is_stacked=false;
//	public static final Feature JFREE_3D_PIE = new Feature("jfreechat.3dpiechart", false,"Use 3D effecct on piecharts");
	//GenericSetPlot ds;
	LinkedHashMap<String,GenericSetPlot> series=new LinkedHashMap<>();
	private TimePeriod period=null;
	public TimePeriod getPeriod() {
		return period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}
	public GenericSetPlot getBarChartSeries(String name,int nset) {
		assert(name != null);
		GenericSetPlot ds= series.get(name);
		if( ds == null) {
				ds = new GenericSetPlot(period,nset);
				series.put(name, ds);
		}
		return ds;
	}

	@Override
	public JFreeChart getJFreeChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int set=0;
		int max_len=0;
		for(Entry<String, GenericSetPlot> e : series.entrySet()) {
			
			String name = e.getKey();
			GenericSetPlot ds = e.getValue();
			double counts[] = ds.getCounts();
			String legends[] = ds.getLegends();
			
			for (int i = 0; i < ds.getNumSets(); i++) {

				if (legends != null && legends.length > i) {
					dataset.addValue(new Double(counts[i]), name,legends[i] );
					int leg_len = legends[i].length();
					if( leg_len > max_len){
						max_len = leg_len;
					}
					//System.out.println(legends[i] + counts[i]);
				} else {
					dataset.addValue(new Double(counts[i]), name,Integer.toString(i) );
				}
			}
		}
		CategoryDataset data = dataset;
		

		
	
		boolean multi_series = series.size() > 1;
		JFreeChart chart;
		if( is_stacked ) {
			chart = ChartFactory.createStackedBarChart(title, 
					null, 
					quantity, 
					data, 
					PlotOrientation.VERTICAL,
					multi_series, // include legends
					false, // tooltips
					false  // urls
					);
		}else {
			chart = ChartFactory.createBarChart(title, 
				null, 
				quantity, 
				data, 
				PlotOrientation.VERTICAL,
				multi_series, // include legends
				false, // tooltips
				false  // urls
				);
		}
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		
		CategoryAxis axis = categoryPlot.getDomainAxis();
		int num_sets=0;
		for(GenericSetPlot ds : series.values()) {
			int i = ds.getNumSets();
			if( i > num_sets) {
				num_sets=i;
			}
		}
		if( max_len > 8 || num_sets > 16 || (max_len * num_sets) > 50 ){
			axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		}
		Font tickLabelFont = axis.getTickLabelFont();
		if( num_sets > 24){
		  axis.setTickLabelFont(tickLabelFont.deriveFont(tickLabelFont.getSize()-2.0F));
		}
		Font labelFont = axis.getLabelFont();
		axis.setMaximumCategoryLabelLines(3);
		//axis.setLabelFont(labelFont.d);
		// axis.setLabel("Pingu"); This works so we can modify
			
		return chart;

	}

	public GenericSetPlot makeDataSet(int i) {
		return new GenericSetPlot(period,i);
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.BarTimeChartData#isStacked()
	 */
	@Override
	public boolean isStacked() {
		return is_stacked;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.BarTimeChartData#setStacked()
	 */
	@Override
	public void setStacked(boolean value) {
		is_stacked=value;
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.BarTimeChartData#getSeries()
	 */
	@Override
	public Map<String, GenericSetPlot> getSeries() {
		return series;
	}

}