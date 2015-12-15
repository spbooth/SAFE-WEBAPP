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

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;



/** A abstract superclass for text {@link Input}s that parse to java {@link Date} objects.
 * 
 * sub-classes may be constrained to generate values that are constrained to day/hour/min boundaries.
 * 
 * This class uses a series of {@link DateFormat}s applied in turn so as to support multiple fomats
 * @author spb
 *
 */
public abstract class AbstractDateInput extends ParseAbstractInput<Date> implements TagInput {
	DateFormat df[];
   
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

	public String getTag() {
		//TODO this is confusing if the browser overrides
		// format to show a date picker with text format selected from
		// locale.
		return "(date e.g. "+getFormats()[0].toUpperCase()+")";
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

	

}