package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.TestDataProvider;
import uk.ac.ed.epcc.webapp.forms.TestEnum;


public class RadioButtonInputTest extends WebappTestBase implements TestDataProvider<String,RadioButtonInput<String,TestEnum>>,
ListInputInterfaceTest<String, TestEnum, RadioButtonInput<String,TestEnum>, TestDataProvider<String,RadioButtonInput<String,TestEnum>>>,
InputInterfaceTest<String, RadioButtonInput<String,TestEnum>, RadioButtonInputTest>{

	public ListInputInterfaceTest<String, TestEnum, RadioButtonInput<String,TestEnum>, TestDataProvider<String,RadioButtonInput<String,TestEnum>>> list_test = new ListInputInterfaceTestImpl<String, TestEnum, RadioButtonInput<String,TestEnum>, TestDataProvider<String,RadioButtonInput<String,TestEnum>>>(this);
	
	public InputInterfaceTest<String, RadioButtonInput<String,TestEnum>, RadioButtonInputTest> input_test = new InputInterfaceTestImpl<String, RadioButtonInput<String,TestEnum>, RadioButtonInputTest>(this);


	public RadioButtonInputTest() {
		
	}
	
	@Test
	public void dummy(){
		
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

	public RadioButtonInput<String,TestEnum> getInput() throws Exception {
		return new RadioButtonInput<String,TestEnum>(new EnumInput<TestEnum>(TestEnum.class));
	}

	@Override
	@Test
	public final void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testMakeHtml()
	 */
	@Override
	@Test
	public final void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testMakeSwing()
	 */
	@Override
	@Test
	public final void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGood()
	 */
	@Override
	@Test
	public final void testGood() throws TypeError, Exception {
		input_test.testGood();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testBad()
	 */
	@Override
	@Test
	public final void testBad() throws Exception {
		input_test.testBad();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGetString()
	 */
	@Override
	@Test
	public final void testGetString() throws Exception {
		input_test.testGetString();
		
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
