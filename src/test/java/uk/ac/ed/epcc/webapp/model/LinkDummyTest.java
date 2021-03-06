//| Copyright - The University of Edinburgh 2015                            |
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
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.LinkDummy.DummyLink;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;


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
	
	@Test
	public void testFilters() throws Exception {
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
		
		Dummy2 d3 = new Dummy2(ctx);
		d3.setName("enoch");
		d3.setNumber(6);
		d3.commit();
		assertFalse(linker.isLinked(d1,d2));
		linker.addLink(d1,d2);
		
		LinkDummy.DummyLink link = linker.find(linker.getLeftJoinFilter(d1_fac.getStringFilter("fred")));
		
		assertEquals("boris",link.getDummy2().getName());
		
		link = linker.find(linker.getLeftRemoteFilter(d1_fac.getStringFilter("fred")));
		
		assertEquals("boris",link.getDummy2().getName());
		
		link = linker.find(linker.getRightJoinFilter(d2_fac.getStringFilter("boris")));
		
		assertEquals("fred",link.getDummy1().getName());
		
		link = linker.find(linker.getRightRemoteFilter(d2_fac.getStringFilter("boris")));
		
		assertEquals("fred",link.getDummy1().getName());
		
		Dummy1 fred = d1_fac.find(linker.getLeftFilter(linker.getLinkedFilter()));
		assertEquals("fred",fred.getName());
		
		Dummy2 boris = d2_fac.find(linker.getRightFilter(linker.getLinkedFilter()));
		assertEquals("boris",boris.getName());
		
	}

	
	
	@Test
	public void testLinkCount() throws Exception {
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
		
		Dummy2 d3 = new Dummy2(ctx);
		d3.setName("enoch");
		d3.setNumber(6);
		d3.commit();
		assertFalse(linker.isLinked(d1,d2));
		linker.addLink(d1,d2);
		
		assertEquals(0,linker.getLinkCount(null, d3, null));
		assertEquals(1,linker.getLinkCount(d1, null, null));
		assertEquals(1,linker.getLinkCount(d1, d2, null));
		assertEquals(0,linker.getLinkCount(d1, d3, null));
		
		assertEquals(0,linker.getLinkCount(null, d3, linker.getLinkedFilter()));
		assertEquals(1,linker.getLinkCount(d1, null, linker.getLinkedFilter()));
		assertEquals(1,linker.getLinkCount(d1, d2, linker.getLinkedFilter()));
		assertEquals(0,linker.getLinkCount(d1, d3, linker.getLinkedFilter()));
	}
	
	@Test
	public void testLinkIterator() throws Exception {
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
		
		Dummy2 d3 = new Dummy2(ctx);
		d3.setName("enoch");
		d3.setNumber(6);
		d3.commit();
		assertFalse(linker.isLinked(d1,d2));
		linker.addLink(d1,d2);
		
		Iterator<DummyLink> it = linker.getLinkIterator(d1, null, null, true);
		assertTrue(it.hasNext());
		DummyLink link = it.next();
		assertEquals("fred",link.getDummy1().getName());
		
		
		it = linker.getLinkIterator(d1, null, null, false);
		assertTrue(it.hasNext());
		link = it.next();
		assertEquals("fred",link.getDummy1().getName());
		
		it = linker.getLinkIterator(d1, d2, linker.getLinkedFilter(), false);
		assertTrue(it.hasNext());
		link = it.next();
		assertEquals("fred",link.getDummy1().getName());
		
		it = linker.getLinkIterator(d1, d3, null, false);
		assertFalse(it.hasNext());
		
	}
	
}