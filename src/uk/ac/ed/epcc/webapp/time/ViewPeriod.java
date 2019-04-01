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

/** A {@link TimePeriod} corresponding to the standard {@link CalendarFieldSplitPeriod}
 * representation. This is usually used as part of the target for transitions with
 * a time-period view-port
 * @author Stephen Booth
 *
 */
public class ViewPeriod implements TimePeriod {
	protected static final String SEPERATOR = "-";
	
	protected final Calendar start;
	protected final int field;
	protected final int block;
	protected final int num_periods;
	
	
	public ViewPeriod(ViewPeriod p){
		this(p.start,p.field,p.block,p.num_periods);
	}

	public ViewPeriod(Calendar start, int field, int block, int num_periods) {
		this.start = (Calendar) start.clone();
		this.field = field;
		this.block = block;
		this.num_periods=num_periods;
	}

	
	  
	public final Date getStart() {
		  return start.getTime();
	  }

	public final Date getEnd() {
	   Calendar cal = (Calendar) start.clone();
	   cal.add(field,num_periods*block);
	   return cal.getTime();
	}
	public final int getField(){
		return field;
	}
	public final int getBlock(){
		return block;
	}
	public final int getNumPeriods(){
		return num_periods;
	}
	
	public final ViewPeriod up(){
		Calendar c = (Calendar) start.clone();
		c.add(field, block);
		return create(c,field,block,num_periods);
	}
	public final ViewPeriod down(){
		Calendar c = (Calendar) start.clone();
		c.add(field, - block);
		return create(c,field,block,num_periods);
	}
	protected ViewPeriod create(Calendar c, int field, int block, int num_periods) {
		return new ViewPeriod(c,field,block,num_periods);
	}
	public static ViewPeriod parsePeriod(String id){
		//System.out.println("<"+id+">");
		String tags[] = id.split(SEPERATOR);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(tags[1]));
		return new ViewPeriod(
		c,
		Integer.parseInt(tags[2]),
		Integer.parseInt(tags[3]),
		Integer.parseInt(tags[4]));
	}
	public String toString(){
		return start.getTimeInMillis()+SEPERATOR+field+SEPERATOR+block+SEPERATOR+num_periods;
	}
	@Override
	public int hashCode() {
		return start.hashCode()+field+block;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ViewPeriod){
			ViewPeriod p = (ViewPeriod) obj;
			return start.equals(p.start)  &&
			   field==p.field && block == p.block && num_periods == p.num_periods;
		}
		return false;
	}
	public final ViewPeriod[] getSubPeriods(){
		ViewPeriod result[] = new ViewPeriod[num_periods];
		Calendar c = (Calendar) start.clone();
		for(int i=0 ;i < num_periods ; i++){
			result[i] = create( c, field, block, 1);
			c.add(field,block);
		}
		return result;
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
	

}
