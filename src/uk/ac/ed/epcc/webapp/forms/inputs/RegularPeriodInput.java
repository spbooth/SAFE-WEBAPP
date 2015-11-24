// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;
@uk.ac.ed.epcc.webapp.Version("$Id: RegularPeriodInput.java,v 1.3 2014/09/15 14:30:20 spb Exp $")


public class RegularPeriodInput extends MultiInput<RegularSplitPeriod, Input> {

	private final TimeStampMultiInput start;
	private final TimeStampMultiInput end;
	private final IntegerInput splits;
	
	public RegularPeriodInput(){
		this(1000L,Calendar.SECOND);
	}
	public RegularPeriodInput(long res,int finest){
		start = new TimeStampMultiInput(res,finest);
		end = new TimeStampMultiInput(res,finest);
		splits = new IntegerInput();
		splits.setMin(2);
		Calendar cal = Calendar.getInstance();
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