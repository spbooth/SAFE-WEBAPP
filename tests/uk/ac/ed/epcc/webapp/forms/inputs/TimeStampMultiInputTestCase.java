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

public class TimeStampMultiInputTestCase extends MultiInputTestBase<Date, Input<Integer>, TimeStampMultiInput> implements TestParseDataProvider<Date, TimeStampMultiInput>,
ParseInputInterfaceTest<Date, TimeStampMultiInput, TimeStampMultiInputTestCase>{

	
	public ParseInputInterfaceTest<Date, TimeStampMultiInput, TimeStampMultiInputTestCase> parse_input_test = new ParseInputInterfaceTestImpl<Date, TimeStampMultiInput, TimeStampMultiInputTestCase>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	public Set<Date> getGoodData() throws Exception {
		Set<Date> result = new HashSet<Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND,0); // parse loses these
		result.add(c.getTime());
		c.add(Calendar.HOUR,1);
		result.add(c.getTime());
		c.add(Calendar.HOUR,-2);
		result.add(c.getTime());
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	public Set<Date> getBadData() throws Exception {
		Set<Date> result = new HashSet<Date>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND,0); // parse loses these
		c.add(Calendar.YEAR,2);
		result.add(c.getTime());
		c.add(Calendar.YEAR,-4);
		result.add(c.getTime());
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	public TimeStampMultiInput getInput() throws Exception {
		TimeStampMultiInput input = new TimeStampMultiInput();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR,-1);
		input.setMinDate(c.getTime());
		c.add(Calendar.YEAR,2);
		input.setMaxDate(c.getTime());
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	public Set<String> getGoodParseData() {
		Set<String> result = new HashSet<String>();
		result.add("Now-0d");
		result.add("Now+3d");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		result.add("Now-6y");
		result.add("Now+6y");
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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

	@Test
	public void setNullTest() throws Exception{
		Input<Date> input = getInput();
		input.setValue(null); 
		assertNull(input.getValue());
	}
	@Test
	public void testConvert() throws Exception{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		Date expect = c.getTime();
		TimeStampMultiInput input = getInput();
		assertEquals(expect, input.convert(expect));
		assertEquals(expect, input.convert(c));
		assertEquals(expect, input.convert(expect.getTime()/1000L));
	}
	@Test
	public void testSetCalendar() throws Exception{
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.MILLISECOND, 0);
	
		TimeStampMultiInput input = getInput();
		input.setValue(c.getTime());
		assertEquals(c, input.setCalendarFromInputs(null));
		assertEquals(c, input.setCalendarFromInputs(Calendar.getInstance()));
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