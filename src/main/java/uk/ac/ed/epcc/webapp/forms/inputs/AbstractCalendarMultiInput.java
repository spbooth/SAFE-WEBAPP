//| Copyright - The University of Edinburgh 2016                            |
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

/** Abstract class to implement a time/date input as multi-input
 * @author spb
 *
 */
public abstract class AbstractCalendarMultiInput extends MultiInput<Date, Input<Integer>> implements ParseInput<Date>,BoundedInput<Date> {

	protected final int max_field;
	protected final IntegerRangeInput hour_input;
	protected final IntegerRangeInput min_input;
	protected final IntegerRangeInput sec_input;
	protected final IntegerInput year_input;
	protected final CalendarMonthInput month_input;
	protected final IntegerRangeInput day_input;

	/**
	 * 
	 */
	public AbstractCalendarMultiInput(int max_field) {
		super();
		this.max_field=max_field;

    	if( max_field >= Calendar.HOUR){
    		hour_input=new IntegerRangeInput(0, 24);
    		addInput("hour",hour_input);
    	}else{
    		hour_input=null;
    	}
    	if( max_field >= Calendar.MINUTE){
    		min_input = new IntegerRangeInput(0, 60);
    		addInput("min",":",min_input);
    	}else{
    		min_input=null;
    	}
    	if( max_field >= Calendar.SECOND){
    		sec_input = new IntegerRangeInput(0, 60);
    		addInput("sec",":",sec_input);
    	}else{
    		sec_input=null;
    	}
    	
    	
		if( max_field >= Calendar.DAY_OF_MONTH){
			String label="";
			if( hour_input != null){
				if( min_input == null ){
					label=":00 on ";
				}else{
					label=" on ";
				}
			}
    		day_input = new IntegerRangeInput(1, 31);
    		addInput("day", label,day_input);
    	}else{
    		day_input = null;
    	}
    	
    	if( max_field >= Calendar.MONTH){
    		month_input= new CalendarMonthInput();
    		addInput("month", month_input);
    	}else{
    		month_input=null;
    	}
    	year_input = new IntegerInput();
    	year_input.setBoxWidth(4);
    	addInput("year", year_input);
    	
	}

	protected Calendar makeCalendar() {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.MONTH,0);
		return cal;
	}

	/** edit a Calendar from the inputs. 
	 * Allocate a new Calendar if necessary
	 * 
	 * @param in
	 * @return modified object
	 */
	protected Calendar setCalendarFromInputs(Calendar in) {
		if( in != null ){
			// we don't store milliseconds so default to zero.
			in.set(Calendar.MILLISECOND, 0);
		}
		if( sec_input != null ){
			Integer sec_value = sec_input.getValue();
			if( sec_value != null ){
				if( in == null){
					in = makeCalendar();
				}
				in.set(Calendar.SECOND,sec_value);
			}
		}
		if( min_input != null ){
			Integer min_value = min_input.getValue();
			if( min_value != null ){
				if( in == null){
					in=makeCalendar();
				}
				in.set(Calendar.MINUTE,min_value);
			}
		}
		if( hour_input != null ){
			Integer hour_value = hour_input.getValue();
			if( hour_value != null ){
				if( in == null ){
					in=makeCalendar();
				}
				in.set(Calendar.HOUR_OF_DAY,hour_value);
			}
		}
		if( day_input != null ){
			Integer value = day_input.getValue();
			if( value != null){
				if( in== null){
					in=makeCalendar();
				}
				in.set(Calendar.DAY_OF_MONTH, value);
			}
		}
		if( month_input != null ){
			Integer value2 = month_input.getValue();
			if( value2 != null){
				if( in == null){
					in=makeCalendar();
				}
				in.set(Calendar.MONTH, value2);
			}
		}
		Integer value3 = year_input.getValue();
		if(value3 !=null){
			if( in == null){
				in=makeCalendar();
			}
			in.set(Calendar.YEAR, value3);
		}
		return in;
	}
   
	protected void setInputsFromCalendar(Calendar in) throws TypeException {
		if( in == null ){
			if( sec_input != null){
				sec_input.setValue(null);
			}
			if( min_input != null ){
				min_input.setValue(null);
			}
			if( hour_input != null ){
				hour_input.setValue(null);
			}
			if( day_input != null ){
				day_input.setValue(null);
			}
			if( month_input != null ){
				month_input.setValue(null);
			}
			year_input.setValue(null);
			setNull();
		}else{
			if( sec_input != null){
				sec_input.setValue(in.get(Calendar.SECOND));
			}
			if( min_input != null ){
				min_input.setValue(in.get(Calendar.MINUTE));
			}
			if( hour_input != null ){
				hour_input.setValue(in.get(Calendar.HOUR_OF_DAY));
			}
			if( day_input != null ){
				day_input.setValue(in.get(Calendar.DAY_OF_MONTH));
			}
			if( month_input != null ){
				month_input.setValue(in.get(Calendar.MONTH));
			}
			year_input.setValue(in.get(Calendar.YEAR));
		}
	}

	@Override
	public String getType() {
		// supress html5
		return null;
	}

}