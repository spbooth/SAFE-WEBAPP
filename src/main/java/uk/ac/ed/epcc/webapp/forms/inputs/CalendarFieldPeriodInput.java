//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;

 
/** Composite input for a CalendarFieldSplitPeriod
 * Sub inputs are:
 * <ul> 
 * <li> start - start-time
 * <li> field - {@link Calendar} field
 * <li> splits - number of sub-periods
 * <li> count - multiple of field in sub-period
 * </ul>
 * 
 * Default start time is midnight at the start of the previous month.
 * <p>
 * various time fields can 
 * be suppressed by passing a Calendar field value to the constructor. Only those fields greater or equal to this
 * value are shown. The year value is always shown.
 * <p>
 * Note we can use {@link #setStartTime(Calendar)} to set Calendar fields 
 * below the finest field shown in the input.
 * @author spb
 *
 */


public class CalendarFieldPeriodInput extends MultiInput<CalendarFieldSplitPeriod, Input> {

	private final BoundedDateInput start;
	private final Input<Integer> count;
	private final CalendarFieldInput field;
	private final IntegerInput splits;
	public static CalendarFieldPeriodInput getInstance(AppContext conn){
		return getInstance(conn,Calendar.SECOND);
	}
	public static CalendarFieldPeriodInput getInstance(AppContext conn,Date now){
		return getInstance(conn,now,Calendar.SECOND);
	}
	public static CalendarFieldPeriodInput getInstance(AppContext conn,int finest_field){
		return getInstance(conn,finest_field,0);
	}
	public static CalendarFieldPeriodInput getInstance(AppContext conn,Date now,int finest_field){
		return getInstance(conn,now,finest_field,0);
	}
	public static CalendarFieldPeriodInput getInstance(AppContext conn,int finest_field, int fixed_blocks){
		return getInstance(conn,null,finest_field,fixed_blocks);
	}
	public static CalendarFieldPeriodInput getInstance(AppContext conn,Date now,int finest_field, int fixed_blocks) {
		return new CalendarFieldPeriodInput(BoundedDateInput.getInstance(conn, finest_field),now, finest_field, fixed_blocks);
	}
	private CalendarFieldPeriodInput(BoundedDateInput date_input,Date now,int finest_field, int fixed_blocks){
		Calendar cal = Calendar.getInstance();
		if( now != null) {
			cal.setTime(now);
		}
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -1);
		start = date_input;
		start.setValue(cal.getTime());
		IntegerInput bare = new IntegerInput();
		bare.setMin(1);
		bare.setBoxWidth(2);
		bare.setMaxResultLength(3);
		if( fixed_blocks <= 0 ){
			bare.setValue(1);
			count=bare;
		}else{
			bare.setValue(fixed_blocks);
			count = new LockedInput<>(bare);
		}
		field = new CalendarFieldInput(finest_field);
		field.setValue(finest_field > Calendar.MONTH? Calendar.MONTH : finest_field);
		splits = new IntegerInput();
		splits.setMin(1);
		splits.setBoxWidth(2);
		splits.setMaxResultLength(3);
		splits.setMax(RegularPeriodInput.PERIOD_INPUT_MAX_SPLITS);
		splits.setValue(1);
		addInput("start", "From ", start);
		
		addInput("splits"," for " ,splits);
		addInput("count"," blocks of ",count);
		addInput("field", field);
	}
	public void setStartTime(Calendar c){
		start.setValue(c.getTime());
	}
	public Calendar getStartTime(){
		Date d = start.getValue();
		if( d == null ){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c;
	}
	public void setBlockField(int f){
		field.setValue(f);
	}
	public int getBlockField(){
		return field.getValue();
	}
	public void setSplitCount(int count){
		splits.setValue(count);
	}
	public int getSplitCount(){
		return splits.getValue();
	}
	public void setBlockCount(int n){
		count.setValue(n);
	}
	public int getBlockCount(){
		return count.getValue();
	}
	@Override
	public CalendarFieldSplitPeriod convert(Object v) throws TypeError {
		if( v == null ){
			return null;
		}
		if( v instanceof CalendarFieldSplitPeriod){
			return ((CalendarFieldSplitPeriod)v);
		}
		throw new TypeError(v.getClass());
	}

	@Override
	public CalendarFieldSplitPeriod getValue() {
		Date start_value = start.getValue();
		Integer field_value = field.getValue();
		Integer value = count.getValue();
		Integer splits_value = splits.getValue();
		if( start_value == null || field_value == null || value == null || splits_value == null){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(start_value);
		return new CalendarFieldSplitPeriod(c,field_value,value,splits_value);
	}

	@Override
	public CalendarFieldSplitPeriod setValue(CalendarFieldSplitPeriod v) throws TypeError {
		CalendarFieldSplitPeriod old = getValue();
		start.setValue(v.getStart());
		field.setValue(v.getField());
		count.setValue(v.getCount());
		splits.setValue(v.getNsplit());
		return old;
		
	}

	
}