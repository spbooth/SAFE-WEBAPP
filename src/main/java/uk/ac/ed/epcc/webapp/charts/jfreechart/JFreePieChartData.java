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
import java.util.LinkedList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.DefaultPieDataset;

import uk.ac.ed.epcc.webapp.charts.GenericSetPlot;
import uk.ac.ed.epcc.webapp.charts.PieTimeChartData;
import uk.ac.ed.epcc.webapp.time.TimePeriod;



public class JFreePieChartData extends JFreeChartData<GenericSetPlot> implements PieTimeChartData<GenericSetPlot> {

//	public static final Feature JFREE_3D_PIE = new Feature("jfreechat.3dpiechart", false,"Use 3D effecct on piecharts");
	GenericSetPlot ds;
	LinkedList<Color> colours=new LinkedList<>();
	private TimePeriod period=null;
	@Override
	public TimePeriod getPeriod() {
		return period;
	}

	@Override
	public void setPeriod(TimePeriod period) {
		this.period = period;
	}
	@Override
	public GenericSetPlot addPieChart(int nset) {
		ds= new GenericSetPlot(period,nset);
		return ds;
	}

	@Override
	public JFreeChart getJFreeChart() {
		DefaultPieDataset pieDataset = makeDataSet();
		

		
	
		JFreeChart chart = ChartFactory.createPieChart(title, // Title
				pieDataset, // Dataset
				true // Show legend
				, false, false);
		PiePlot plot = (PiePlot) chart.getPlot();
		
		StandardPieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0} ({2})");
		
		plot.setLabelGenerator(gen);
		plot.setMaximumLabelWidth(0.07);
		plot.setLabelFont(plot.getLabelFont().deriveFont(9.0F));
		
		
		LegendTitle leg = chart.getLegend();
		
		leg.setPosition(RectangleEdge.RIGHT);
		if(! colours.isEmpty()){
			String legends[] = ds.getLegends();
			
			for (int i = 0; i < ds.getNumSets(); i++) {
				if (legends != null && legends.length > i && colours.size() > i) {
					Color c = colours.get(i);
					plot.setSectionPaint(legends[i], c);
				}
			}
		}
			
		return chart;

	}

	protected DefaultPieDataset makeDataSet() {
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		double counts[] = ds.getCounts();
		String legends[] = ds.getLegends();
		for (int i = 0; i < ds.getNumSets(); i++) {
			if (legends != null && legends.length > i) {
				pieDataset.setValue(legends[i], new Double(counts[i]));
				//System.out.println(legends[i] + counts[i]);
			} else {
				pieDataset.setValue(new Integer(i), new Double(counts[i]));
			}
		}
		return pieDataset;
	}

	@Override
	public GenericSetPlot makeDataSet(int i) {
		return new GenericSetPlot(period,i);
	}

	@Override
	public GenericSetPlot addPieChart(int nset, Color[] custom_colours) {
		if( custom_colours != null){
			for(Color c : custom_colours){
				colours.add(c);
			}
		}
		return addPieChart(nset);
	}

}