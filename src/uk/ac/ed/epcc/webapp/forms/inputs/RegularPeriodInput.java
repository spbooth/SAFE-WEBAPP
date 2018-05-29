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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;



public class RegularPeriodInput extends MultiInput<RegularSplitPeriod, Input> {

	/**
	 * 
	 */
	public static final int PERIOD_INPUT_MAX_SPLITS = 500;
	private final TimeStampMultiInput start;
	private final TimeStampMultiInput end;
	private final IntegerInput splits;
	
	public RegularPeriodInput(){
		this(1000L,Calendar.SECOND);
	}
	public RegularPeriodInput(Date current_time){
		this(current_time,1000L,Calendar.SECOND);
	}
	public RegularPeriodInput(long res,int finest){
		this(null,res,finest);
	}
	public RegularPeriodInput(Date current_time,long res,int finest){
		start = new TimeStampMultiInput(res,finest);
		end = new TimeStampMultiInput(res,finest);
		splits = new IntegerInput();
		splits.setMin(2);
		Calendar cal = Calendar.getInstance();
		if( current_time != null) {
			// allowing the current time to be passed
			// in is needed to support time dependent tests.
			cal.setTime(current_time);
		}
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		end.setValue(cal.getTime());
		cal.add(Calendar.MONTH, -1);
		splits.setBoxWidth(2);
		splits.setMaxResultLength(3);
		splits.setValue(4);
		splits.setMin(0);
		splits.setMax(PERIOD_INPUT_MAX_SPLITS);
		start.setValue(cal.getTime());
		addInput("start", "Start Date", start);
		addInput("end", "End Date",end);
		addInput("splits","Number Of Sub Periods" ,splits);
	}
	public RegularSplitPeriod convert(Object v) throws TypeError {
		if( v == null || v instanceof RegularSplitPeriod){
			return ((RegularSplitPeriod)v);
		}
		throw new TypeError(v.getClass());
	}

	@Override
	public RegularSplitPeriod getValue() {
		
		Date start_value = start.getValue();
		Date end_value = end.getValue();
		Integer splits_value = splits.getValue();
		if( start_value == null || end_value == null || splits_value == null){
			return null;
		}
		return new RegularSplitPeriod(start_value,end_value,splits_value);
	}

	@Override
	public RegularSplitPeriod setValue(RegularSplitPeriod v) throws TypeError {
		RegularSplitPeriod old = getValue();
		start.setValue(v.getStart());
		end.setValue(v.getEnd());
		splits.setValue(v.getNsplit());
		return old;
		
	}
	public void setStartDate(Date d){
		start.setValue(d);
	}
	public void setEndDate(Date d){
		end.setValue(d);
	}
	public void setSplits(int n){
		splits.setValue(n);
	}
	@Override
	public void validate() throws FieldException {
		super.validate();
		if( start.getValue().after(end.getValue())){
			throw new ValidateException("start after end");
		}
	}

}