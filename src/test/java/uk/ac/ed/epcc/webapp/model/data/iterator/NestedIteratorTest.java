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
package uk.ac.ed.epcc.webapp.model.data.iterator;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class NestedIteratorTest {

	@Test
	public void test() throws DataFault {
		NestedIterator<Number> it = new NestedIterator<>();
		LinkedHashSet<Number> set1= new LinkedHashSet<>();
		set1.add(1.0);
		set1.add(2.0);
		LinkedHashSet<Number> set2= new LinkedHashSet<>();
		set2.add(3.0);
		set2.add(4.0);
		
		it.add(set1.iterator());
		it.add(set2.iterator());
		Number expected[] = new Number[] {1.0,2.0,3.0,4.0};
		for(Number n : expected){
			assertTrue(it.hasNext());
			assertEquals(n, it.next());
		}
		
		assertFalse(it.hasNext());
	}

	@Test
	public void testRemove() throws Exception{
		NestedIterator it = new NestedIterator();
		
		try{
			it.remove();
			throw new Exception("Should not get here");
		}catch(UnsupportedOperationException e){
			
		}
	}
}