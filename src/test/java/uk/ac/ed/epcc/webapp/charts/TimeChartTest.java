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
		  //String legs[] = ((Chart2DChartData)tc.getChartData()).getLegends();
		ChartData<?> c = tc.getChartData();
		File pngfile = new File(System.getProperty("java.io.tmpdir")+"/daystrip"+".png");
		c.createPNG(pngfile);
		  c.getSize();
		  //tc.createSVG("/tmp/daystrip2");
		  Table t = tc.getTable();
		  t.setKeyName("Day of week");
		  checkTable(t);
		 
		  pngfile.delete();
	  }
}