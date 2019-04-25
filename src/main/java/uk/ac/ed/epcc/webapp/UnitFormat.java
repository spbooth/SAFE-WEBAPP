//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.EnumSet;

/** A {@link NumberFormat} for {@link Units} intended for setting/editing quota values.
 * The largest unit that exactly divides the value is used
 * @author Stephen Booth
 *
 */
public class UnitFormat extends NumberFormat {

	/**
	 * 
	 */
	public UnitFormat() {
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double arg0, StringBuffer arg1, FieldPosition arg2) {
		Double d = Double.valueOf(arg0);  // jsut convert to long and format
		return format(d.longValue(),arg1,arg2);
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(long arg0, StringBuffer arg1, FieldPosition arg2) {
		Units u=Units.B;
		if( arg0 > 0L) {
			for(Units c : EnumSet.allOf(Units.class)) {
				if( arg0 >= c.bytes && ((arg0 % c.bytes) == 0L)) {
					u=c;
				}
			}
		}
		long val = arg0/u.bytes;
		String num = Long.toString(val);
		int start =arg1.length();
		arg1.append(num);
		if( u.bytes > 1L) {
			arg1.append(u.toString());
		}
		int end = arg1.length();
		if( arg2 != null ) {
			arg2.setBeginIndex(start);
			arg2.setEndIndex(end);
		}
		return arg1;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(String arg0, ParsePosition arg1) {
		int len = arg0.length();
		int start = arg1.getIndex();
		int end=start;
		while( end < len && Character.isDigit(arg0.charAt(end))) {
			end++;
		}
		if( end == start) {
			arg1.setErrorIndex(start);
			return null;
		}
		long value = Long.parseLong(arg0.substring(start, end));
		arg1.setIndex(end);
		String remainder = arg0.substring(end);
		Units u = null;
		for(Units c : EnumSet.allOf(Units.class)) {
			if( remainder.startsWith(c.toString())) {
				u=c;
				arg1.setIndex(end+c.toString().length());
				break;
			}
		}
		if( u != null ) {
			value *= u.bytes;
		}
		return Long.valueOf(value);
	}

	@Override
	public boolean isGroupingUsed() {
		return false;
	}

	@Override
	public boolean isParseIntegerOnly() {
		return true;
	}

	@Override
	public Long parse(String arg0) throws ParseException {
		ParsePosition p = new ParsePosition(0);
		Long o = (Long) parse(arg0,p);
		if( p.getIndex() != arg0.length()) {
			throw new ParseException("Unparsed suffix",p.getIndex());
		}
		return o;
	}

	

}
