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