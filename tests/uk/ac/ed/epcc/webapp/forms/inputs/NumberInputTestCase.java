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
