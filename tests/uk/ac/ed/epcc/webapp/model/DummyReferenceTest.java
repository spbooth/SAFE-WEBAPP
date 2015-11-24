// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class DummyReferenceTest extends WebappTestBase{

	/**
	 * 
	 */
	public DummyReferenceTest() {
		// TODO Auto-generated constructor stub
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
	public void testByReferenceName() throws DataFault{
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		
		Iterator<Dummy1> it = fac.getResult(ref_fac.getReferencedFilter("Ref11")).iterator();
		
		assertTrue(it.hasNext());
		Dummy1 peer = it.next();
		assertEquals("Test2", peer.getName());
		
		assertFalse(it.hasNext());
		
	}
	
	@Test
	public void testReferencedIterator() throws DataFault{
		DummyReferenceFactory ref_fac = new DummyReferenceFactory(ctx);
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		
		Set<Dummy1> set = ref_fac.geReferencedDummy("Ref11");
		
		assertEquals(1, set.size());
		Dummy1 peer = set.iterator().next();
		assertEquals("Test2", peer.getName());
		
	}

}
