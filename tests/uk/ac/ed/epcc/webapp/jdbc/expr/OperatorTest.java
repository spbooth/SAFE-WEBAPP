// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.jdbc.expr;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link Operator}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
