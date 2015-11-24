/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.charts.chart2D.Chart2DChartData;
import uk.ac.ed.epcc.webapp.content.Table;

public class TimeChartTest extends ChartTestCase {
	

	@SuppressWarnings("unchecked")
	@Test
	public void testTimeChart() throws Exception{
		  Calendar d = Calendar.getInstance();
		  d.set(2006, Calendar.JANUARY, 1);
		  Calendar e = (Calendar) d.clone();
		  e.add(Calendar.YEAR, 1);
		  TimeChart tc = TimeChart.getInstance(ctx, d, e, Calendar.MONTH, 3,  10);
		 PeriodSequencePlot plot = tc.addAreaGraph(8);
		  loadData(tc, plot);
		  plot.doConvertToStacked();
		  int nsets = plot.getNumSets();
		  System.out.println(nsets);
		  String legs[] = ((Chart2DChartData)tc.getChartData()).getLegends();
		ChartData<?> c = tc.getChartData();
		c.createPNG(new File(System.getProperty("java.io.tmpdir")+"/daystrip"+".png"));
		  c.getSize();
		  //tc.createSVG("/tmp/daystrip2");
		  Table t = tc.getTable();
		  t.setKeyName("Day of week");
		  checkTable(t);
		 
		  
	  }
}
