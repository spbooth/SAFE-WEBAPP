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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.CheckBoxInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;



public class CheckBoxInputTest extends WebappTestBase implements TestParseDataProvider<String,CheckBoxInput>,
InputInterfaceTest<String, CheckBoxInput, CheckBoxInputTest>,
BinaryInputInterfaceTest<String, CheckBoxInput, CheckBoxInputTest>,
ParseInputInterfaceTest<String, CheckBoxInput, CheckBoxInputTest>

{

	
	public InputInterfaceTest<String, CheckBoxInput, CheckBoxInputTest> input_test = new InputInterfaceTestImpl<>(this);
	
	
	public BinaryInputInterfaceTest<String, CheckBoxInput, CheckBoxInputTest> binary_input_test = new BinaryInputInterfaceTestImpl<>(this);
	
	
	public ParseInputInterfaceTest<String, CheckBoxInput, CheckBoxInputTest> parse_input_test = new ParseInputInterfaceTestImpl<>(this);
	
	@Override
	public Set<String> getGoodData() throws Exception {
		HashSet<String> good = new HashSet<>();
		good.add( "Y" );
		good.add( "N" );
		return good;
	}
	@Test
	public void testSet() throws ParseException{
		CheckBoxInput input = getInput();
		input.setChecked(false);
		assertFalse(input.isChecked());
		input.parse("Y");
		assertTrue(input.isChecked());
		assertEquals(input.getValue(), "Y");
		assertEquals(input.getString(), "Y");
		
	}
	@Test
	public void testUnSet() throws ParseException{
		CheckBoxInput input = getInput();
		input.setChecked(true);
		assertTrue(input.isChecked());
		input.parse(null);
		assertFalse(input.isChecked());
		assertEquals(input.getValue(), "N");
		assertEquals(input.getString(), "N");
	}
	@Test
	public void testEmpty() throws ParseException{
		CheckBoxInput input = getInput();
		input.setChecked(true);
		assertTrue(input.isChecked());
		input.parse("");
		assertFalse(input.isChecked());
		assertEquals(input.getValue(), "N");
		assertEquals(input.getString(), "N");
	}
	@Override
	public Set<String> getBadData() throws Exception {
		HashSet<String> bad = new HashSet<>();
		return bad;
	}

	@Override
	public CheckBoxInput getInput() {

		return new CheckBoxInput("Y", "N");
	}
	

	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> good = new HashSet<>();
		good.add( "Y" );
		good.add( "N" );
		return good;
	}

	@Override
	public Set<String> getBadParseData() {
		HashSet<String> bad = new HashSet<>();
		return bad;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#allowNull()
	 */
	@Override
	public boolean allowNull() {
		return false;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#parseNull()
	 */
	@Override
	@Test
	public void parseNull() throws Exception {
		parse_input_test.parseNull();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public void testGoodDataParses() throws Exception {
		parse_input_test.testGoodDataParses();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public void testGoodParse() throws Exception {
		parse_input_test.testGoodParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public void testBadParse() throws Exception {
		parse_input_test.testBadParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.BinaryInputInterfaceTest#testBinary()
	 */
	@Override
	@Test
	public void testBinary() throws Exception {
		binary_input_test.testBinary();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGetKey()
	 */
	@Override
	@Test
	public void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testMakeHtml()
	 */
	@Override
	@Test
	public void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testMakeSwing()
	 */
	@Override
	@Test
	public void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGood()
	 */
	@Override
	@Test
	public void testGood() throws TypeError, Exception {
		input_test.testGood();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testBad()
	 */
	@Override
	@Test
	public void testBad() throws Exception {
		input_test.testBad();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGetString()
	 */
	@Override
	@Test
	public void testGetString() throws Exception {
		input_test.testGetString();
		
	}
}