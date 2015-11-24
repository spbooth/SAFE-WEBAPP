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

	public ListInputInterfaceTest<Integer, TestEnum, EnumIntegerInput<TestEnum>, TestDataProvider<Integer,EnumIntegerInput<TestEnum>>> list_test = new ListInputInterfaceTestImpl<Integer, TestEnum, EnumIntegerInput<TestEnum>, TestDataProvider<Integer,EnumIntegerInput<TestEnum>>>(this);
	
	public EnumIntegerInputTest() {
		
	}

	public Set<String> getGoodParseData() {
		Set<String> good = new HashSet<String>();
		for(Integer i : getGoodData()){
			good.add(i.toString());
		}
		return good;
	}

	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<String>();
		bad.add("Lion");
		bad.add("987");
		bad.add("1fred");
		return bad;
	}

	public Set<Integer> getGoodData()  {
		HashSet<Integer> res = new HashSet<Integer>();
		res.add(TestEnum.CAT.ordinal());
		res.add(TestEnum.DOG.ordinal());
		res.add(TestEnum.HAMSTER.ordinal());
		return res;
	}

	public Set<Integer> getBadData()  {
		HashSet<Integer> res = new HashSet<Integer>();
		res.add(1000);
		res.add(-8);
		res.add(45);
		return res;
	}

	public EnumIntegerInput<TestEnum> getInput() throws Exception {
		return new EnumIntegerInput<TestEnum>(TestEnum.class);
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
