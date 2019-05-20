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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link Operator}.
 * 
 * @author aheyrovs
 *
 */

public class OperatorTest {
	
	/**
	 * Tests the method {@link Operator#operate(Number, Number)}.
	 */
	@Test
	public void testOperate() {
		// tests ADD
		Assert.assertEquals(10, Operator.ADD.operate(6, 4));
		// tests SUB
		Assert.assertEquals(8, Operator.SUB.operate(13, 5));
		// tests MUL
		Assert.assertEquals(18, Operator.MUL.operate(2, 9));
		// tests DIV
		Assert.assertEquals(2.5, Operator.DIV.operate(5, 2));
	}
	
	/**
	 * Tests the method {@link Operator#text()}.
	 */
	@Test
	public void testText() {
		// tests ADD
		Assert.assertEquals("+", Operator.ADD.text());
		// tests SUB
		Assert.assertEquals("-", Operator.SUB.text());
		// tests MUL
		Assert.assertEquals("*", Operator.MUL.text());
		// tests DIV
		Assert.assertEquals("/", Operator.DIV.text());
	}

}