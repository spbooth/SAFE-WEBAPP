//| Copyright - The University of Edinburgh 2015                            |
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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.TestParseDataProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampInput;
public class TimeStampInputTest extends ParseAbstractInputTestCase<Date,TimeStampInput>  implements TestParseDataProvider<Date,TimeStampInput>,BoundedInputDataProvider<Date, TimeStampInput>,BoundedInputInterfaceTest<Date, TimeStampInput, TimeStampInputTest>{

	public final BoundedInputInterfaceTest<Date, TimeStampInput, TimeStampInputTest> bounded_tests = new BoundedInputInterfaceTestImpl<>(this);
	@Test
	public void dummy(){
		
	}
	@Override
	public TimeStampInput getInput() {
	
		return  new TimeStampInput(1000L);
	}

	
	@Override
	public Set<Date> getBadData() {
		return new HashSet<>();
	}

	
	@Override
	public Set<Date> getGoodData() {
		HashSet<Date> good = new HashSet<>();
		Date r = new Date();
		r.setTime((r.getTime()/1000)*1000);
		good.add(r);
		return good;
	}

	

	@Override
	public Set<String> getBadParseData() {
		Set<String> res = new HashSet<>();
		res.add("12-12-2008 08:00:00");
		res.add("boris the spider");
		return res;
	}


	@Override
	public Set<String> getGoodParseData() {
		Set<String> res = new HashSet<>();
		res.add("2006-12-12 08:00:00");
		res.add("2006-12-12 08:00");
		res.add("2006-12-12 08");
		res.add("2006-12-12");
		return res;
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowBound()
	 */
	@Override
	public Date getLowBound() {
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2000, Calendar.FEBRUARY, 14);
		return c.getTime();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighBound()
	 */
	@Override
	public Date getHighBound() {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2500, Calendar.FEBRUARY, 14);
		return c.getTime();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighData()
	 */
	@Override
	public Set<Date> getHighData() {
		Set<Date> set = new HashSet<>();
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2500, Calendar.OCTOBER, 10);
		set.add(c.getTime());
		return set;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowData()
	 */
	@Override
	public Set<Date> getLowData() {
		Set<Date> set = new HashSet<>();
		set.add(new Date(9000L));
		return set;
	}
}