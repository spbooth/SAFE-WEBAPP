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
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.RangeSearch;

/**
 * @author Stephen Booth
 *
 */
public class RangeAllocatorTest extends WebappTestBase {

	private Dummy1.Factory fac;
	private RangeSearch search;
	/**
	 * 
	 */
	public RangeAllocatorTest() {
		
	}

	@Test
	public void testRangeAllocator() throws DataException {
		fac = new Dummy1.Factory(ctx);
		search = new RangeSearch<>(fac, Dummy1.NUMBER);
		
		assertEquals(7L, allocate(7L, 9L));
		assertEquals(8L, allocate(7L, 9L));
		assertEquals(9L, allocate(7L, 9L));
		assertEquals(-1L, allocate(7L, 9L));
		
		
		assertEquals(2L, allocate(2L, 5L));
		assertEquals(3L, allocate(2L, 5L));
		assertEquals(4L, allocate(2L, 5L));
		assertEquals(5L, allocate(2L, 5L));
		assertEquals(-1L, allocate(2L, 5L));
		
		assertNotEquals(-1L, allocate(1L, 9L));
		assertNotEquals(-1L, allocate(1L, 9L));
		assertEquals(-1L, allocate(1L, 9L));
	}
	
	@Test
	public void testRangeAllocator2() throws DataException {
		fac = new Dummy1.Factory(ctx);
		search = new RangeSearch<>(fac, Dummy1.NUMBER);
		
		assertEquals(7L, allocate(7L, 9L));
		assertEquals(8L, allocate(7L, 9L));
		assertEquals(9L, allocate(7L, 9L));
		assertEquals(-1L, allocate(7L, 9L));
		
		
		
		assertEquals(4L, allocate(4L, 5L));
		assertEquals(5L, allocate(4L, 5L));
		assertEquals(-1L, allocate(4L, 5L));
		
		assertNotEquals(-1L, allocate(1L, 9L));
		assertNotEquals(-1L, allocate(1L, 9L));
		assertNotEquals(-1L, allocate(1L, 9L));
		assertNotEquals(-1L, allocate(1L, 9L));
		assertEquals(-1L, allocate(1L, 9L));
	}
	@Test
	public void testRangeAllocator3() throws DataException {
		fac = new Dummy1.Factory(ctx);
		search = new RangeSearch<>(fac, Dummy1.NUMBER);
		
		assertEquals(17L, allocate(17L, 19L));
		assertEquals(18L, allocate(17L, 19L));
		assertEquals(19L, allocate(17L, 19L));
		assertEquals(-1L, allocate(17L, 19L));
		
		
		
		assertEquals(14L, allocate(14L, 15L));
		assertEquals(15L, allocate(14L, 15L));
		assertEquals(-1L, allocate(14L, 15L));
		
		assertEquals(1L, allocate(1L, 1L));
		
		for(int i=6; i<19 ; i++ ) {
			assertNotEquals(-1L, allocate(1L, 19L));
		}
		
		assertEquals(-1L, allocate(1L, 9L));
	}
	private long allocate(long min, long max) throws DataException {
		long res = search.search(min, max);
		if( res >= 0 ) {
			Dummy1 d = fac.makeBDO();
			d.setNumber((int) res);
			d.commit();
		}
		return res;
	}
}
