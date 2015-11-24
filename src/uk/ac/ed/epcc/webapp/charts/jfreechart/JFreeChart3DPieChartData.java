// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts.jfreechart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: JFreeChart3DPieChartData.java,v 1.3 2014/09/15 14:30:13 spb Exp $")
public class JFreeChart3DPieChartData extends JFreePieChartData {

	/**
	 * 
	 */
	public JFreeChart3DPieChartData() {
	}

	@Override
	public JFreeChart getJFreeChart() {
		DefaultPieDataset pieDataset = makeDataSet();
		JFreeChart chart = ChartFactory.createPieChart3D(quantity, // Title
		pieDataset, // Dataset
		true // Show legend
		, false, false);
		
		return chart;
		
	}

}
