//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.expr.CountDistinctExpression;
import uk.ac.ed.epcc.webapp.model.data.Duration;

/**
 * @author Stephen Booth
 *
 */
public class NumberOpTest {

	/**
	 * 
	 */
	public NumberOpTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testAddInt() {
		assertEquals(Integer.valueOf(7), NumberOp.add(Integer.valueOf(5), Integer.valueOf(2)));
		assertEquals(Integer.valueOf(3), NumberOp.add(Integer.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Integer.valueOf(-15), NumberOp.add(Integer.valueOf(5), Integer.valueOf(-20)));
	}
	@Test
	public void testSubInt() {
		assertEquals(Integer.valueOf(3), NumberOp.sub(Integer.valueOf(5), Integer.valueOf(2)));
		assertEquals(Integer.valueOf(7), NumberOp.sub(Integer.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Integer.valueOf(25), NumberOp.sub(Integer.valueOf(5), Integer.valueOf(-20)));
		assertEquals(Integer.valueOf(5), NumberOp.sub(Integer.valueOf(5), null));
		assertEquals(Integer.valueOf(-5), NumberOp.sub(null,Integer.valueOf(5)));
	}
	@Test
	public void testMulInt() {
		assertEquals(Integer.valueOf(10), NumberOp.mult(Integer.valueOf(5), Integer.valueOf(2)));
		assertEquals(Integer.valueOf(-10), NumberOp.mult(Integer.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Integer.valueOf(-100), NumberOp.mult(Integer.valueOf(5), Integer.valueOf(-20)));
	}
	@Test
	public void testDivInt() {
		assertEquals(Integer.valueOf(2), NumberOp.div(Integer.valueOf(5), Integer.valueOf(2)));
		assertEquals(Integer.valueOf(-2), NumberOp.div(Integer.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Integer.valueOf(0), NumberOp.div(Integer.valueOf(5), Integer.valueOf(-20)));
	}
	@Test
	public void testMinInt() {
		assertEquals(Integer.valueOf(2), NumberOp.min(Integer.valueOf(5), Integer.valueOf(2)));
		assertEquals(Integer.valueOf(-2), NumberOp.min(Integer.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Integer.valueOf(-20), NumberOp.min(Integer.valueOf(5), Integer.valueOf(-20)));
	}
	@Test
	public void testMaxInt() {
		assertEquals(Integer.valueOf(5), NumberOp.max(Integer.valueOf(5), Integer.valueOf(2)));
		assertEquals(Integer.valueOf(5), NumberOp.max(Integer.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Integer.valueOf(5), NumberOp.max(Integer.valueOf(5), Integer.valueOf(-20)));
	}
	@Test
	public void testAddLong() {
		assertEquals(Long.valueOf(7), NumberOp.add(Integer.valueOf(5), Long.valueOf(2)));
		assertEquals(Long.valueOf(3), NumberOp.add(Long.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Long.valueOf(-15), NumberOp.add(Long.valueOf(5), Long.valueOf(-20)));
	}
	@Test
	public void testSubLong() {
		assertEquals(Long.valueOf(3), NumberOp.sub(Integer.valueOf(5), Long.valueOf(2)));
		assertEquals(Long.valueOf(7), NumberOp.sub(Long.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Long.valueOf(25), NumberOp.sub(Long.valueOf(5), Long.valueOf(-20)));
		assertEquals(Long.valueOf(5), NumberOp.sub(Long.valueOf(5), null));
		assertEquals(Long.valueOf(-5), NumberOp.sub(null,Long.valueOf(5)));
	}
	@Test
	public void testMulLong() {
		assertEquals(Long.valueOf(10), NumberOp.mult(Integer.valueOf(5), Long.valueOf(2)));
		assertEquals(Long.valueOf(-10), NumberOp.mult(Long.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Long.valueOf(-100), NumberOp.mult(Long.valueOf(5), Long.valueOf(-20)));
	}
	@Test
	public void testDivLong() {
		assertEquals(Long.valueOf(2), NumberOp.div(Integer.valueOf(5), Long.valueOf(2)));
		assertEquals(Long.valueOf(-2), NumberOp.div(Long.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Long.valueOf(0), NumberOp.div(Long.valueOf(5), Long.valueOf(-20)));
	}
	@Test
	public void testMinLong() {
		assertEquals(Long.valueOf(2), NumberOp.min(Integer.valueOf(5), Long.valueOf(2)));
		assertEquals(Integer.valueOf(-2), NumberOp.min(Long.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Long.valueOf(-20), NumberOp.min(Long.valueOf(5), Long.valueOf(-20)));
	}
	@Test
	public void testMaxLong() {
		assertEquals(Integer.valueOf(5), NumberOp.max(Integer.valueOf(5), Long.valueOf(2)));
		assertEquals(Long.valueOf(5), NumberOp.max(Long.valueOf(5), Integer.valueOf(-2)));
		assertEquals(Long.valueOf(5), NumberOp.max(Long.valueOf(5), Long.valueOf(-20)));
	}
	@Test
	public void testAddFloat() {
		assertEquals(Float.valueOf(7), NumberOp.add(Integer.valueOf(5), Float.valueOf(2)));
		assertEquals(Float.valueOf(3), NumberOp.add(Long.valueOf(5), Float.valueOf(-2)));
		assertEquals(Float.valueOf(-15), NumberOp.add(Float.valueOf(5), Float.valueOf(-20)));
	}
	@Test
	public void testSubFloat() {
		assertEquals(Float.valueOf(3), NumberOp.sub(Integer.valueOf(5), Float.valueOf(2)));
		assertEquals(Float.valueOf(7), NumberOp.sub(Long.valueOf(5), Float.valueOf(-2)));
		assertEquals(Float.valueOf(25), NumberOp.sub(Float.valueOf(5), Float.valueOf(-20)));
		assertEquals(Float.valueOf(5), NumberOp.sub(Float.valueOf(5), null));
		assertEquals(Float.valueOf(-5), NumberOp.sub(null,Float.valueOf(5)));
	}
	@Test
	public void testMulFloat() {
		assertEquals(Float.valueOf(10), NumberOp.mult(Integer.valueOf(5), Float.valueOf(2)));
		assertEquals(Float.valueOf(-10), NumberOp.mult(Long.valueOf(5), Float.valueOf(-2)));
		assertEquals(Float.valueOf(-100), NumberOp.mult(Float.valueOf(5), Float.valueOf(-20)));
	}
	@Test
	public void testDivFloat() {
		assertEquals(Float.valueOf(2.5f), NumberOp.div(Integer.valueOf(5), Float.valueOf(2)));
		assertEquals(Float.valueOf(-2.5f), NumberOp.div(Long.valueOf(5), Float.valueOf(-2)));
		assertEquals(Float.valueOf(-0.25f), NumberOp.div(Float.valueOf(5), Float.valueOf(-20)));
	}
	@Test
	public void testMinFloat() {
		assertEquals(Float.valueOf(2), NumberOp.min(Integer.valueOf(5), Float.valueOf(2)));
		assertEquals(Float.valueOf(-2), NumberOp.min(Long.valueOf(5), Float.valueOf(-2)));
		assertEquals(Float.valueOf(-20), NumberOp.min(Float.valueOf(5), Float.valueOf(-20)));
	}
	@Test
	public void testMaxFloat() {
		assertEquals(Integer.valueOf(5), NumberOp.max(Integer.valueOf(5), Float.valueOf(2)));
		assertEquals(Long.valueOf(5), NumberOp.max(Long.valueOf(5), Float.valueOf(-2)));
		assertEquals(Float.valueOf(5), NumberOp.max(Float.valueOf(5), Float.valueOf(-20)));
	}
	@Test
	public void testAddDouble() {
		assertEquals(Double.valueOf(7), NumberOp.add(Integer.valueOf(5), Double.valueOf(2)));
		assertEquals(Double.valueOf(3), NumberOp.add(Long.valueOf(5), Double.valueOf(-2)));
		assertEquals(Double.valueOf(-15), NumberOp.add(Double.valueOf(5), Float.valueOf(-20)));
		assertEquals(Double.valueOf(-15), NumberOp.add(Double.valueOf(5), Double.valueOf(-20)));
		assertEquals(Double.valueOf(5), NumberOp.add(Double.valueOf(5), null));
		assertEquals(Double.valueOf(5), NumberOp.add(null,Double.valueOf(5)));
	}
	@Test
	public void testSubDouble() {
		assertEquals(Double.valueOf(3), NumberOp.sub(Integer.valueOf(5), Double.valueOf(2)));
		assertEquals(Double.valueOf(7), NumberOp.sub(Long.valueOf(5), Double.valueOf(-2)));
		assertEquals(Double.valueOf(25), NumberOp.sub(Double.valueOf(5), Float.valueOf(-20)));
		assertEquals(Double.valueOf(25), NumberOp.sub(Double.valueOf(5), Double.valueOf(-20)));
		assertEquals(Double.valueOf(5), NumberOp.sub(Double.valueOf(5), null));
		assertEquals(Double.valueOf(-5), NumberOp.sub(null,Double.valueOf(5)));
	}
	@Test
	public void testMultDouble() {
		assertEquals(Double.valueOf(10), NumberOp.mult(Integer.valueOf(5), Double.valueOf(2)));
		assertEquals(Double.valueOf(-10), NumberOp.mult(Long.valueOf(5), Double.valueOf(-2)));
		assertEquals(Double.valueOf(-100), NumberOp.mult(Double.valueOf(5), Float.valueOf(-20)));
		assertEquals(Double.valueOf(-100), NumberOp.mult(Double.valueOf(5), Double.valueOf(-20)));
	}
	@Test
	public void testDivDouble() {
		assertEquals(Double.valueOf(2.5), NumberOp.div(Integer.valueOf(5), Double.valueOf(2)));
		assertEquals(Double.valueOf(-2.5), NumberOp.div(Long.valueOf(5), Double.valueOf(-2)));
		assertEquals(Double.valueOf(-0.25), NumberOp.div(Double.valueOf(5), Float.valueOf(-20)));
		assertEquals(Double.valueOf(-0.25), NumberOp.div(Double.valueOf(5), Double.valueOf(-20)));
	}
	@Test
	public void testMinDouble() {
		assertEquals(Double.valueOf(2), NumberOp.min(Integer.valueOf(5), Double.valueOf(2)));
		assertEquals(Double.valueOf(-2), NumberOp.min(Long.valueOf(5), Double.valueOf(-2)));
		assertEquals(Float.valueOf(-20), NumberOp.min(Double.valueOf(5), Float.valueOf(-20)));
		assertEquals(Double.valueOf(-20), NumberOp.min(Double.valueOf(5), Double.valueOf(-20)));
		assertEquals(Double.valueOf(5), NumberOp.min(Double.valueOf(5), Double.valueOf(20)));
		assertEquals(Double.valueOf(5), NumberOp.min(Double.valueOf(5), null));
		assertEquals(Double.valueOf(5), NumberOp.min(null,Double.valueOf(5)));
	}
	@Test
	public void testMaxDouble() {
		assertEquals(Integer.valueOf(5), NumberOp.max(Integer.valueOf(5), Double.valueOf(2)));
		assertEquals(Long.valueOf(5), NumberOp.max(Long.valueOf(5), Double.valueOf(-2)));
		assertEquals(Double.valueOf(5), NumberOp.max(Double.valueOf(5), Float.valueOf(-20)));
		assertEquals(Double.valueOf(5), NumberOp.max(Double.valueOf(5), Double.valueOf(-20)));
		assertEquals(Double.valueOf(20), NumberOp.max(Double.valueOf(5), Double.valueOf(20)));
		assertEquals(Double.valueOf(5), NumberOp.max(Double.valueOf(5), null));
		assertEquals(Double.valueOf(5), NumberOp.max(null,Double.valueOf(5)));
	}
	@Test
	public void testAddDuration() {
		assertEquals(new Duration(7), NumberOp.add(Integer.valueOf(5000), new Duration(2000,1L)));
		assertEquals(new Duration(3), NumberOp.add(new Duration(2000,1L),new Duration(1)));
	}
	
	
	@Test
	public void testAverage() {
		Number n = NumberOp.average(1, 2);
		assertEquals(1.5, n.doubleValue(),0.0001);
		n = NumberOp.average(n, 3);
		assertEquals(2.0, n.doubleValue(),0.0001);
		
		n = NumberOp.average(4, n);
		assertEquals(2.5, n.doubleValue(),0.0001);
	}
	
	@Test
	public void testMedian() {
		
		Number n  = NumberOp.median(1, 2);
		assertEquals(1.5, n.doubleValue(),0.0001);
		
		n = NumberOp.median(n, 100);
		assertEquals(2.0, n.doubleValue(),0.0001);
		
		n = NumberOp.median(4, n);
		assertEquals(3.0, n.doubleValue(),0.0001);
		
		n = NumberOp.median(1000, n);
		assertEquals(4.0, n.doubleValue(),0.0001);
		
		
		n = NumberOp.median(1, n);
		assertEquals(3.0, n.doubleValue(),0.0001);
		
		n = NumberOp.median(1000, n);
		assertEquals(4.0, n.doubleValue(),0.0001);
		
		n = NumberOp.median(2000, n);
		assertEquals(52.0, n.doubleValue(),0.0001);
		
		n = NumberOp.median(200, n);
		assertEquals(100.0, n.doubleValue(),0.0001);
	}
	
	@Test
	public void testCountDistinct() {
		DistinctCount n = DistinctCount.make("hello");
		assertEquals(1, n.intValue());
		n = (DistinctCount) NumberOp.add(n, DistinctCount.make("world"));
		assertEquals(2, n.intValue());
		
		n = (DistinctCount) NumberOp.add(n, DistinctCount.make("world"));
		assertEquals(2, n.intValue());
		n = (DistinctCount) NumberOp.add(n, DistinctCount.make("hello"));
		assertEquals(2, n.intValue());
		
	}
}
