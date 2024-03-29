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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.TestDataProvider;
import uk.ac.ed.epcc.webapp.forms.UnmodifiableInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.UnmodifiableInputInterfaceTestImpl;


/**
 * @author spb
 *
 */

// Note OptionalInput test calls setValue so won't pass
public class ConstantInputTestCase extends WebappTestBase implements TestDataProvider<Number, ConstantInput<Number>> ,
InputInterfaceTest<Number, ConstantInput<Number>, ConstantInputTestCase>,
UnmodifiableInputInterfaceTest<Number, ConstantInput<Number>, ConstantInputTestCase> 
{

	
	public InputInterfaceTest<Number, ConstantInput<Number>, ConstantInputTestCase> input_test = new InputInterfaceTestImpl<>(this);
	
	
	public UnmodifiableInputInterfaceTest<Number, ConstantInput<Number>, ConstantInputTestCase> unmodifiable_test = new UnmodifiableInputInterfaceTestImpl<>(this);
	
	/**
	 * 
	 */
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<Number> getGoodData() throws Exception {
		Set<Number> result = new HashSet<>();
		result.add(12);
		result.add(16.0);
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set<Number> getBadData() throws Exception {
		Set<Number> result = new HashSet<>();
		//result.add(15);
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public ConstantInput<Number> getInput() throws Exception {
		ConstantInput<Number> input = new ConstantInput<>("Boris",12);
		return input;
	}

	@Test
	public void testValue() throws Exception{
		Input input = getInput();
		assertEquals(12,input.getValue());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.InputInterfaceTest#testGetKey()
	 */
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
	public final void testGood() throws  Exception {
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
	 * @see uk.ac.ed.epcc.webapp.forms.UnmodifiableInputInterfaceTest#testWebParse()
	 */
	@Override
	public void testWebParse() throws Exception {
		unmodifiable_test.testWebParse();
		
	}
	
	
}