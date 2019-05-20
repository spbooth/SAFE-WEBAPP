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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;


/**
 * @author spb
 *
 */

public class CaseInsensativeSetInputTestCase extends ParseAbstractInputTestCase<String, SetInput<Number>> implements
ListInputInterfaceTest<String, Number, SetInput<Number>, CaseInsensativeSetInputTestCase> {

	public ListInputInterfaceTest<String, Number, SetInput<Number>, CaseInsensativeSetInputTestCase> list_test = new ListInputInterfaceTestImpl<>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	@Override
	public Set<String> getGoodParseData() {
		Set<String> good = new HashSet<>();
		good.add("One");
		good.add("Three");
		good.add("Five");
		good.add("OnE");
		good.add("three");
		good.add("FiVe");
		return good;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getBadParseData()
	 */
	@Override
	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<>();
		bad.add("Two");
		bad.add("Four");
		bad.add("Eight");
		return bad;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<String> getGoodData() throws Exception {
		Set<String> good = new HashSet<>();
		good.add("one");
		good.add("three");
		good.add("five");
		good.add("one");
		good.add("three");
		good.add("five");
		return good;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set<String> getBadData() throws Exception {
		return getBadParseData();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public SetInput<Number> getInput() throws Exception {
		SetInput<Number> input = new SetInput<>();
		input.setCaseInsensative(true);
		input.addChoice("One", 1);
		input.addChoice("Three",3);
		input.addChoice("Five", 5);
		return input;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItembyValue()
	 */
	@Override
	@Test
	public final void testGetItembyValue() throws Exception {
		list_test.testGetItembyValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItems()
	 */
	@Override
	@Test
	public final void testGetItems() throws Exception {
		list_test.testGetItems();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByItem()
	 */
	@Override
	@Test
	public final void testGetTagByItem() throws Exception {
		list_test.testGetTagByItem();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByValue()
	 */
	@Override
	@Test
	public final void testGetTagByValue() throws Exception {
		list_test.testGetTagByValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetText()
	 */
	@Override
	@Test
	public final void testGetText() throws Exception {
		list_test.testGetText();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testIsValid()
	 */
	@Override
	@Test
	public final void testIsValid() throws Exception {
		list_test.testIsValid();
		
	}

}