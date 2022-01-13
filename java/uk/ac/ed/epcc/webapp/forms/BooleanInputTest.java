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
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;


public class BooleanInputTest extends WebappTestBase implements TestParseDataProvider<Boolean,BooleanInput>,
InputInterfaceTest<Boolean, BooleanInput, BooleanInputTest>,
BinaryInputInterfaceTest<Boolean, BooleanInput, BooleanInputTest>,

ParseInputInterfaceTest<Boolean, BooleanInput, BooleanInputTest>
{

	
	public InputInterfaceTest<Boolean, BooleanInput, BooleanInputTest> input_test = new InputInterfaceTestImpl<>(this);
	
	
	public BinaryInputInterfaceTest<Boolean, BooleanInput, BooleanInputTest> binary_test = new BinaryInputInterfaceTestImpl<>(this);
	
	
	public ParseInputInterfaceTest<Boolean, BooleanInput, BooleanInputTest> parse_test = new ParseInputInterfaceTestImpl<>(this);
	
	@Override
	public Set<Boolean> getGoodData() throws Exception {
		HashSet<Boolean> good = new HashSet<>();
		good.add( Boolean.TRUE );
		good.add( Boolean.FALSE );
		return good;
	}

	@org.junit.Test
	public void testSet() throws ParseException{
		BooleanInput input = getInput();
		input.setChecked(false);
		assertFalse(input.isChecked());
		input.parse("true");
		assertTrue(input.isChecked());
		assertEquals(input.getValue(), Boolean.TRUE);
		assertEquals(input.getString(), "true");
		
	}
	@org.junit.Test
	public void testUnSet() throws ParseException{
		BooleanInput input = getInput();
		input.setChecked(true);
		assertTrue(input.isChecked());
		input.parse(null);
		assertFalse(input.isChecked());
		assertEquals(input.getValue(), Boolean.FALSE);
		assertEquals(input.getString(), "false");
	}
	@org.junit.Test
	public void testEmpty() throws ParseException{
		BooleanInput input = getInput();
		input.setChecked(true);
		assertTrue(input.isChecked());
		input.parse("");
		assertFalse(input.isChecked());
		assertEquals(input.getValue(), Boolean.FALSE);
		assertEquals(input.getString(), "false");
	}
	@Override
	public Set<Boolean> getBadData() throws Exception {
		HashSet<Boolean> bad = new HashSet<>();
		return bad;
	}

	@Override
	public BooleanInput getInput() {

		return new BooleanInput();
	}

	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> good = new HashSet<>();
		good.add( "true" );
		good.add( "false" );
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
		parse_test.parseNull();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public void testGoodDataParses() throws Exception {
		parse_test.testGoodDataParses();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public void testGoodParse() throws Exception {
		parse_test.testGoodParse();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public void testBadParse() throws Exception {
		parse_test.testBadParse();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.BinaryInputInterfaceTest#testBinary()
	 */
	@Override
	@Test
	public void testBinary() throws Exception {
		binary_test.testBinary();
		
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
	public void testGood() throws Exception {
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