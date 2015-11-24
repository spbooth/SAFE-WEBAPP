/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.LinkDummy.DummyLink;


public class LinkDummyTest extends WebappTestBase {

	
	

	@Rule public TestName name = new TestName();
	
	
	
	
	
	@Test
	public void testLink() throws Exception{
		LinkDummy linker = LinkDummy.getInstance(ctx);
	    Dummy1.Factory d1_fac = new Dummy1.Factory(ctx);
	    Dummy2.Factory d2_fac = new Dummy2.Factory(ctx);
		
		Dummy1 d1 = new Dummy1(ctx);
		d1.setName("fred");
		d1.setNumber(5);
		d1.commit();
		Dummy2 d2 = new Dummy2(ctx);
		d2.setName("boris");
		d2.setNumber(8);
		d2.commit();
		
		assertFalse(linker.isLinked(d1,d2));
		linker.addLink(d1,d2);
		assertTrue(linker.isLinked(d1,d2));
		linker.removeLink(d1,d2);
		assertFalse(linker.isLinked(d1,d2));
		DummyLink l = linker.getLink(d1,d2);
		assertEquals(d1,l.getDummy1());
		assertEquals(d2,l.getDummy2());
	}

	static final int size = 10;
	@Test
	public void testMany() throws Exception{
		LinkDummy linker = LinkDummy.getInstance(ctx);
	    Dummy1.Factory d1_fac = new Dummy1.Factory(ctx);
	    Dummy2.Factory d2_fac = new Dummy2.Factory(ctx);
		//TestDataHelper.loadDataSetsForTest("LinkDummyTest."+name.getMethodName());
		
		Dummy1 d1[] = new Dummy1[size];
		Dummy2 d2[] = new Dummy2[size];
		
		for(int i=0 ; i< size ; i++){
			d1[i]= new Dummy1(ctx);
			d1[i].setName("fred");
			d1[i].setNumber(i);
			d1[i].commit();
			d2[i]= new Dummy2(ctx);
			d2[i].setName("boris");
			d2[i].setNumber(i*10);
			d2[i].commit();
		}
		
		for(int i=0;i<size;i++){
			for(int j=i+1;j<size;j++){
				assertNotNull(d1[i]);
				assertNotNull(d2[j]);
				linker.addLink(d1[i],d2[j]);
			}
		}
		
		for(int i=0; i< size; i++){
			System.out.println("get high");
			assertNotNull(d1[i]);
			Set high = linker.getDummy2(d1[i]);
			System.out.println("Get low");
			assertNotNull(d2[i]);
			Set low = linker.getDummy1(d2[i]);
			
			assertEquals("I = "+i,high.size(), size -1 -i);
			assertEquals(low.size(), i);
			for(int j=0;j<size;j++){
				if( j < i ){
					assertTrue(low.contains(d1[j]));
				}
				if( j > i){
					assertTrue(high.contains(d2[j]));
				}
				if( i == j ){
					assertFalse(low.contains(d1[j]));
					assertFalse(high.contains(d2[j]));
				}
			}
			for(Iterator it = low.iterator(); it.hasNext();){
				Dummy1 o = (Dummy1) it.next();
				assertEquals(o.getName(),"fred");
				assertTrue(o.getNumber() < i);
			}
			for(Iterator it = high.iterator(); it.hasNext();){
				Dummy2 o = (Dummy2) it.next();
				assertEquals(o.getName(),"boris");
				assertTrue((o.getNumber()/10) > i);
			}
	
		}
		
	}
}
