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
ParseInputInterfaceTest<Date, TimeStampMultiInput, TimeStampMultiInputTestCase>,
BoundedInputDataProvider<Date, TimeStampMultiInput>,
BoundedInputInterfaceTest<Date, TimeStampMultiInput, TimeStampMultiInputTestCase>{

	
	public ParseInputInterfaceTest<Date, TimeStampMultiInput, TimeStampMultiInputTestCase> parse_input_test = new ParseInputInterfaceTestImpl<>(this);
	
	public BoundedInputInterfaceTestImpl<Date, TimeStampMultiInput, TimeStampMultiInputTestCase> bounded_tests = new BoundedInputInterfaceTestImpl<>(this);
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<Date> getGoodData() throws Exception {
		Set<Date> result = new HashSet<>();
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
	@Override
	public Set<Date> getBadData() throws Exception {
		Set<Date> result = new HashSet<>();
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
	@Override
	public TimeStampMultiInput getInput() throws Exception {
		
		Calendar c = Calendar.getInstance();
		TimeStampMultiInput input = new TimeStampMultiInput(c.getTime());
		c.add(Calendar.YEAR,-1);
		input.setMinDate(c.getTime());
		c.add(Calendar.YEAR,2);
		input.setMaxDate(c.getTime());
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	@Override
	public Set<String> getGoodParseData() {
		Set<String> result = new HashSet<>();
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
	@Override
	public Set<String> getBadParseData() {
		Set<String> result = new HashSet<>();
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
	@Override
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputInterfaceTest#testMin()
	 */
	@Override
	@Test
	public void testMin() throws Exception {
		bounded_tests.testMin();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputInterfaceTest#testMax()
	 */
	@Override
	@Test
	public void testMax() throws Exception {
		bounded_tests.testMax();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputInterfaceTest#testBoth()
	 */
	@Override
	@Test
	public void testBoth() throws Exception {
		bounded_tests.testBoth();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputInterfaceTest#testAll()
	 */
	@Override
	@Test
	public void testAll() throws Exception {
		bounded_tests.testAll();
		
	}

	@Override
	public Date getLowBound() {
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.HOUR_OF_DAY, -1);
		return c.getTime();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighBound()
	 */
	@Override
	public Date getHighBound() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.HOUR_OF_DAY, 1);
		return c.getTime();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighData()
	 */
	@Override
	public Set<Date> getHighData() {
		Set<Date> set = new HashSet<>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.HOUR_OF_DAY, 8);
		set.add(c.getTime());
		return set;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowData()
	 */
	@Override
	public Set<Date> getLowData() {
		Set<Date> set = new HashSet<>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.HOUR_OF_DAY, -8);
		set.add(c.getTime());
		return set;
	}
}