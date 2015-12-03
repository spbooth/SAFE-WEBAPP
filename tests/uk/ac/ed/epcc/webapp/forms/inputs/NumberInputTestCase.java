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

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.NumberInput;
import static org.junit.Assert.*;

public abstract class NumberInputTestCase<N extends Number,I extends NumberInput<N>> extends ParseAbstractInputTestCase<N,I> {

	
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
			assertEquals(num, ni.convert(num.toString()));
			assertEquals(num, ni.convert(Double.parseDouble(num.toString())));
		}
	}
}