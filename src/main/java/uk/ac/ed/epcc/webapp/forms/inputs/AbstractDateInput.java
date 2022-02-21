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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;



/** A abstract superclass for text {@link Input}s that parse to java {@link Date} objects.
 * 
 * sub-classes may be constrained to generate values that are constrained to day/hour/min boundaries.
 * 
 * This class uses a series of {@link DateFormat}s applied in turn so as to support multiple formats
 * the first format should be the one compatible with the format defined in the standard for the corresponding Html5 input.
 * However  {@link #getHintIndex()} specifies the format that should be suggested to a user when
 * defaulting to a text input
 * 
 * @author spb
 *
 */
public abstract class AbstractDateInput extends ParseAbstractInput<Date> implements BoundedDateInput, FormatHintInput {
//	public static final Date DEFAULT_MAX_DATE;
//	static {
//		Calendar c = Calendar.getInstance();
//		c.clear();
//		c.set(9999, Calendar.DECEMBER, 31);
//		DEFAULT_MAX_DATE=c.getTime();
//	}
    Date min=null;
    Date max=null;
    long resolution=1000L; // number of milliseconds in a tick
    public AbstractDateInput(){
    	this(1000L);
    }
	public AbstractDateInput(long resolution) {
		super();
		setSingle(true);
		int length=0;
		String formats[] = getFormats();

		for(int i=0 ; i< formats.length;i++){

			if( formats[i].length() > length){
				length = formats[i].length();
			}

		}
		setBoxWidth(length);
		setMaxResultLength(length);
		addValidator(new FieldValidator<Date>() {
			
			@Override
			public void validate(Date value) throws FieldException {
				if( min != null && value.before(min)){
					throw new ValidateException("Value must be after "+getString(min));
				}
				if( max != null && value.after(max)){
					throw new ValidateException("Value must be before "+getString(max));
				}
				
			}
		});
	}
	protected DateFormat getDateFormat(String format) {
		return new SimpleDateFormat(format);
	}
	

	public abstract String[] getFormats();
		
	/**
	 * @param v
	 * @return
	 * @throws ParseException
	 */
	@Override
	public Date parseValue(String v) throws ParseException {
		if( v == null || v.isEmpty()) {
			return null;
		}
		Date d=null;
		String fmt[] = getFormats();
		for(int i=0 ; i< fmt.length ; i++){
			try {
				DateFormat df = getDateFormat(fmt[i]);
				
				if( df != null ){
					df.setLenient(false);
					d = df.parse(v);
					break;
				}
			} catch (java.text.ParseException e) {
				if( i == (fmt.length-1)){
					// none worked
					throw new ParseException("Bad date format ["+v+"] expecting "+fmt[0]);
				}
			}
		}
		return d;
	}

	/**
	 * simple format of a date
	 * 
	 * @param date
	 *            Date to format
	 * @return String in DD-MM-YYY format or null if passed null
	 */
	@Override
	public  String getString(java.util.Date date) {
		if (date == null)
			return null;
		DateFormat df = getDateFormat(getFormats()[0]);
		return df.format(date);
	}
	
	/** Which of the available formats in {@link #getFormats()}
	 * should be used as the hint text.
	 * 
	 * @return
	 */
	protected int getHintIndex(){
		return 0;
	}

	@Override
	public String getFormatHint() {
		//With html5 enabled this shows as a placeholder so if the browser overrides
		// format to show a date picker this is hidden.
		return getFormats()[getHintIndex()].toLowerCase();
	}

	@Override
	public Date convert(Object v) throws TypeException {
		if( v instanceof Date || v == null){
		   return (Date) v;
		}
		if( v instanceof Calendar) {
			return ((Calendar)v).getTime();
		}
		if( v instanceof Number ){
			return new Date(((Number)v).longValue()*resolution);
		}
		if (v instanceof String) {
			try {
				return parseValue((String)v);
				
			} catch (ParseException e) {
					throw new TypeException("Bad date format "+v);
			}
		}
		throw new TypeException(v.getClass());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	@Override
	public String getType() {
		return "date";
	}
	@Override
	public Date getMin() {
		return min;
	}
	@Override
	public Date getMax() {
		return max;
	}
	@Override
	public Date setMin(Date val){
		Date old = min;
		min=val;
		return old;
	}
	@Override
	public Date setMax(Date val){
		Date old = max;
		max=val;
		return old;
	}
	@Override
	public String formatRange(Date n) {
		return getString(n);
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