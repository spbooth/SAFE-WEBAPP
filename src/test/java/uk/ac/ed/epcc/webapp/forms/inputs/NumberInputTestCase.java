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
package uk.ac.ed.epcc.webapp.forms.inputs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;

public abstract class NumberInputTestCase<N extends Number&Comparable<N>,I extends NumberInput<N>> extends ParseAbstractInputTestCase<N,I> implements BoundedInputInterfaceTest<N, I, NumberInputTestCase<N,I>>,BoundedInputDataProvider<N, I> {

	BoundedInputInterfaceTestImpl<N,I, NumberInputTestCase<N,I>> bounded_tests = new BoundedInputInterfaceTestImpl<>(this);
	@Test
	public void testUnit() throws Exception{
		NumberInput ni = getInput();
		ni.setUnit("blocks");
		assertEquals("blocks",ni.getUnit());
	}
	
	
	@Test
	public void testType() throws Exception{
		assertEquals(getExpectedType(), getInput().getType());
	}


	/** get the html5 type expected.
	 * 
	 * If you override the getType method on the input the test needs to override
	 * this to match
	 * 
	 * @return
	 */
	protected String getExpectedType() {
		return "number";
	}
	
	@Test
	public void testParseNull() throws Exception{
		NumberInput ni = getInput();
		ni.parse(null);
		assertNull(ni.getValue());
		
		ni.parse("");
		assertNull(ni.getValue());
	}

	@Test
	public void testConvert() throws Exception{
		NumberInput ni = getInput();
		
		for(N num : getGoodData()){
			assertEquals(num, ni.convert(num));
			assertEquals(num, ni.convert(ni.getString(num)));
			// won't work for inputs with a parse scale
			//assertEquals(num, ni.convert(Double.parseDouble(num.toString())));
		}
	}


	@Override
	@Test
	public void testMin() throws Exception {
		bounded_tests.testMin();
		
	}


	@Override
	@Test
	public void testMax() throws Exception {
		bounded_tests.testMax();
		
	}


	@Override
	@Test
	public void testBoth() throws Exception {
		bounded_tests.testBoth();
		
	}


	@Override
	@Test
	public void testAll() throws Exception {
		bounded_tests.testAll();
		
	}
}