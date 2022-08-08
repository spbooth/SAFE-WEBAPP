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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;


public abstract class AbstractInputTestCase<T,I extends AbstractInput<T>> extends WebappTestBase implements TestDataProvider<T,I> ,
InputInterfaceTest<T, I, AbstractInputTestCase<T, I>>
{
	

	public InputInterfaceTest<T, I, AbstractInputTestCase<T, I>> input_test = new InputInterfaceTestImpl<>(this);
	



	

	@Override
	@Test
	public final void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}


	@Override
	@Test
	public final void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
	}


	@Override
	@Test
	public final void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}


	@Override
	@Test
	public final void testGood() throws  Exception {
		input_test.testGood();
		
	}


	@Override
	@Test
	public final void testBad() throws Exception {
		input_test.testBad();
	}


	@Override
	@Test
	public final void testGetString() throws Exception {
		input_test.testGetString();
	}

	
	
	
	
}