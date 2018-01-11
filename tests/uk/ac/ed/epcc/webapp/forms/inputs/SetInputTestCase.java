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

public class SetInputTestCase extends ParseAbstractInputTestCase<String, SetInput<Number>> implements
ListInputInterfaceTest<String, Number, SetInput<Number>, SetInputTestCase> {

	public ListInputInterfaceTest<String, Number, SetInput<Number>, SetInputTestCase> list_test = new ListInputInterfaceTestImpl<String, Number, SetInput<Number>, SetInputTestCase>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getGoodParseData()
	 */
	public Set<String> getGoodParseData() {
		Set<String> good = new HashSet<String>();
		good.add("One");
		good.add("Three");
		good.add("Five");
		return good;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#getBadParseData()
	 */
	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<String>();
		bad.add("Two");
		bad.add("Four");
		bad.add("Eight");
		bad.add("ONE");
		return bad;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	public Set<String> getGoodData() throws Exception {
		return getGoodParseData();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	public Set<String> getBadData() throws Exception {
		return getBadParseData();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	public SetInput<Number> getInput() throws Exception {
		SetInput<Number> input = new SetInput<Number>();
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

}