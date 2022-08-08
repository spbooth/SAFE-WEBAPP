//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.model.cron;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author Stephen Booth
 *
 */
public class LocksTest extends WebappTestBase {
	
	@Test
	public void testLock() throws DataFault, ParseException {
		Date d = setTime(2020, Calendar.APRIL, 4, 9, 00);
		LockFactory fac = LockFactory.getFactory(ctx);
		
		Lock a = fac.makeFromString("first-lock");
		
		assertFalse(a.isLocked());
		assertFalse(a.isHolding());
		
		assertTrue(a.takeLock());
		assertTrue(a.isLocked());
		assertTrue(a.isHolding());
		
		assertEquals(d, a.lastLocked());
		assertEquals(d, a.wasLockedAt());
		
		Lock b = fac.makeFromString("first-lock");
		assertTrue(b.isLocked());
		assertFalse(b.isHolding());
		assertEquals(d, b.lastLocked());
		assertEquals(d, b.wasLockedAt());
		
		a.releaseLock();
		assertFalse(a.isHolding());
		assertFalse(a.isLocked());
		assertNull(a.wasLockedAt());
		assertEquals(d, a.lastLocked());

		b = fac.makeFromString("first-lock");
		assertFalse(b.isHolding());
		assertFalse(b.isLocked());
		assertNull(b.wasLockedAt());
		assertEquals(d, b.lastLocked());
		
	}

}
