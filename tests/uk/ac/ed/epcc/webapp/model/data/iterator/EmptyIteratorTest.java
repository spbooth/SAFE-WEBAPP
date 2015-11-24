// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data.iterator;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class EmptyIteratorTest {

	@Test
	public void test() {
		EmptyIterator it=new EmptyIterator();
		
		assertFalse(it.hasNext());
	}

	@Test
	public void testRemove() throws Exception{
		Iterator it = new EmptyIterator();
		
		try{
			it.remove();
			throw new Exception("Should not get here");
		}catch(UnsupportedOperationException e){
			
		}
	}
	
	@Test
	public void testNext() throws Exception{
		Iterator it = new EmptyIterator();
		
		try{
			it.next();
			throw new Exception("Should not get here");
		}catch(NoSuchElementException e){
			
		}
	}
}
