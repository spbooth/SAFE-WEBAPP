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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.EnumSet;

/** A NumberFormat for storage units for displaying arbitrary values
 * 
 * The largest unit that is less than the value is used by default.
 * @author Stephen Booth
 *
 */
public class UnitDisplayFormat extends NumberFormat {
	private final NumberFormat inner;
	/**
	 * 
	 */
	public UnitDisplayFormat() {
		inner = NumberFormat.getInstance();
		inner.setMaximumFractionDigits(2);
	}
	@Override
	public StringBuffer format(long arg0, StringBuffer arg1, FieldPosition arg2) {
		return format((double)arg0,arg1,arg2);
	}
	

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double arg0, StringBuffer arg1, FieldPosition arg2) {
		Units u=Units.B;
		if( arg0 > 0L) {
			for(Units c : EnumSet.allOf(Units.class)) {
				if( arg0 >= c.bytes ) {
					u=c;
				}
			}
		}
		double val = ((double)arg0)/((double)u.bytes);
		inner.format(val, arg1, arg2);
		
		if( u.bytes > 1L) {
			arg1.append(u.toString());
			arg2.setEndIndex(arg2.getEndIndex()+u.toString().length());
		}
		return arg1;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(String arg0, ParsePosition arg1) {
		
		Number base = inner.parse(arg0, arg1);
		if( base == null) {
			return null;
		}
		int end = arg1.getIndex();
		String remainder = arg0.substring(end);
		if( remainder.isEmpty()) {
			// must be bytes
			return Double.valueOf(base.doubleValue());
		}
		Units u = null;
		for(Units c : EnumSet.allOf(Units.class)) {
			if( remainder.startsWith(c.toString())) {
				u=c;
				arg1.setIndex(end+c.toString().length());
				break;
			}
		}
		if( u != null ) {
			return Double.valueOf(base.doubleValue()*u.bytes);
		}
		arg1.setErrorIndex(end);
		return null;
	}

	

	@Override
	public Number parse(String arg0) throws ParseException {
		ParsePosition p = new ParsePosition(0);
		Number o =  parse(arg0,p);
		if( p.getIndex() != arg0.length()) {
			throw new ParseException("Unparsed suffix",p.getIndex());
		}
		return o;
	}

	@Override
	public int getMaximumFractionDigits() {
		return inner.getMaximumFractionDigits();
	}

	@Override
	public int getMinimumFractionDigits() {
		return inner.getMinimumFractionDigits();
	}

	@Override
	public void setMaximumFractionDigits(int newValue) {
		inner.setMaximumFractionDigits(newValue);
	}

	@Override
	public void setMinimumFractionDigits(int newValue) {
		inner.setMinimumFractionDigits(newValue);
	}

	

}
