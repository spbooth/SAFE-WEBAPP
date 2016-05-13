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
import java.util.Date;

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
public abstract class AbstractDateInput extends ParseAbstractInput<Date> implements BoundedInput<Date>, FormatHintInput {
	DateFormat df[];
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
		df = new DateFormat[formats.length];
	
		for(int i=0 ; i< formats.length;i++){
			try{
				df[i] = getDateFormat(formats[i]);
				df[i].setLenient(false);
				if( formats[i].length() > length){
					length = formats[i].length();
				}
			}catch(IllegalArgumentException e){
				// be lenient here in case of old java version
				df[i]=null;
			}
		}
		setBoxWidth(length);
		setMaxResultLength(length);
	}
	protected DateFormat getDateFormat(String format) {
		return new SimpleDateFormat(format);
	}
	

	public abstract String[] getFormats();
		
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
		Date d = parseValue(v);
		setValue(d);
	}
	/**
	 * @param v
	 * @return
	 * @throws ParseException
	 */
	public Date parseValue(String v) throws ParseException {
		Date d=null;
		for(int i=0 ; i< df.length ; i++){
			try {
				if( df[i] != null ){
					d = df[i].parse(v);
					break;
				}
			} catch (java.text.ParseException e) {
				if( i == (df.length-1)){
					// none worked
					throw new ParseException("Bad date format ["+v+"] expecting "+getFormats()[0]);
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

		return df[0].format(date);
	}
	
	/** 
	 * 
	 * @return
	 */
	protected int getHintIndex(){
		return 0;
	}

	public String getFormatHint() {
		//With html5 enabled this shows as a placeholder so if the browser overrides
		// format to show a date picker this is hidden.
		return getFormats()[getHintIndex()].toUpperCase();
	}

	@Override
	public Date convert(Object v) throws TypeError {
		if( v instanceof Date || v == null){
		   return (Date) v;
		}
		if( v instanceof Number ){
			return new Date(((Number)v).longValue()*resolution);
		}
		if (v instanceof String) {
			try {
				return parseValue((String)v);
				
			} catch (ParseException e) {
					throw new TypeError("Bad date format "+v);
			}
		}
		throw new TypeError(v.getClass());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
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
	public void validate() throws FieldException {
		super.validate();
		Date value = getValue();
		if( value == null ){
			return;
		}
		if( min != null && value.before(min)){
			throw new ValidateException("Value must be after "+getString(min));
		}
		if( max != null && value.after(max)){
			throw new ValidateException("Value must be before "+getString(max));
		}
	}

	

}