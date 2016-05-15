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


public class TimeStampMultiInput extends AbstractCalendarMultiInput implements ParseInput<Date>, BoundedInput<Date> {
	private final long resolution;

    private final DateFormat df;

    Calendar c=null;
    private Date min_date=null;
    private Date max_date=null;
    public TimeStampMultiInput(){
    	this(1000L,Calendar.SECOND);
    }
    public TimeStampMultiInput(long resolution,int max_field){
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
    	df = new RelativeDateFormat(date_format.toString());
    	
    }
	public Date convert(Object v) throws TypeError {
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
				throw new TypeError("Invalid date "+v);
			}
		}
		throw new TypeError(v.getClass());
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
	public final Date setValue(Date v) throws TypeError {
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
		return old;
	}
	public Date setMaxDate(Date d){
		Date old = max_date;
		max_date=d;
		return old;
	}
	public void parse(String v) throws ParseException {
		if (v == null) {
			setValue(null);
			return;
		}
		v = v.trim();
		if (v.length() == 0) {
			setValue(null);
			return;
		}
		Date d=null;
		try {
			d=df.parse(v);
		} catch (Exception e){
			throw new ParseException(e);
		}
		setValue(d);
		try {
			validate();
		} catch (FieldException e) {
			throw new ParseException("parsed values not in range");
		}
		
	}
	@Override
	public String getString(Date val) {
		return df.format(val);
	}
	@Override
	public void validate() throws FieldException {
		super.validate();
		Date val = getValue();
		if( min_date != null && min_date.after(val)){
			throw new ValidateException("Before "+df.format(min_date));
		}
		if( max_date != null && max_date.before(val)){
			throw new ValidateException("After "+df.format(max_date));
		}
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
	protected void setNull() {
		c=null;
	}

	
}