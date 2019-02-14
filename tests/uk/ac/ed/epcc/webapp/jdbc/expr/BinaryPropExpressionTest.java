//| Copyright - The University of Edinburgh 2014                            |
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

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;

/**
 * @author spb
 *
 */

public class BinaryPropExpressionTest extends WebappTestBase {

	
	
	@Test
	public void testMergeConst(){
		
		assertEquals(new ConstExpression(Number.class, 12L),BinaryExpression.create(ctx, new ConstExpression(Number.class, 4L), Operator.MUL, new ConstExpression(Number.class, 3L)));
	}
	@Test
	public void testSubtractSelf(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression answer = BinaryExpression.create(ctx, exp, Operator.SUB, exp);
		assertTrue( answer instanceof ConstExpression);
		assertEquals(Double.valueOf(0.0), Double.valueOf(((Number)((ConstExpression)answer).getValue()).doubleValue()));
	}
	@Test
	public void testDivideSelf(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression answer = BinaryExpression.create(ctx, exp, Operator.DIV, exp);
		assertTrue( answer instanceof ConstExpression);
		assertEquals(Double.valueOf(1.0), Double.valueOf(((Number)((ConstExpression)answer).getValue()).doubleValue()));
	}
	
	@Test
	public void testAone(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		assertEquals(exp, BinaryExpression.create(ctx, new ConstExpression(Number.class, 1.0), Operator.MUL,exp));
		
	}
	@Test
	public void testBone(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		assertEquals(exp, BinaryExpression.create(ctx, exp, Operator.MUL,new ConstExpression(Number.class, 1.0)));
		
	}
	@Test
	public void testAMulzero(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		assertEquals(new ConstExpression(Number.class, 0.0), BinaryExpression.create(ctx, new ConstExpression(Number.class, 0.0), Operator.MUL,exp));
		
	}
	@Test
	public void testAaddzero(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		assertEquals(exp, BinaryExpression.create(ctx, new ConstExpression(Number.class, 0.0), Operator.ADD,exp));
		
	}
	@Test
	public void testBMulzero(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		assertEquals(new ConstExpression(Number.class, 0.0), BinaryExpression.create(ctx, exp, Operator.MUL,new ConstExpression(Number.class, 0.0)));
		
	}
	@Test
	public void testBAddzero(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		assertEquals(exp, BinaryExpression.create(ctx, exp, Operator.ADD,new ConstExpression(Number.class, 0.0)));
		
	}
	@Test
	public void testHeadConst1(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		// (4*(4*A)) = 16 * A
		SQLExpression answer = BinaryExpression.create(ctx, new ConstExpression(Number.class, 4.0), Operator.MUL, BinaryExpression.create(ctx, new ConstExpression(Number.class, 4.0), Operator.MUL, exp));
		SQLExpression expected = new BinaryExpression(new ConstExpression(Number.class, 16.0), Operator.MUL, exp);
		assertEquals(expected, answer);
	}
	@Test
	public void testHeadConst2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		// (4*(4*A)) = 16 * A
		SQLExpression answer = BinaryExpression.create(ctx, new ConstExpression(Number.class, 4.0), Operator.MUL, BinaryExpression.create(ctx, exp, Operator.MUL,new ConstExpression(Number.class, 4.0)));
		SQLExpression expected = new BinaryExpression(new ConstExpression(Number.class, 16.0), Operator.MUL, exp);
		assertEquals(expected, answer);
	}
	@Test
	public void testTailConst1(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		
		// ((A*4)/4) = A
		assertEquals(exp, BinaryExpression.create(ctx,BinaryExpression.create(ctx, exp, Operator.MUL,new ConstExpression(Number.class, 4.0)),Operator.DIV,new ConstExpression(Number.class, 4.0) ));
	}
	@Test
	public void testTailConst2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		// (4*A)/4) = A
		assertEquals(exp, BinaryExpression.create(ctx,BinaryExpression.create(ctx, new ConstExpression(Number.class, 4.0), Operator.MUL,exp),Operator.DIV,new ConstExpression(Number.class, 4.0) ));
	}
	@Test
	public void testTailConst3(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
        // ((A+4)-4) = A
		assertEquals(exp, BinaryExpression.create(ctx,BinaryExpression.create(ctx, exp, Operator.ADD,new ConstExpression(Number.class, 4.0)),Operator.SUB,new ConstExpression(Number.class, 4.0) ));
	}
	@Test
	public void testTailConst4(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		// ((4+A)-4) = A
		assertEquals(exp, BinaryExpression.create(ctx,BinaryExpression.create(ctx, new ConstExpression(Number.class, 4.0), Operator.ADD,exp),Operator.SUB,new ConstExpression(Number.class, 4.0) ));
	}
	@Test
	public void testTailConst5(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
	    // ((A-4)+4) = A  equality is right but this would require A-4 -> A+(-4)
		SQLExpression expected = new BinaryExpression(new BinaryExpression(exp, Operator.SUB, new ConstExpression(Number.class, 4.0)), Operator.ADD, new ConstExpression(Number.class, 4.0));
		SQLExpression<Number> answer = BinaryExpression.create(ctx,BinaryExpression.create(ctx, exp, Operator.SUB,new ConstExpression(Number.class, 4.0)),Operator.ADD,new ConstExpression(Number.class, 4.0) );
		assertEquals(expected, answer);
		assertEquals(expected.hashCode(), answer.hashCode());
	}
	@Test
	public void testTailConst6(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();

		// ((4-A)+4 != A
		SQLExpression expected = new BinaryExpression(new BinaryExpression(new ConstExpression(Number.class, 4.0), Operator.SUB,exp ), Operator.ADD, new ConstExpression(Number.class, 4.0));
		SQLExpression<Number> answer = BinaryExpression.create(ctx,BinaryExpression.create(ctx, new ConstExpression(Number.class, 4.0), Operator.SUB,exp),Operator.ADD,new ConstExpression(Number.class, 4.0) );
		assertEquals(expected, answer);
		assertEquals(expected.hashCode(), answer.hashCode());
	
	}
	
	
	@Test
	public void testCommonPreFactor(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(four, Operator.MUL, new BinaryExpression(exp, Operator.ADD, exp2));
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,four , Operator.MUL, exp), 
				Operator.ADD,
				BinaryExpression.create(ctx, four, Operator.MUL, exp2));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonPreFactor2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(four, Operator.MUL, new BinaryExpression(exp, Operator.SUB, exp2));
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,four , Operator.MUL, exp), 
				Operator.SUB,
				BinaryExpression.create(ctx, four, Operator.MUL, exp2));
		assertEquals(expected, actual);
	}
	@Test
	public void testCrossFactor(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression factor = new BinaryExpression(exp, Operator.ADD, exp2);
		BinaryExpression actual = (BinaryExpression) BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.MUL, four), 
				Operator.ADD,
				BinaryExpression.create(ctx, four, Operator.MUL, exp2));
		assertEquals(Operator.MUL, actual.getOp());
		assertTrue( actual.getA().equals(four) || actual.getB().equals(four));
		assertTrue( actual.getA().equals(factor) || actual.getB().equals(factor));
		assertNotEquals(actual.getA(), actual.getB());
	}
	@Test
	public void testCrossFactor2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression factor = new BinaryExpression(exp, Operator.ADD, exp2);
		BinaryExpression actual = (BinaryExpression) BinaryExpression.create(ctx, BinaryExpression.create(ctx,four , Operator.MUL, exp), 
				Operator.ADD,
				BinaryExpression.create(ctx, exp2, Operator.MUL, four));
		assertEquals(Operator.MUL, actual.getOp());
		assertTrue( actual.getA().equals(four) || actual.getB().equals(four));
		assertTrue( actual.getA().equals(factor) || actual.getB().equals(factor));
		assertNotEquals(actual.getA(), actual.getB());
	}
	@Test
	public void testCommonPostFactor(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression( new BinaryExpression(exp, Operator.ADD, exp2),Operator.MUL,four);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.MUL, four), 
				Operator.ADD,
				BinaryExpression.create(ctx, exp2, Operator.MUL, four));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonPostFactor2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(new BinaryExpression(exp, Operator.SUB, exp2),Operator.MUL,four);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.MUL, four), 
				Operator.SUB,
				BinaryExpression.create(ctx,exp2, Operator.MUL, four));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonPostDivisor(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(new BinaryExpression(exp, Operator.SUB, exp2),Operator.DIV,four);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.DIV, four), 
				Operator.SUB,
				BinaryExpression.create(ctx,exp2, Operator.DIV, four));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonPostDivisor2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(new BinaryExpression(exp, Operator.SUB, exp2),Operator.DIV,four);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.DIV, four), 
				Operator.SUB,
				BinaryExpression.create(ctx,exp2, Operator.DIV, four));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCommonFactor(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(exp, Operator.DIV, exp2);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.MUL, four), 
				Operator.DIV,
				BinaryExpression.create(ctx,exp2, Operator.MUL, four));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonFactor2(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(exp, Operator.DIV, exp2);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,exp , Operator.MUL, four), 
				Operator.DIV,
				BinaryExpression.create(ctx,four, Operator.MUL, exp2));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonFactor3(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(exp, Operator.DIV, exp2);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,four , Operator.MUL, exp), 
				Operator.DIV,
				BinaryExpression.create(ctx,exp2, Operator.MUL, four));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommonFactor4(){
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		// (4*A)+(4*B)  = 4*(A+B)
		ConstExpression four = new ConstExpression(Number.class, 4.0);
		SQLExpression expected = new BinaryExpression(exp, Operator.DIV, exp2);
		SQLExpression actual = BinaryExpression.create(ctx, BinaryExpression.create(ctx,four , Operator.MUL, exp), 
				Operator.DIV,
				BinaryExpression.create(ctx,four, Operator.MUL, exp2));
		assertEquals(expected, actual);
	}
	@Test
	public void testCommutativeEquals() {
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		SQLExpression a = BinaryExpression.create(ctx, exp, Operator.MUL, exp2);
		SQLExpression b = BinaryExpression.create(ctx, exp2, Operator.MUL, exp);
		assertTrue("Comutative equals",a.equals(b));
		assertTrue("Comutative equals rev",b.equals(a));
		assertTrue("Comutative equals self",a.equals(a));
		assertEquals("hash", a.hashCode(),b.hashCode());
	}
	
	@Test
	public void testNonCommutativeEquals() {
		NumberFactory fac = new NumberFactory(ctx);
		SQLExpression exp = fac.getExpr();
		SQLExpression exp2 = fac.getExpr2();
		SQLExpression a = BinaryExpression.create(ctx, exp, Operator.DIV, exp2);
		SQLExpression b = BinaryExpression.create(ctx, exp2, Operator.DIV, exp);
		assertFalse("Comutative equals",a.equals(b));
		assertFalse("Comutative equals rev",b.equals(a));
		assertTrue("Comutative equals self",a.equals(a));
	
	}
}