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

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.charts.strategy.LabelledSetRangeMapper;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlPrinter;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.TableXMLFormatter;
/** Superclass for Chart tests
 * 
 * @author spb
 *
 */
public abstract class ChartTestCase extends WebappTestBase {
    
	/** Tests charts by adding Calendar objects for each Day in the period
     * @param <P> type of plot object
     * 
     * @param c
     * @param p 
	 * @throws Exception 
     */
	public <P extends PeriodPlot> void loadData(PeriodChart<P> c, P p) throws Exception{
		loadData(c, p, 1.0F);
	}
	public <P extends PeriodPlot> void loadData(PeriodChart<P> c, P p,float scale) throws Exception{	
	  Date s = c.getStartDate();
	  Date e = c.getEndDate();
	  
	  Calendar d = Calendar.getInstance();
	  Calendar end = Calendar.getInstance();
	  d.setTime(s);
	  end.setTime(e);
	  LabelledSetRangeMapper<Calendar> f = new DayTransform(scale);
	  while( d.before(end)){
		  c.addData(p, f, d);
		  d.add(Calendar.DAY_OF_YEAR, 1);
	  }
	  p.setLegends(f.getLabels().toArray(new String[0]));
	  c.getChartData().setQuantityName("Days");
	  
  }
	public static class DayTransform implements LabelledSetRangeMapper<Calendar>{
		/**
		 * @param scale
		 */
		public DayTransform(float scale) {
			super();
			this.scale = scale;
		}

		private final float scale;
		@Override
		public int getSet(Calendar c) {
			return c.get(Calendar.DAY_OF_WEEK);
		}

		public boolean after(Calendar c, Date point) {
			return point.before(c.getTime());
		}

		public boolean before(Calendar c, Date point) {
			return point.after(c.getTime());
		}

		@Override
		public float getOverlapp(Calendar c, Date start, Date end) {
			Calendar e = (Calendar) c.clone();
			e.add(Calendar.DAY_OF_YEAR, 1);
			long value = e.getTimeInMillis() - c.getTimeInMillis();
			long s = c.getTimeInMillis();
			long f = e.getTimeInMillis();
			if( start.after(c.getTime())){
				s = start.getTime();
			}
			if( end.before(e.getTime())){
				f = end.getTime();
			}
			if( s >= f){
			   return 0;
			}
			return scale * (float)(f-s)/(float)(value);
		}

		@Override
		public boolean overlapps(Calendar c, Date start, Date end) {
			Calendar e = (Calendar) c.clone();
			e.add(Calendar.DAY_OF_YEAR, 1);
			return e.getTime().after(start) && c.getTime().before(end);
		}

		@Override
		public Vector<String> getLabels() {
			Vector<String> v = new Vector<>();
			v.add("Noday");
			v.add("Sunday");
			v.add("Monday");
			v.add("Tuesday");
			v.add("Wednesday");
			v.add("Thursday");
			v.add("Friday");
			v.add("Saturday");
			return v;
		}

		@Override
		public int nSets() {
			return 8;
		}
		
	}
	public void checkTable(Table t){
		HtmlPrinter hb = new HtmlBuilder();
		TableXMLFormatter fmt = new TableXMLFormatter(hb, null,"auto");
		fmt.add(t);
		System.out.println(hb.toString());
	}
	
}