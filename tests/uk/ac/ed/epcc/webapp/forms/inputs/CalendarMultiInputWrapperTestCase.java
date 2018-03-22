//| Copyright - The University of Edinburgh 2012                            |
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.MultiInputTestBase;
import uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.TestParseDataProvider;

/**
 * @author spb
 *
 */

public class CalendarMultiInputWrapperTestCase extends MultiInputTestBase<Date, Input<Integer>, CalendarMultiInputWrapper> implements TestParseDataProvider<Date, CalendarMultiInputWrapper>,
ParseInputInterfaceTest<Date, CalendarMultiInputWrapper, CalendarMultiInputWrapperTestCase>{

	
	public ParseInputInterfaceTest<Date, CalendarMultiInputWrapper, CalendarMultiInputWrapperTestCase> parse_input_test = new ParseInputInterfaceTestImpl<Date, CalendarMultiInputWrapper, CalendarMultiInputWrapperTestCase>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	public Set<Date> getGoodData() throws Exception {
		Set<Date> result = new HashSet<Date>();
		Calendar c = getCalendar(); // parse loses these
		result.add(c.getTime());
		c.add(Calendar.DAY_OF_YEAR,1);
		result.add(c.getTime());
		c.add(Calendar.DAY_OF_YEAR,-2);
		result.add(c.getTime());
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	public Set<Date> getBadData() throws Exception {
		Set<Date> result = new HashSet<Date>();
		Calendar c = getCalendar();
		c.add(Calendar.YEAR,2);
		result.add(c.getTime());
		c.add(Calendar.YEAR,-4);
		result.add(c.getTime());
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	public CalendarMultiInputWrapper getInput() throws Exception {
		DateInput nested = new DateInput();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR,-1);
		nested.setMin(c.getTime());
		c.add(Calendar.YEAR,2);
		nested.setMax(c.getTime());
		CalendarMultiInputWrapper input = new CalendarMultiInputWrapper(nested);

		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	public Set<String> getGoodParseData() {
		Set<String> result = new HashSet<String>();
		//result.add("Now-0d");
		//result.add("Now+3d");
		DateFormat df = getDateFormat();
		try {
			for(Date d : getGoodData()){
				result.add(df.format(d));
			}
		} catch (Exception e) {
			assertTrue("Data error",false);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getBadParseData()
	 */
	public Set<String> getBadParseData() {
		Set<String> result = new HashSet<String>();
		//result.add("Now-6y");
		//result.add("Now+6y");
		DateFormat df = getDateFormat();
		try {
			for(Date d : getBadData()){
				result.add(df.format(d));
			}
		} catch (Exception e) {
			assertTrue("Data error",false);
		}
		result.add("BorisTheSpider");
		return result;
	}

	/**
	 * @return
	 */
	protected SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	@Test
	public void setNullTest() throws Exception{
		Input<Date> input = getInput();
		input.setValue(null); 
		assertNull(input.getValue());
	}
	@Test
	public void testConvert() throws Exception{
		Calendar c = getCalendar();
		Date expect = c.getTime();
		CalendarMultiInputWrapper input = getInput();
		assertEquals(expect, input.convert(expect));
		assertEquals(expect, input.convert(c));
		assertEquals(expect, input.convert(expect.getTime()/1000L));
	}

	/**
	 * @return
	 */
	protected Calendar getCalendar() {
		Calendar c = Calendar.getInstance();
		// We are wrapping a Date so scrub fields which will be lost on parse
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.HOUR_OF_DAY,0);
		return c;
	}
	@Test
	public void testSetCalendar() throws Exception{
		Calendar c = getCalendar();
	
		CalendarMultiInputWrapper input = getInput();
		input.setValue(c.getTime());
		assertEquals(c, input.setCalendarFromInputs(null));
		assertEquals(c, input.setCalendarFromInputs(getCalendar()));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#allowNull()
	 */
	public boolean allowNull() {
		return true;
	}
	@Override
	@Test
	public final void parseNull() throws Exception {
		parse_input_test.parseNull();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public final void testGoodDataParses() throws Exception {
		parse_input_test.testGoodDataParses();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public final void testGoodParse() throws Exception {
		parse_input_test.testGoodParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public final void testBadParse() throws Exception {
		parse_input_test.testBadParse();
		
	}
	
}