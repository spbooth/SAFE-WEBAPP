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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
/** An input that selects a timestamp using pull down menus.
 * The class also implements ParseInput to support setting 
 * default values.
 * <p>
 * various time fields can 
 * be suppressed by passing a Calendar field value to the constructor. Only those fields greater or equal to this
 * value are shown. The year value is always shown.
 * <p>
 * Values for the suppressed fields will be remembered by the input though not 
 * editable  by the UI.
 * @author spb
 *
 */


public class TimeStampMultiInput extends AbstractCalendarMultiInput implements BoundedDateInput {
	private final long resolution;

    private final DateFormat df;

    Calendar c=null;
    private Date min_date=null;
    private Date max_date=null;
    public TimeStampMultiInput(Date now){
    	this(now,1000L,Calendar.SECOND);
    }
    public TimeStampMultiInput(Date now,long resolution,int max_field){
    	super(max_field);
    	this.resolution=resolution;
    	//df = new RelativeDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	
    	StringBuilder time_format = new StringBuilder();
    	if( max_field >= Calendar.HOUR){
    		time_format.append("HH");
    	}
    	if( max_field >= Calendar.MINUTE){
    		time_format.append(":mm");
    	}
    	if( max_field >= Calendar.SECOND){
    		time_format.append(":ss");
    	}
    	
    	// Use ISO/Web date format for string
    	StringBuilder date_format = new StringBuilder();
    	if( max_field >= Calendar.DAY_OF_MONTH){
    		date_format.append("yyyy-MM-dd");
    	}else if( max_field >= Calendar.MONTH){
    		date_format.append("yyyy-MM");
    	}else{
    		date_format.append("yyyy");
    	}
    	
    	if( time_format.length() > 0 ){
    		date_format.append(" ");
    		date_format.append(time_format);
    	}
    	df = new RelativeDateFormat(now,date_format.toString());
    	addValidator(new FieldValidator<Date>() {
			
			@Override
			public void validate(Date val) throws FieldException {
				if( min_date != null && min_date.after(val)){
					if( (min_date.getTime() - val.getTime()) < 1000L  ) {
						// This is a boundary case min and value will format the same
						// even though min is technically after value
						throw new ValidateException("Must be after "+df.format(min_date));
					}
					throw new ValidateException("Before "+df.format(min_date));
				}
				if( max_date != null && max_date.before(val)){
					// boundary ok as will format as day before
					throw new ValidateException("After "+df.format(max_date));
				}
				
			}
		});
    }
	@Override
	public Date convert(Object v) throws TypeException {
		if( v == null ){
			return null;
		}
		if( v instanceof Date){
			return (Date) v;
		}
		if( v instanceof Calendar){
			return ((Calendar)v).getTime();
		}
		if( v instanceof Number){
			return new Date(((Number)v).longValue()*resolution);
		}
		if( v instanceof String){
			try {
				return df.parse((String)v);
			} catch (java.text.ParseException e) {
				throw new TypeException("Invalid date "+v);
			}
		}
		throw new TypeException(v.getClass());
	}

	@Override
	public final Date getValue() {
		// sync c with input fields
		
		c = setCalendarFromInputs(c);
		if( c == null){
			return null;
		}
		return c.getTime();
	}
	@Override
	public final Date setValue(Date v) throws TypeException {
		Date old = getValue();
		if( v != null){
			if( c == null ){
				// going to completely rewrite so don't need makeCalendar
				c=Calendar.getInstance();
			}
			c.setTime(v);
			setInputsFromCalendar(c);
		}else{
			setInputsFromCalendar(null);
			c=null;
		}
		return old;
	}
	public Date setMinDate(Date d){
		Date old = min_date;
		min_date=d;
		setBounds();
		return old;
	}
	public Date setMaxDate(Date d){
		Date old = max_date;
		max_date=d;
		setBounds();
		return old;
	}
	@Override
	public Date parseValue(String v) throws ParseException {
		if (v == null) {
			return null;
		}
		v = v.trim();
		if (v.length() == 0) {
			return null;
		}
		try {
			return df.parse(v);
		} catch (Exception e){
			throw new ParseException(e);
		}
	}
	@Override
	public String getString(Date val) {
		return df.format(val);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#getMin()
	 */
	@Override
	public Date getMin() {
		return min_date;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#getMax()
	 */
	@Override
	public Date getMax() {
		return max_date;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#formatRange(java.lang.Object)
	 */
	@Override
	public String formatRange(Date n) {
		return getString(n);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#setMin(java.lang.Object)
	 */
	@Override
	public Date setMin(Date val) {
		return setMinDate(val);
	}
	protected void setBounds() {
		if( min_date == null && max_date == null ) {
			year_input.setMin(null);
			month_input.setMin(null);
			day_input.setMin(null);
			year_input.setMax(null);
			month_input.setMax(null);
			day_input.setMax(null);
			return;
		}
		Calendar min_cal=null;
		Calendar max_cal=null;
		if( min_date != null) {
			min_cal = Calendar.getInstance();
			min_cal.setTime(min_date);
			year_input.setMin(min_cal.get(Calendar.YEAR));
		}else {
			year_input.setMin(null);
			month_input.setMin(null);
			day_input.setMin(null);
		}
		if( max_date != null) {
			max_cal = Calendar.getInstance();
			max_cal.setTime(max_date);
			year_input.setMax(max_cal.get(Calendar.YEAR));
		}else {
			year_input.setMax(null);
			month_input.setMax(null);
			day_input.setMax(null);
		}
		// if same year set month min/max
		if( min_cal != null && max_cal != null && min_cal.get(Calendar.YEAR) == max_cal.get(Calendar.YEAR)) {
			int min_month = min_cal.get(Calendar.MONTH);
			month_input.setMin(min_month);
			int max_month = max_cal.get(Calendar.MONTH);
			month_input.setMax(max_month);
			if(min_month == max_month) {
				day_input.setMin(min_cal.get(Calendar.DAY_OF_MONTH));
				day_input.setMax(max_cal.get(Calendar.DAY_OF_MONTH));
			}
			
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#setMax(java.lang.Object)
	 */
	@Override
	public Date setMax(Date val) {
		return setMaxDate(val);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractCalendarMultiInput#setNull()
	 */
	@Override
	public void setNull() {
		super.setNull();
		c=null;
	}
	@Override
	public void setDate(Date d) {
		try {
			setValue(d);
		} catch (TypeException e) {
			throw new TypeError(e);
		}
	}

}