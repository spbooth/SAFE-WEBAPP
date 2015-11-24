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
	

	public ListInputInterfaceTest<String, Class<? extends DataObjectFactory>, ClassInput<DataObjectFactory>, ClassInputTest> list_test = new ListInputInterfaceTestImpl<String, Class<? extends DataObjectFactory>, ClassInput<DataObjectFactory>, ClassInputTest>(this);

	public Set<String> getGoodData() throws Exception {
		Set<String> result = new HashSet<String>();
		result.add("AppUserFactory");
		return result;
	}

	public Set<String> getBadData() throws Exception {
		Set<String> result = new HashSet<String>();
		result.add("Fred");
		result.add("Date");
		return result;
	}

	public ClassInput<DataObjectFactory> getInput() throws Exception {
		return new ClassInput<DataObjectFactory>(ctx, DataObjectFactory.class);
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

}
