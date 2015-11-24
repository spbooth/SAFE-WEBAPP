// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.time.Period;
@uk.ac.ed.epcc.webapp.Version("$Id: SimplePeriodInput.java,v 1.2 2014/09/15 14:30:21 spb Exp $")


public class SimplePeriodInput extends MultiInput<Period, TimeStampMultiInput> {

	private final TimeStampMultiInput start;
	private final TimeStampMultiInput end;
	
	public SimplePeriodInput(){
		this(1000L,Calendar.SECOND);
	}
	public SimplePeriodInput(long res,int finest_field){
		start = new TimeStampMultiInput(res,finest_field);
		end = new TimeStampMultiInput(res,finest_field);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		end.setValue(cal.getTime());
		cal.add(Calendar.MONTH, -1);
		start.setValue(cal.getTime());
		addInput("start", "From ", start);
		addInput("end", " Until ",end);
		setLineBreaks(true);
	}
	public Period convert(Object v) throws TypeError {
		if( v == null || v instanceof Period){
			return ((Period)v);
		}
		throw new TypeError(v.getClass());
	}
	public void setStartDate(Date d){
		start.setValue(d);
	}
	public void setEndDate(Date d){
		end.setValue(d);
	}
	@Override
	public Period getValue() {
		return new Period(start.getValue(),end.getValue());
	}

	@Override
	public Period setValue(Period v) throws TypeError {
		Period old = getValue();
		start.setValue(v.getStart());
		end.setValue(v.getEnd());
		return old;
		
	}

	@Override
	public void validate() throws FieldException {
		
		super.validate();
		if( start.getValue().after(end.getValue())){
			throw new ValidateException("start after end");
		}
	}

}