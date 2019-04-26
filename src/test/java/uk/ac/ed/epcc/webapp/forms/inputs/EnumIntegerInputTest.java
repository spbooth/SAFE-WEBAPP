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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.TestDataProvider;
import uk.ac.ed.epcc.webapp.forms.TestEnum;

public class EnumIntegerInputTest extends ParseAbstractInputTestCase<Integer,EnumIntegerInput<TestEnum>> implements TestDataProvider<Integer,EnumIntegerInput<TestEnum>>,
ListInputInterfaceTest<Integer, TestEnum, EnumIntegerInput<TestEnum>, TestDataProvider<Integer,EnumIntegerInput<TestEnum>>>
{

	public ListInputInterfaceTest<Integer, TestEnum, EnumIntegerInput<TestEnum>, TestDataProvider<Integer,EnumIntegerInput<TestEnum>>> list_test = new ListInputInterfaceTestImpl<>(this);
	
	public EnumIntegerInputTest() {
		
	}

	@Override
	public Set<String> getGoodParseData() {
		Set<String> good = new HashSet<>();
		for(Integer i : getGoodData()){
			good.add(i.toString());
		}
		return good;
	}

	@Override
	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<>();
		bad.add("Lion");
		bad.add("987");
		bad.add("1fred");
		return bad;
	}

	@Override
	public Set<Integer> getGoodData()  {
		HashSet<Integer> res = new HashSet<>();
		res.add(TestEnum.CAT.ordinal());
		res.add(TestEnum.DOG.ordinal());
		res.add(TestEnum.HAMSTER.ordinal());
		return res;
	}

	@Override
	public Set<Integer> getBadData()  {
		HashSet<Integer> res = new HashSet<>();
		res.add(1000);
		res.add(-8);
		res.add(45);
		return res;
	}

	@Override
	public EnumIntegerInput<TestEnum> getInput() throws Exception {
		return new EnumIntegerInput<>(TestEnum.class);
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