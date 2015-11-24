/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.content.Table;

public class PieChartTest extends ChartTestCase {


@SuppressWarnings("unchecked")
@Test
public void testPie() throws Exception{
	  Calendar d = Calendar.getInstance();
	  d.set(2006, Calendar.JANUARY, 1);
	  Calendar e = (Calendar) d.clone();
	  e.add(Calendar.YEAR, 1);
	  PieTimeChart pie = PieTimeChart.getInstance(ctx, d, e);
	  PeriodSetPlot plot = pie.addPieChart(8);
	  loadData(pie, plot);
	ChartData<?> c = pie.getChartData();
	c.createPNG(new File(System.getProperty("java.io.tmpdir")+"/daypie"+".png"));
	  c.getSize();
	  //pie.createSVG("/tmp/daypie2");
	  Table t = pie.getTable();
	  t.setKeyName("Day of week");
	  checkTable(t);
	  
	  
  }
}
