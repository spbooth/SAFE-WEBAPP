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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.BinaryExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.ConstExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.Operator;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.Dummy1.Beatle;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase;
import uk.ac.ed.epcc.webapp.model.data.FieldSQLExpression;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

public class DummyFactoryTest extends DataObjectFactoryTestCase {

	
	@Override
	public DataObjectFactory getFactory() {
		return new Dummy1.Factory(ctx);
	}

	@Before
	public void setUp()  {
	    try{
	    	((Dummy1.Factory) getFactory()).nuke();
	    }catch(Exception e){
	    	log.error( "Error nuking table",e);
	    }
	}
	
	@After
	public void tearDown() throws Exception {
		((Dummy1.Factory) getFactory()).nuke();
	}
	
	@Test
	public void testMultiFind() throws DataException{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
	
		
		try{
			SQLAndFilter<Dummy1> f = new SQLAndFilter<>(fac.getTarget());
			
			System.out.println("hello\n");
			fac.find(f);
			assertFalse("Exception not throws", true);
		}catch(MultipleResultException e){
			
		}
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		// Dummy accept filter to force non sQLFilter
		fil.addFilter(new AbstractAcceptFilter<Dummy1>(fac.getTarget()) {
			public boolean accept(Dummy1 o) {
				return true;
			}
		});
		
		try{
			fac.find(fil);
			assertFalse("Exception not throws", true);
		}catch(MultipleResultException e){
			
		}
	}
	
	
	@Test
	public void testOrder() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
		
		int expect=0;
		for(Dummy1 d : fac.all()){
			assertEquals(expect, d.getNumber());
			assertEquals(16-expect, (int) d.getUnsigned());
			assertEquals(d.getName(), "Test"+(16-expect));
			expect++;
		}
		assertEquals(expect, 17);
	}
	
	@Test
	public void testDefaultOrderWithFilter() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
		
		int expect=0;

		for(Dummy1 d : fac.getWithFilter()){
			assertEquals(expect, d.getNumber());
			assertEquals(16-expect, (int) d.getUnsigned());
			assertEquals(d.getName(), "Test"+(16-expect));
			expect++;
		}
		assertEquals(expect, 17);
	}
	
	@Test
	public void testReverseOrder() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(int i=0; i< 17; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Test"+i);
			d.setUnsigned(i);
			d.setNumber(16-i);
			d.commit();
		}
		
		int expect=16;
		for(Dummy1 d : fac.getReverse()){
			assertEquals(expect, d.getNumber());
			assertEquals(16-expect, (int) d.getUnsigned());
			assertEquals(d.getName(), "Test"+(16-expect));
			expect--;
		}
		assertEquals(expect, -1);
	}

	@Test
	public void getGetNullable(){
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		Set<String> nullable = fac.getNullFields();
		
		assertEquals(4, nullable.size());
		
		assertTrue(fac.fieldExists(Dummy1.MANDATORY));
		assertFalse(nullable.contains(Dummy1.MANDATORY));
	}
	
	
	@Test
	public void testFalse() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		Dummy1 d = new Dummy1(ctx);
		d.setName("Fred");
		d.commit();
		Iterator it = fac.getResult(new FalseFilter<>(Dummy1.class)).iterator();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testFields() throws DataException{
		
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		
		
		Dummy1 d = new Dummy1(ctx);
		d.setName("Fred");
		d.setBeatle(Beatle.George);
		d.setRuttle(Beatle.Paul);
		d.setNumber(12);
		
		d.commit();
		
		SQLFilter<Dummy1> fil = fac.getFilter(d);
		
		Dummy1 peer = fac.find(fil, true);
		assertTrue(d.equals(d));
		assertTrue(peer.equals(d));
		assertTrue(d.equals(peer));
		assertFalse(d.equals(null));
		assertEquals("Fred", peer.getName());
		assertEquals(Beatle.George,peer.getBeatle());
		assertEquals(Beatle.Paul,peer.getRuttle());
		assertEquals(12, d.getNumber());
		
		peer.delete();
		
		assertNull(fac.find(fil,true));
	}
	
	@Test
	public void testEquals() throws DataException{
	Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		
		
		Dummy1 d = new Dummy1(ctx);
		d.setBeatle(Beatle.Ringo);
		d.commit();

		Dummy1 d2 = new Dummy1(ctx);
		d2.setBeatle(Beatle.Ringo);
		assertTrue(d.equals(d));
		assertFalse(d.equals(null));
		assertFalse(d.equals(d2));
		assertFalse(d2.equals(d));
		
		d2.commit();
		assertFalse(d.equals(d2));
		assertFalse(d2.equals(d));
		
		Dummy1 d3 = fac.find(d.getID());
		assertTrue(d.equals(d3));
		assertEquals(d.hashCode(), d3.hashCode());
		
		Dummy2.Factory fac2 = new Dummy2.Factory(ctx);
		Dummy2 dd = fac2.makeBDO();
		dd.setName("Hank");
		dd.commit();
		
		assertFalse(d.equals(dd));
		assertEquals(d.getID(),dd.getID());
		
		Dummy1.Factory fac_alt = new Dummy1.Factory(ctx,"OtherTable");
		Dummy1 alt = fac_alt.makeBDO();
		alt.setBeatle(Beatle.Ringo);
		alt.commit();
		assertFalse(d.equals(alt));
	}
	
	@Test
	public void testReference() throws DataFault{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		
		
		Dummy1 d = new Dummy1(ctx);
		d.setBeatle(Beatle.Ringo);
		d.commit();
		IndexedReference<Dummy1> ref1 = fac.makeReference(d);
		assertTrue(fac.isMine(d));
		assertTrue(fac.isMyReference(ref1));
		assertFalse(fac.isMyReference(null));
		
		
		Dummy2.Factory fac2 = new Dummy2.Factory(ctx);
		Dummy2 dd = fac2.makeBDO();
		dd.setName("Hank");
		dd.commit();
		IndexedReference<Dummy2> ref2 = fac2.makeReference(dd);
		
		assertFalse(fac.isMine(dd));
		assertFalse(fac.isMyReference(ref2));
		
		Dummy1.Factory fac_alt = new Dummy1.Factory(ctx,"OtherTable");
		Dummy1 alt = fac_alt.makeBDO();
		alt.setBeatle(Beatle.Ringo);
		alt.commit();
		
		assertFalse(fac.isMine(alt));
		assertTrue(fac_alt.isMine(alt));
		IndexedReference alt_ref = fac_alt.makeReference(alt);
		assertFalse(fac.isMyReference(alt_ref));
		assertTrue(fac_alt.isMyReference(alt_ref));
		
		
	}
	
	@Test
	public void testRemove() throws DataException{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		
		for(int i=0 ; i< 27 ; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Name"+i);
			d.setNumber(i);
			d.commit();
		}
		
		assertEquals(27,fac.count(null));
		
		int count=0;
		for(Iterator<Dummy1> it = fac.getResult(null).iterator(); it.hasNext();){
			Dummy1 d = it.next();
			it.remove();
			count++;
			assertEquals("number remaining" ,27-count,fac.count(null));
		}
		assertEquals(27, count);
		assertEquals(0,fac.count(null));
		assertFalse(fac.exists(null));
	}
	
	@Test
	public void testRemoveWithBackup() throws Exception{
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		
		takeBaseline();
		
		for(int i=0 ; i< 27 ; i++){
			Dummy1 d = new Dummy1(ctx);
			d.setName("Name"+i);
			d.setNumber(i);
			d.commit();
		}
		
		assertEquals(27,fac.count(null));
		
		int count=0;
		for(Iterator<Dummy1> it = fac.getResult(null).iterator(); it.hasNext();){
			Dummy1 d = it.next();
			it.remove();
			count++;
			if( count > 2 ){
				// start backing up after a few deletes
				getContext().setAttribute(Repository.BACKUP_SUFFIX_ATTR, "TestBackup");
			}
			assertEquals("number remaining" ,27-count,fac.count(null));
		}
		assertEquals(27, count);
		assertEquals(0,fac.count(null));
		assertFalse(fac.exists(null));
		
		checkDiff("/cleanup.xsl", "backup.xml");
	}
	@Test
	public void testTypeProducerFilter() throws DataException {
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		for(Beatle b : Dummy1.Beatle.values()) {
			Dummy1 d = fac.makeBDO();
			d.setBeatle(b);
			d.commit();
		}
		
		assertEquals((long) Dummy1.Beatle.values().length, fac.getCount(null));
		
		Dummy1 ringo = fac.find(fac.getBeatleFilter(Beatle.Ringo));
		assertEquals(Beatle.Ringo, ringo.getBeatle());
	}
	@Test
	public void testUpdate() throws DataException {
		Dummy1.Factory fac = (Dummy1.Factory) getFactory();
		int i=0;
		for(Beatle b : Dummy1.Beatle.values()) {
			if( b != Beatle.John) {
				Dummy1 d = fac.makeBDO();
				d.setBeatle(b);
				d.setNumber(i++);
				d.setName(b.toString());
				d.commit();
			}
		}
		FilterUpdate update = fac.getUpdate();
		
		update.updateExpression((FieldSQLExpression) fac.getNumberExpression(), BinaryExpression.create(ctx,fac.getNumberExpression(),Operator.MUL, new ConstExpression(Number.class, 7)), null);
		update.update(fac.getBeatleFieldValue(), Beatle.John, fac.getBeatleFilter(Beatle.Ringo));
		
		Dummy1 d = fac.find(fac.getBeatleFilter(Beatle.John));
		
		assertEquals(7,d.getNumber());
		
		
	}
	
}