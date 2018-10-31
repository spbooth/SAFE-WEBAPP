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

import java.util.Iterator;

import org.junit.Test;

import static org.junit.Assert.*;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;


public abstract class MultiInputTestBase<T,V extends Input,I extends MultiInput<T, V>>  extends WebappTestBase implements TestDataProvider<T, I> ,
InputInterfaceTest<T, I, MultiInputTestBase<T, V, I>>{

	
	public InputInterfaceTest<T, I, MultiInputTestBase<T, V, I>> input_test = new InputInterfaceTestImpl<>(this);
	@Test
	public void dummy(){
		
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testGetInputs() throws Exception{
		MultiInput i = getInput();
		for(Iterator<Input> it = i.getInputs(); it.hasNext();){
			Input si = it.next();
			assertNotNull(si);
		}
	}
	@Test
	public void testGetSubInput() throws Exception{
		MultiInput<?,?> i = getInput();
		for(String s : i.getSubKeys() ){
			Input it = i.getInput(s);
			assertNotNull(it);
		}
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
	
}