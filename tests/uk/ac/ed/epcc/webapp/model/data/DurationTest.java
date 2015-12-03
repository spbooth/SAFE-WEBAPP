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
package uk.ac.ed.epcc.webapp.model.data;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author spb
 *
 */

public class DurationTest {

	@Test
	public void TestComparable(){
	   Duration ten = new Duration(10);
	   Duration fifty = new Duration(50);
	 
	   assertTrue(ten.compareTo(fifty) < 0);
	   assertTrue(fifty.compareTo(ten) > 0);
	   assertEquals(ten.compareTo(ten), 0);
	}

	@Test
	public void testValue(){
		Duration onesecond = new Duration(1);
		assertEquals(onesecond.longValue(), 1000L);
		assertEquals(onesecond.getSeconds(), 1);
		assertEquals(onesecond.getMilliseconds(), 1000L);
		
		Duration onemilli = new Duration(1,1L);
		assertEquals(1L,onemilli.longValue());
		assertEquals(0,onemilli.getSeconds());
		assertEquals(1L,onemilli.getMilliseconds());	
	}
	
	
	@Test
	public void testSubtract(){
		Duration tens = new Duration(10);
		Duration threes = new Duration(3);
		
		Duration diff = tens.subtract(threes);
		
		assertEquals(7,diff.getSeconds());
		assertEquals(7000L, diff.getMilliseconds());
		assertEquals(7000L, diff.longValue());
	}
	
	
	@Test
	public void testadd(){
		Duration tens = new Duration(10);
		Duration threes = new Duration(3);
		
		Duration diff = tens.add(threes);
		
		assertEquals(13,diff.getSeconds());
		assertEquals(13000L, diff.getMilliseconds());
		assertEquals(13000L, diff.longValue());
	}
	
	
	
	
}