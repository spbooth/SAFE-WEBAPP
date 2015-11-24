// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data.iterator;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;

import org.junit.Test;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class NestedIteratorTest {

	@Test
	public void test() {
		NestedIterator<Number> it = new NestedIterator<Number>();
		LinkedHashSet<Number> set1= new LinkedHashSet<Number>();
		set1.add(1.0);
		set1.add(2.0);
		LinkedHashSet<Number> set2= new LinkedHashSet<Number>();
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
