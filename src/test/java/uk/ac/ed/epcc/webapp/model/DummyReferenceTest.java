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
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class DummyReferenceTest extends WebappTestBase{

	/**
	 * 
	 */
	public DummyReferenceTest() {
		
	}
	
	@Before
	public void setup() throws DataFault{
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		Dummy1 array[] = new Dummy1[17];
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
			array[i]=d;
		}
		
		for( int i=0 ; i<17 ; i ++){
			DummyReference ref = ref_fac.makeBDO();
			ref.setName("Ref"+i);
			ref.setReference(array[i%3]);
			ref.setNumber(i);
			ref.commit();
		}
	}
	
	@Test
	public void testGetByRemoteName() throws DataFault{
		
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		FilterResult<DummyReference> result = ref_fac.getResult(ref_fac.getRemoteNameFilter("Test2"));
		Collection<DummyReference> coll = result.toCollection();
		
		assertTrue(coll.size() > 0);
		
		for( DummyReference ref : coll ){
			int i = ref.getNumber();
			assertTrue(i%3 == 2);
		}
		
		
		
	}
	
	@Test
	public void testGetByRemoteNumber() throws DataFault{
		
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		FilterResult<DummyReference> result = ref_fac.getResult(ref_fac.getRemoteNumberFilter(14));
		Collection<DummyReference> coll = result.toCollection();
		
		assertTrue(coll.size() > 0);
		
		for( DummyReference ref : coll ){
			int i = ref.getNumber();
			assertTrue(i%3 == 2);
		}
		
		
		
	}
	
	@Test
	public void testGetByRemoteNumberAccept() throws DataFault{
		
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		FilterResult<DummyReference> result = ref_fac.getResult(ref_fac.getRemoteNumberAcceptFilter(14));
		Collection<DummyReference> coll = result.toCollection();
		
		assertTrue(coll.size() > 0);
		
		for( DummyReference ref : coll ){
			int i = ref.getNumber();
			assertTrue(i%3 == 2);
		}
		
		
		
	}
	@Test
	public void testGetByRemoteNumberAnd() throws DataFault{
		
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		FilterResult<DummyReference> result = ref_fac.getResult(ref_fac.getRemoteNumberAndFilter(14));
		Collection<DummyReference> coll = result.toCollection();
		
		assertTrue(coll.size() > 0);
		
		for( DummyReference ref : coll ){
			int i = ref.getNumber();
			assertTrue(i%3 == 2);
		}
		
		
		
	}
	@Test
	public void testGetByRemoteNumberDual() throws DataFault{
		
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		FilterResult<DummyReference> result = ref_fac.getResult(ref_fac.getRemoteNumberDualFilter(14));
		Collection<DummyReference> coll = result.toCollection();
		
		assertTrue(coll.size() > 0);
		
		for( DummyReference ref : coll ){
			int i = ref.getNumber();
			assertTrue(i%3 == 2);
		}
		
		
		
	}
	@Test
	public void testByReferenceName() throws DataFault{
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		
		Iterator<Dummy1> it = fac.getResult(ref_fac.getReferencedFilter("Ref11")).iterator();
		
		assertTrue(it.hasNext());
		Dummy1 peer = it.next();
		assertEquals("Test2", peer.getName());
		
		assertFalse(it.hasNext());
		
	}
	
	
	

}