/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.content.Table;

public class BarChartTest extends ChartTestCase {
	 

	@SuppressWarnings("unchecked")
	@Test
	public void testBar() throws Exception{
		  Calendar d = Calendar.getInstance();
		  d.set(2006, Calendar.JANUARY, 1);
		  Calendar e = (Calendar) d.clone();
		  e.add(Calendar.YEAR, 1);
		  BarTimeChart bar = BarTimeChart.getInstance(ctx, d, e);
		  PeriodSetPlot plot = bar.addBarChart(8);
		  loadData(bar, plot);
		  System.out.println("Size is "+plot.getNumSets());
		ChartData<?> c = bar.getChartData();
		c.createPNG(new File(System.getProperty("java.io.tmpdir")+"/daybar"+".png"));
		  c.getSize();
		  //pie.createSVG("/tmp/daypie2");
		  Table t = bar.getTable();
		  t.setKeyName("Day of week");
		  checkTable(t);
		  //System.out.println(t.getHTML());
		  
	  }
	}