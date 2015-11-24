// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import java.awt.Color;
import java.awt.GradientPaint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import uk.ac.ed.epcc.webapp.charts.GenericSetPlot;
import uk.ac.ed.epcc.webapp.charts.PieChartData;
@uk.ac.ed.epcc.webapp.Version("$Id: JFreePieChartData.java,v 1.13 2014/09/15 14:30:13 spb Exp $")


public class JFreePieChartData extends JFreeChartData<GenericSetPlot> implements PieChartData<GenericSetPlot> {

//	public static final Feature JFREE_3D_PIE = new Feature("jfreechat.3dpiechart", false,"Use 3D effecct on piecharts");
	GenericSetPlot ds;
	

	public GenericSetPlot addPieChart(int nset) {
		ds= new GenericSetPlot(nset);
		return ds;
	}

	@Override
	public JFreeChart getJFreeChart() {
		DefaultPieDataset pieDataset = makeDataSet();
		

		
	
		JFreeChart chart = ChartFactory.createPieChart(quantity, // Title
				pieDataset, // Dataset
				true // Show legend
				, false, false);
		
			
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

	public GenericSetPlot makeDataSet(int i) {
		return new GenericSetPlot(i);
	}

	public GenericSetPlot addPieChart(int nset, Color[] custom_colours) {
		// TODO support colors
		return addPieChart(nset);
	}

}