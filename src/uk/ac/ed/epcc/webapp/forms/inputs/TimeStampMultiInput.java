// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.text.DateFormat;
import java.text.NumberFormat;
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
@uk.ac.ed.epcc.webapp.Version("$Id: TimeStampMultiInput.java,v 1.5 2014/09/15 14:30:21 spb Exp $")

public class TimeStampMultiInput extends MultiInput<Date,Input<Integer>> implements ParseInput<Date> {
	private final IntegerRangeInput hour_input;
	private final IntegerRangeInput min_input;
	private final IntegerRangeInput sec_input;
	private final IntegerInput year_input;
    private final CalendarMonthInput month_input;
    private final IntegerRangeInput day_input;
    private final long resolution;

    private final DateFormat df;

    private Calendar c=null;
    private Date min_date=null;
    private Date max_date=null;
    public TimeStampMultiInput(){
    	this(1000L,Calendar.SECOND);
    }
    public TimeStampMultiInput(long resolution,int max_field){
    	this.resolution=resolution;
    	//df = new RelativeDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	NumberFormat nf = NumberFormat.getIntegerInstance();
    	nf.setMinimumIntegerDigits(2);
    	nf.setMaximumIntegerDigits(2);
    	StringBuilder time_format = new StringBuilder();
    	if( max_field >= Calendar.HOUR){
    		hour_input = new IntegerRangeInput(0, 24);
    		hour_input.setNumberFormat(nf);
    		addInput("hour",hour_input);
    		time_format.append("HH");
    	}else{
    		hour_input=null;
    	}
    	if( max_field >= Calendar.MINUTE){
    		min_input = new IntegerRangeInput(0, 60);
    		min_input.setNumberFormat(nf);
    		addInput("min",":",min_input);
    		time_format.append(":mm");
    	}else{
    		min_input=null;
    	}
    	if( max_field >= Calendar.SECOND){
    		sec_input = new IntegerRangeInput(0, 60);
    		sec_input.setNumberFormat(nf);
    		addInput("sec",":",sec_input);
    		time_format.append(":ss");
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
    	year_input.setMaxResultLength(4);
    	year_input.setBoxWidth(4);
    	addInput("year", year_input);
    	
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
	private Calendar makeCalendar(){
		Calendar cal=Calendar.getInstance();
    	cal.set(Calendar.MILLISECOND,0);
    	cal.set(Calendar.SECOND,0);
    	cal.set(Calendar.MINUTE,0);
    	cal.set(Calendar.HOUR_OF_DAY,0);
    	cal.set(Calendar.DAY_OF_MONTH,1);
    	cal.set(Calendar.MONTH,0);
    	return cal;
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
	protected void setInputsFromCalendar(Calendar in) throws TypeError {
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
			c=null;
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

	
}