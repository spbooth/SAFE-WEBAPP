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
package uk.ac.ed.epcc.webapp.forms;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.inputs.ClassInput;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;


public class ClassInputTest extends WebappTestBase implements TestDataProvider<String, ClassInput<DataObjectFactory>>,
ListInputInterfaceTest<String, Class<? extends DataObjectFactory>, ClassInput<DataObjectFactory>, ClassInputTest> 
{
	

	public ListInputInterfaceTest<String, Class<? extends DataObjectFactory>, ClassInput<DataObjectFactory>, ClassInputTest> list_test = new ListInputInterfaceTestImpl<>(this);

	@Override
	public Set<String> getGoodData() throws Exception {
		Set<String> result = new HashSet<>();
		result.add("AppUserFactory");
		return result;
	}

	@Override
	public Set<String> getBadData() throws Exception {
		Set<String> result = new HashSet<>();
		result.add("Fred");
		result.add("Date");
		return result;
	}

	@Override
	public ClassInput<DataObjectFactory> getInput() throws Exception {
		return new ClassInput<>(ctx, DataObjectFactory.class);
	}
	
	@Test
	public void testGetItem() throws Exception{
		ClassInput<DataObjectFactory> input = getInput();
		for(String s : getGoodData()){
			input.setValue(s);
			Class x = input.getItem();
			assertNotNull(x);
			assertTrue(DataObjectFactory.class.isAssignableFrom(x));
		}
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