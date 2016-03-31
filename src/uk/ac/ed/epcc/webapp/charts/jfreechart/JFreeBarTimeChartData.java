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

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import uk.ac.ed.epcc.webapp.charts.BarTimeChartData;
import uk.ac.ed.epcc.webapp.charts.GenericSetPlot;
import uk.ac.ed.epcc.webapp.charts.PieChartData;
import uk.ac.ed.epcc.webapp.charts.PieTimeChartData;
import uk.ac.ed.epcc.webapp.time.TimePeriod;



public class JFreeBarTimeChartData extends JFreeChartData<GenericSetPlot> implements BarTimeChartData<GenericSetPlot> {

//	public static final Feature JFREE_3D_PIE = new Feature("jfreechat.3dpiechart", false,"Use 3D effecct on piecharts");
	GenericSetPlot ds;
	
	private TimePeriod period=null;
	public TimePeriod getPeriod() {
		return period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}
	public GenericSetPlot addPieChart(int nset) {
		ds= new GenericSetPlot(period,nset);
		return ds;
	}

	@Override
	public JFreeChart getJFreeChart() {
		CategoryDataset data = makeDataSet();
		

		
	
		JFreeChart chart = ChartFactory.createBarChart(title, 
				null, 
				quantity, 
				data, 
				PlotOrientation.VERTICAL,
				false, // include legends
				false, // tooltips
				false  // urls
				);
				
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		CategoryAxis axis = categoryPlot.getDomainAxis();
		Font tickLabelFont = axis.getTickLabelFont();
		axis.setTickLabelFont(tickLabelFont.deriveFont(tickLabelFont.getSize()-4.0F));
		Font labelFont = axis.getLabelFont();
		//axis.setLabelFont(labelFont.d);
		// axis.setLabel("Pingu"); This works so we can modify
			
		return chart;

	}

	protected CategoryDataset makeDataSet() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double counts[] = ds.getCounts();
		String legends[] = ds.getLegends();
		for (int i = 0; i < ds.getNumSets(); i++) {
			if (legends != null && legends.length > i) {
				dataset.addValue(new Double(counts[i]), "Series-1",legends[i] );
				//System.out.println(legends[i] + counts[i]);
			} else {
				dataset.addValue(new Double(counts[i]), "Series-1",Integer.toString(i) );
			}
		}
		return dataset;
	}

	public GenericSetPlot makeDataSet(int i) {
		return new GenericSetPlot(period,i);
	}

	public GenericSetPlot addBarChart(int nset, Color[] custom_colours) {
		// TODO support colors
		return addBarChart(nset);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.charts.BarTimeChartData#addBarChart(int)
	 */
	@Override
	public GenericSetPlot addBarChart(int nset) {
		ds= new GenericSetPlot(period,nset);
		return ds;
	}

}