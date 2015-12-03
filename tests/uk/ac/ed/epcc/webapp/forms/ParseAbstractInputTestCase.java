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
import org.junit.runner.RunWith;

import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;

/**
 * 
 * @author spb
 *
 * @param <T> Type returned by input
 * @param <I> Type of input
 */
public abstract class ParseAbstractInputTestCase<T,I extends ParseAbstractInput<T>> extends AbstractInputTestCase<T,I> implements TestParseDataProvider<T,I>,
ParseInputInterfaceTest<T, I, ParseAbstractInputTestCase<T, I>>{

	
	public ParseInputInterfaceTest<T, I, ParseAbstractInputTestCase<T, I>> parse_input_test = new ParseInputInterfaceTestImpl<T, I, ParseAbstractInputTestCase<T,I>>(this);
	
	public boolean allowNull(){
		return true;
	}
	@Override
	@Test
	public final void parseNull() throws Exception {
		parse_input_test.parseNull();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public final void testGoodDataParses() throws Exception {
		parse_input_test.testGoodDataParses();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public final void testGoodParse() throws Exception {
		parse_input_test.testGoodParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public final void testBadParse() throws Exception {
		parse_input_test.testBadParse();
		
	}
	
	
}