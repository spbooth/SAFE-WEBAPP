//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.model.data.TestType.TestValue;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.BasicTypeInput;

/**
 * @author Stephen Booth
 *
 */
public class BasicTypeInputTest extends ParseAbstractInputTestCase<String, BasicTypeInput<TestType.TestValue>> implements
ListInputInterfaceTest<String, TestType.TestValue, BasicTypeInput<TestType.TestValue>, BasicTypeInputTest>{

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set<String> getBadData() {
		Set<String> values = new HashSet<>();
		values.add("Emu");
		values.add("Sooty");
		values.add("Basil");
		return values;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<String> getGoodData()  {
		Set<String> values = new HashSet<>();
		for(TestType.TestValue v : TestType.monsters.getValueSet()) {
			values.add(v.getTag());
		}
		return values;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public BasicTypeInput<TestValue> getInput() throws Exception {
		return TestType.monsters.getInput();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	@Override
	public Set<String> getGoodParseData() {
		
		return getGoodData();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getBadParseData()
	 */
	@Override
	public Set<String> getBadParseData() {
	
		return getBadData();
	}
	
	public final ListInputInterfaceTest<String, TestType.TestValue,BasicTypeInput<TestType.TestValue> , BasicTypeInputTest> list_tests = new ListInputInterfaceTestImpl<String, TestType.TestValue, BasicTypeInput<TestValue>, BasicTypeInputTest>(this);

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItembyValue()
	 */
	@Override
	@Test
	public final void testGetItembyValue() throws Exception {
		list_tests.testGetItembyValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItems()
	 */
	@Override
	@Test
	public final void testGetItems() throws Exception {
		list_tests.testGetItems();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByItem()
	 */
	@Override
	@Test
	public final void testGetTagByItem() throws Exception {
		list_tests.testGetTagByItem();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByValue()
	 */
	@Override
	@Test
	public final void testGetTagByValue() throws Exception {
		list_tests.testGetTagByValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetText()
	 */
	@Override
	@Test
	public final void testGetText() throws Exception {
		list_tests.testGetText();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testIsValid()
	 */
	@Override
	@Test
	public final void testIsValid() throws Exception {
		list_tests.testIsValid();
		
	}

}
