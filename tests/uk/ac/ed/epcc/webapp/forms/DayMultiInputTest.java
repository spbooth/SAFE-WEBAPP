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
package uk.ac.ed.epcc.webapp.forms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.DayMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;



public class DayMultiInputTest extends MultiInputTestBase<Date,Input<Integer>,DayMultiInput> implements TestParseDataProvider<Date, DayMultiInput> ,

ParseInputInterfaceTest<Date, DayMultiInput, DayMultiInputTest>
{


 
	public ParseInputInterfaceTest<Date, DayMultiInput, DayMultiInputTest> parse_input_test = new ParseInputInterfaceTestImpl<>(this);
	
	@Override
	public DayMultiInput getInput() throws Exception {
		return  new DayMultiInput();
	}
   
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.DateInputTest#getGoodParseData()
	 */
	@Override
	public Set<String> getGoodParseData() {
		
		Set<String> res = new HashSet<>();
		res.add("Now+0d");
		res.add("Now-1y");
		res.add("\nNow-1m\n");
		res.add("2010-12-12");
		return res;
	}
	@Override
	public Set<Date> getGoodData() throws Exception {
		Set<Date> res = new HashSet<>();
		return res;
	}
	@Override
	public Set<Date> getBadData() throws Exception {
		Set<Date> res = new HashSet<>();
		return res;
	}
	@Override
	public Set<String> getBadParseData() {
		Set<String> res = new HashSet<>();
		res.add("womble");
		return res;
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
	public void parseNull() throws Exception {
		parse_input_test.parseNull();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public void testGoodDataParses() throws Exception {
		parse_input_test.testGoodDataParses();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public void testGoodParse() throws Exception {
		parse_input_test.testGoodParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public void testBadParse() throws Exception {
		parse_input_test.testBadParse();
		
	}
	
}