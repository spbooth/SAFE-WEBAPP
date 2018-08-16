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
import uk.ac.ed.epcc.webapp.forms.inputs.EnumInput;

public class EnumInputTest extends ParseAbstractInputTestCase<String,EnumInput<TestEnum>> implements TestDataProvider<String,EnumInput<TestEnum>>,
ListInputInterfaceTest<String, TestEnum, EnumInput<TestEnum>, TestDataProvider<String,EnumInput<TestEnum>>>
{

	public ListInputInterfaceTest<String, TestEnum, EnumInput<TestEnum>, TestDataProvider<String,EnumInput<TestEnum>>> list_test = new ListInputInterfaceTestImpl<String, TestEnum, EnumInput<TestEnum>, TestDataProvider<String,EnumInput<TestEnum>>>(this);
	
	public EnumInputTest() {
		
	}

	public Set<String> getGoodParseData() {
		return getGoodData();
	}

	public Set<String> getBadParseData() {
		return getBadData();
	}

	public Set<String> getGoodData()  {
		HashSet<String> res = new HashSet<String>();
		res.add(TestEnum.CAT.name());
		res.add(TestEnum.DOG.name());
		res.add(TestEnum.HAMSTER.name());
		return res;
	}

	public Set<String> getBadData()  {
		HashSet<String> res = new HashSet<String>();
		res.add("Lion");
		res.add("Tiger");
		res.add("Bear");
		return res;
	}

	public EnumInput<TestEnum> getInput() throws Exception {
		return new EnumInput<TestEnum>(TestEnum.class);
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