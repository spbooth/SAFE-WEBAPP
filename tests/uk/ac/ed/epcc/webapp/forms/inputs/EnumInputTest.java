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

	

}
