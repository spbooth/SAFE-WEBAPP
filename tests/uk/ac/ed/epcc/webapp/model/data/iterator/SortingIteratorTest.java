// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
		LinkedList<Number> list = new LinkedList<Number>();
		
		list.add(1.0);
		list.add(13.0);
		list.add(3.0);
		list.add(2.0);
		list.add(3.0);
		
		SortingIterator it = new SortingIterator<Number>(list.iterator(), new Comparator<Number>(){

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

