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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * @author spb
 *
 */

public class SortingIteratorTest {

	@Test
	public void test() {
		LinkedList list = new LinkedList();
		
		list.add(1.0);
		list.add(13.0);
		list.add(3.0);
		list.add(2.0);
		list.add(3.0);
		
		SortingIterator it = new SortingIterator(list.iterator(), null);
		
		for(Number n : new Number[] {1.0, 2.0, 3.0, 13.0}){
			assertTrue(it.hasNext());
			assertEquals(n, it.next());
			
		}
		assertFalse(it.hasNext());
		
	}
	
	@Test
	public void testReverse() {
		LinkedList<Number> list = new LinkedList<>();
		
		list.add(1.0);
		list.add(13.0);
		list.add(3.0);
		list.add(2.0);
		list.add(3.0);
		
		SortingIterator it = new SortingIterator<>(list.iterator(), new Comparator<Number>(){

			public int compare(Number o1, Number o2) {
				return (int) (o2.doubleValue() - o1.doubleValue());
			}
			
		});
		
		for(Number n : new Number[] {13.0, 3.0, 2.0, 1.0}){
			assertTrue(it.hasNext());
			assertEquals(n, it.next());
			
		}
		assertFalse(it.hasNext());
		
	}
	@Test
	public void testNext() throws Exception{
		HashSet set = new HashSet();
		Iterator it = new SortingIterator(set.iterator(), null);
		
		try{
			it.next();
			throw new Exception("Should not get here");
		}catch(NoSuchElementException e){
			
		}
	}
}