/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AsciiConverterTest {

	AsciiConverter conv;
	
	@Before
	public void setUp() throws Exception {
		conv = new AsciiConverter();
	}
	
	@Test
	public void testConvert(){
		
		assertEquals("Hello world", conv.convert("Hello world"));
		//assertEquals("alfabetico", conv.convert("alfabético"));
		assertEquals("mele", conv.convert("m\u00e8l\u00e9"));
	}

}
