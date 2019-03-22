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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/** A Format for Date objects that understands a relative date notation 
 * 
 * Now[+-]<number>[dwmy]
 * 
 * which translates to the current time plus or minus the specified 
 * number of days,weeks,months,years.
 * Time fields smaller then the specified unit are set to zero so
 * <ul> 
 * <li>Now+0y  is midnight at the start of the current year</li>
 * <li>Now-1m  is midnight at the start of the previous month
 * </ul>
 * Any other input is handled as per the superclass and objects are always formatted
 * in the superclass format.
 * 
 * @author spb
 *
 */


public class RelativeDateFormat extends SimpleDateFormat{
    private static final Pattern p = Pattern.compile("\\s*Now([+-])(\\d+)([dwmy])\\s*");
    public RelativeDateFormat(String format){
    	super(format);
    	setLenient(false);
    }
	

	@Override
	public Date parse(String v, ParsePosition pos) {
		

		if( v != null){
			Matcher m = p.matcher(v.substring(pos.getIndex()));
			if(m.lookingAt()){
				Calendar c = Calendar.getInstance();
				c.set(Calendar.MILLISECOND,0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MINUTE,0);
				c.set(Calendar.HOUR_OF_DAY, 0);
				String unit = m.group(3);
				int field;
				if( unit.equalsIgnoreCase("d")){
					field=Calendar.DATE;
				}else if( unit.equalsIgnoreCase("w")){
					c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
					field=Calendar.WEEK_OF_YEAR;
				}else if (unit.equalsIgnoreCase("m")){
					c.set(Calendar.DAY_OF_MONTH,1);
					field=Calendar.MONTH;
				}else{
					c.set(Calendar.DAY_OF_YEAR,1);
					field=Calendar.YEAR;
				}
				int number = Integer.parseInt(m.group(2));
				if( "+".equals(m.group(1))){
					c.add(field, number);
				}else{
					c.add(field,-number);
				}
				pos.setIndex(pos.getIndex()+m.regionEnd());
				return c.getTime();
			}else{
				return super.parse(v,pos);
			}

		}else{
			return super.parse(v,pos);
		}
	}
	
}