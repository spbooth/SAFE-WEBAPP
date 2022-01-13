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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * @author spb
 *
 */

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