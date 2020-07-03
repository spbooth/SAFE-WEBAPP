//| Copyright - The University of Edinburgh 2015                            |
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
		  
		  PeriodSetPlot plot = bar.getBarChartSeries("Series-1",8);
		  loadData(bar, plot);
		  System.out.println("Size is "+plot.getNumSets());
		ChartData<?> c = bar.getChartData();
		File pngfile = new File(System.getProperty("java.io.tmpdir")+"/daybar"+".png");
		c.createPNG(pngfile);
		  c.getSize();
		  //pie.createSVG("/tmp/daypie2");
		  Table t = bar.getTable();
		  t.setKeyName("Day of week");
		  checkTable(t);
		  //System.out.println(t.getHTML());
		  pngfile.delete();
		  
	  }
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMultiBar() throws Exception{
		  Calendar d = Calendar.getInstance();
		  d.set(2006, Calendar.JANUARY, 1);
		  Calendar e = (Calendar) d.clone();
		  e.add(Calendar.YEAR, 1);
		  BarTimeChart bar = BarTimeChart.getInstance(ctx, d, e);
		  PeriodSetPlot plot = bar.getBarChartSeries("Series-1",8);
		  loadData(bar, plot);
		  
		  PeriodSetPlot plot2 = bar.getBarChartSeries("Series-2",8);
		  loadData(bar, plot2,2.0F);
		  System.out.println("Size is "+plot.getNumSets());
		ChartData<?> c = bar.getChartData();
		c.createPNG(new File(System.getProperty("java.io.tmpdir")+"/daymultibar"+".png"));
		  c.getSize();
		  //pie.createSVG("/tmp/daypie2");
		  Table t = bar.getTable();
		  t.setKeyName("Day of week");
		  checkTable(t);
		  //System.out.println(t.getHTML());
		  
	  }
	}