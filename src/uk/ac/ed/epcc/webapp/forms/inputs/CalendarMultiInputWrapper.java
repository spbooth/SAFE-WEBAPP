//| Copyright - The University of Edinburgh 2016                            |
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

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** A wrapper to convert a {@link AbstractDateInput} into a {@link MultiInput}.
 * @author spb
 *
 */
public class CalendarMultiInputWrapper extends AbstractCalendarMultiInput {
	
	public CalendarMultiInputWrapper(TimeStampInput input){
		this(input,Calendar.SECOND);
	}
	public CalendarMultiInputWrapper(DateInput input){
		this(input,Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * @param input nested {@link AbstractDateInput}
	 * @param max_field maximum resolution field to present
	 */
	public CalendarMultiInputWrapper(AbstractDateInput input, int max_field) {
		super(max_field);
		this.input=input;
	}

	private final AbstractDateInput input;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public void parse(String v) throws ParseException {
		input.parse(v);
		Date val = input.getValue();
		if( val == null ){
			setNull();
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(val);
			setInputsFromCalendar(cal);
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#convert(java.lang.Object)
	 */
	@Override
	public Date convert(Object v) throws TypeError {
		return input.convert(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#getMin()
	 */
	@Override
	public Date getMin() {
		return input.getMin();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#getMax()
	 */
	@Override
	public Date getMax() {
		return input.getMax();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#formatRange(java.lang.Object)
	 */
	@Override
	public String formatRange(Date n) {
		return input.formatRange(n);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#setMin(java.lang.Object)
	 */
	@Override
	public Date setMin(Date val) {
		return input.setMin(val);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInput#setMax(java.lang.Object)
	 */
	@Override
	public Date setMax(Date val) {
		return input.getMax();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractCalendarMultiInput#setNull()
	 */
	@Override
	protected void setNull() {
		input.setValue(null);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#getValue()
	 */
	@Override
	public Date getValue() {
	    Calendar c = null;
	    Date d = input.getValue();
	    if( d != null ){
	    	c = Calendar.getInstance();
	    	c.setTime(d);
	    }
	    c=setCalendarFromInputs(c);
	    if( c == null ){
	    	input.setValue(null);
	    }else{
	    	input.setValue(c.getTime());
	    }
	    if( c == null ) {
	    	return null;
	    }
		return c.getTime();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#setValue(java.lang.Object)
	 */
	@Override
	public Date setValue(Date v) throws TypeError {
		Date old = input.setValue(v);
		if( v == null ){
			setInputsFromCalendar(null);
		}else{
			Calendar c = Calendar.getInstance();
			c.setTime(v);
			setInputsFromCalendar(c);
		}
		return old;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#getString(java.lang.Object)
	 */
	@Override
	public String getString(Date val) {
		return input.getString(val);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.MultiInput#validate()
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		input.validate();
	}
	

}
