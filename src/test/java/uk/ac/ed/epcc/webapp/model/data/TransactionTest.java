//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.Dummy1;

/**
 * @author Stephen Booth
 *
 */
@ConfigFixtures("nomemory.properties")
public class TransactionTest extends WebappTestBase {


	@Test
	public void testCreate() throws Exception {
		takeBaseline();
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		
		Dummy1 dummy = fac.makeBDO();
		dummy.setName("Boris");
		dummy.commit();
		checkDiff("/cleanup.xsl", "create_dummy.xml");
		
		
	}
	
	@Test
	public void testCreateInTransaction() throws Exception {
		takeBaseline();
		DatabaseService db = ctx.getService(DatabaseService.class);
		
		db.startTransaction();
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		
		Dummy1 dummy = fac.makeBDO();
		dummy.setName("Boris");
		dummy.commit();
		
		db.stopTransaction();
		checkDiff("/cleanup.xsl", "create_dummy.xml");
		
		
	}
	
	@Test
	public void testAbortTransaction() throws Exception {
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		assertTrue(fac.isValid());
		takeBaseline();
		DatabaseService db = ctx.getService(DatabaseService.class);
		
		db.startTransaction();
		
		
		Dummy1 dummy = fac.makeBDO();
		dummy.setName("Boris");
		dummy.commit();
		db.rollbackTransaction();
		db.stopTransaction();
		checkUnchanged();
		
		
	}
	
	@Test
	public void testAbortPartialTransaction() throws Exception {
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		assertTrue(fac.isValid());
		takeBaseline();
		DatabaseService db = ctx.getService(DatabaseService.class);
		
		db.startTransaction();
		Dummy1 fred = fac.makeBDO();
		fred.setName("Fred");
		fred.commit();
		db.rollbackTransaction();
		Dummy1 dummy = fac.makeBDO();
		dummy.setName("Boris");
		dummy.commit();
		
		db.stopTransaction();
		// same as above but different id
		checkDiff("/cleanup.xsl", "create_dummy2.xml");
		
		
	}
}
