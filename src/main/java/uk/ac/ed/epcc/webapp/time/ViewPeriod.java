//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.time;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.util.Supplier;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link TimePeriod} corresponding to the standard {@link CalendarFieldSplitPeriod}
 * representation. This is usually used as part of the target for transitions with
 * a time-period view-port
 * @author Stephen Booth
 *
 */
public class ViewPeriod extends CalendarFieldSplitPeriod {
	protected static final String SEPERATOR = "-";

	
	
	public ViewPeriod(CalendarFieldSplitPeriod p){
		this(p.getCalStart(),p.getField(),p.getCount(),p.getNsplit());
	}

	public ViewPeriod(Calendar start, int field, int block, int num_periods) {
		super(start,field,block,num_periods);
	}

	
	
	
	public final int getBlock(){
		return getNsplit();
	}
	public final int getNumPeriods(){
		return getCount();
	}
	
	public ViewPeriod up(){
		Calendar c = (Calendar) getCalStart().clone();
		c.add(getField(), getCount());
		return create(c,getField(),getCount(),getNsplit());
	}
	public ViewPeriod down(){
		Calendar c = (Calendar) getCalStart().clone();
		c.add(getField(), - getCount());
		return create(c,getField(),getCount(),getNsplit());
	}
	/** create a new object of the same type as this one.
	 * 
	 * This is needed so sub-classes can create new objects of the corret type
	 * @param c
	 * @param field
	 * @param block
	 * @param num_periods
	 * @return
	 */
	protected ViewPeriod create(Calendar c, int field, int block, int num_periods) {
		return new ViewPeriod(c,field,block,num_periods);
	}
	public static ViewPeriod parsePeriod(String id){
		//System.out.println("<"+id+">");
		String tags[] = id.split(SEPERATOR);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(tags[0]));
		return new ViewPeriod(
		c,
		Integer.parseInt(tags[1]),
		Integer.parseInt(tags[2]),
		Integer.parseInt(tags[3]));
	}
	public String toString(){
		return getCalStart().getTimeInMillis()+SEPERATOR+getField()+SEPERATOR+getCount()+SEPERATOR+getNsplit();
	}
	
	
	

	public static Number getCalendarMonths(TimePeriod period){
		int full_months=0;
		double fraction=0.0;
		Calendar start=Calendar.getInstance();
		Date start_date = period.getStart();
		start.setTime(start_date);
		start.set(Calendar.MILLISECOND,0);
		start.set(Calendar.SECOND,0);
		start.set(Calendar.MINUTE,0);
		start.set(Calendar.HOUR_OF_DAY,0);
		start.set(Calendar.DAY_OF_MONTH, 1);
		Date month_start=start.getTime();
		if( month_start.before(start_date)){
			// fraction at start
			start.add(Calendar.MONTH, 1);
			
			fraction += (
					((double) (start.getTimeInMillis() - start_date.getTime())) /
					((double) (start.getTimeInMillis() - month_start.getTime())));
		}
		Calendar end = Calendar.getInstance();
		Date end_date = period.getEnd();
		end.setTime(end_date);
		end.set(Calendar.MILLISECOND,0);
		end.set(Calendar.SECOND,0);
		end.set(Calendar.MINUTE,0);
		end.set(Calendar.HOUR_OF_DAY,0);
		end.set(Calendar.DAY_OF_MONTH, 1);
		if( end.before(start)) {
			full_months--;
		}else {
			while(start.before(end)){
				full_months++;
				start.add(Calendar.MONTH, 1);
			}
		}
		Date month_end = end.getTime();
		if( month_end.before(end_date) ){
			// fraction at end
			end.add(Calendar.MONTH,1);
			fraction += (
					((double) (end_date.getTime() - month_end.getTime())) /
					((double) (end.getTimeInMillis() - month_end.getTime())));
		}
		if( fraction == 0.0 ){
			return Integer.valueOf(full_months);
		}
		return Double.valueOf(full_months+fraction);
		
	}
	/** Generate a ViewPeriod matching a specified {@link Period}
	 * 
	 * If this is not possible the default period is returned
	 * 
	 * @param conn
	 * @param p
	 * @return
	 */
	public static ViewPeriod getViewPeriod(AppContext conn,TimePeriod p,Supplier<ViewPeriod> def) {
		if( p instanceof ViewPeriod) {
			return (ViewPeriod) p;
		}
		if( p instanceof CalendarFieldSplitPeriod) {
			return new ViewPeriod((CalendarFieldSplitPeriod)p);
		}
		SplitPeriod split = SplitPeriod.getInstance(p.getStart(), p.getEnd());
		if( split instanceof CalendarFieldSplitPeriod) {
			return new ViewPeriod((CalendarFieldSplitPeriod) split);
		}
		return def.get();
	}

	
}
