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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;



public class MonthInput extends ParseAbstractInput<Date> implements TagInput {
    private static final String DEFAULT_FORMAT = "MM-yyyy";
	DateFormat df;
 
    long resolution=1000L; // number of milliseconds in a tick
    public MonthInput(){
    	this(1000L);
    }
	public MonthInput(long resolution) {
		super();
		df = new SimpleDateFormat(DEFAULT_FORMAT);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setLenient(false);
		df.setCalendar(cal);
		df.setLenient(false);
		this.resolution = resolution;
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

		ParsePosition pos = new ParsePosition(0);
		d = df.parse(v, pos);
		// we have to parse the full string without error to have a match
		if( pos.getErrorIndex() != -1 || pos.getIndex() != v.length()){
			//Try to parse an integer
			try{
				int relative = Integer.parseInt(v);
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				c.set(Calendar.MILLISECOND,0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MINUTE,0);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.DAY_OF_MONTH,1);
				c.add(Calendar.MONTH, relative);
				d=c.getTime();
			}catch(NumberFormatException e2){
				throw new ParseException("Bad date format ["+v+"]");
			}
		}
		setValue(d);
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

		return df.format(date);
	}

	public String getTag() {
		return "("+DEFAULT_FORMAT.toUpperCase()+")";
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
				return df.parse((String)v);
			} catch (java.text.ParseException e) {
					throw new TypeError("Bad date format "+v);
			}
		}
		throw new TypeError(v.getClass());
	}

	
	

}