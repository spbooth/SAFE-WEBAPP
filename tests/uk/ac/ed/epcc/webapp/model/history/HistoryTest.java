/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.history.HistoryFactory.HistoryRecord;

public class HistoryTest extends WebappTestBase {

	 
	
	
	
	
	@Test
	public void testIT() throws IllegalArgumentException, ConsistencyError, DataException{
		DummyHistoryFactory fac = new DummyHistoryFactory(ctx);
		Dummy1.Factory test_fac = new Dummy1.Factory(ctx);
		Dummy1 t = new Dummy1(ctx);
		
		t.setName("fred");
		t.setNumber(14);
		t.commit();
		
		HistoryRecord first = fac.update(t);
		
		Date first_point = new Date();
		
		HistoryRecord ret = fac.find(t,first_point);
		assertTrue(first.equals(ret));
		assertTrue(ret.equals(first));
		assertEquals(t.getID(),first.getPeerID());
		assertEquals(first.getID(),ret.getID());
		
		assertEquals(1, fac.getCount(null));
		// now check no mod does not change History
		t.setNumber(14);
		t.commit();
		ret = fac.update(t);
		assertTrue(first.equals(ret));
		assertTrue(ret.equals(first));
		assertEquals(t.getID(),first.getPeerID());
		assertEquals(first.getID(),ret.getID());
		
		assertEquals(1, fac.getCount(null));
		Date second_point = new Date();
		ret = fac.find(t,second_point);
		assertTrue(first.equals(ret));
		assertTrue(ret.equals(first));
		assertEquals(t.getID(),first.getPeerID());
		assertEquals(first.getID(),ret.getID());
		
		
		// check the same is true even if object ahs been modified and back again
		t.setNumber(12);
		t.commit();
		t.setNumber(14);
		t.commit();
		ret = fac.update(t);
		assertTrue(first.equals(ret));
		assertTrue(ret.equals(first));
		assertEquals(t.getID(),first.getPeerID());
		assertEquals(first.getID(),ret.getID());
		assertEquals(1, fac.getCount(null));
		
		
		Date third_point = new Date();
		ret = fac.find(t,third_point);
		assertTrue(first.equals(ret));
		assertTrue(ret.equals(first));
		assertEquals(t.getID(),first.getPeerID());
		assertEquals(first.getID(),ret.getID());
		// now really change and update
		
		t.setNumber(12);
		t.commit();
		ret = fac.update(t);
		assertEquals(2, fac.getCount(null));
		assertFalse(first.equals(ret));
		Dummy1 peer = (Dummy1) ret.getAsPeer();
		assertEquals(t.getNumber(),peer.getNumber());
		fac.purge(t);
		t.delete();
	}
}
