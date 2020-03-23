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
package uk.ac.ed.epcc.webapp.model.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
		assertNotNull(ret);
		assertTrue(first.equals(ret));
		assertTrue(ret.equals(first));
		assertEquals(t.getID(),first.getPeerID());
		assertEquals(first.getID(),ret.getID());
		
		assertEquals(1, fac.getCount(null));
		// now check no mod does not change History
		t.setNumber(14);
		boolean changed = t.commit();
		assertFalse("commit changed", changed);
		ret = fac.update(t);
		assertTrue("Same history",first.equals(ret));
		assertTrue("same history again",ret.equals(first));
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