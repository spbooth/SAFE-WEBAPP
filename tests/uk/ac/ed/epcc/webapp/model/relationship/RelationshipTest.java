//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.relationship;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy3;
import uk.ac.ed.epcc.webapp.model.Dummy3;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

/** Tests of relationships
 * @author spb
 *
 */
public class RelationshipTest extends WebappTestBase {
	Dummy3.Factory fac;
	
	@Before
	public void setup() throws DataFault{
		fac = new Dummy3.Factory(getContext());
		
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		fred.commit();
		AppUser bill = (AppUser) login.makeBDO();
		bill.setEmail("bill@example.com");
		bill.commit();
		
		Dummy3 t = new Dummy3(ctx);
		t.setName("Test1");
		t.setPerson(fred);
		t.commit();
		
		Dummy3 t2 = new Dummy3(ctx);
		t2.setName("Test2");
		t2.setPerson(bill);
		t2.commit();
		
		
		
		Relationship<AppUser, Dummy3> rel = new Relationship<AppUser, Dummy3>(ctx, "TestRelationship");
		rel.setRole(fred, t, "shark", true);		
		
	}
	@Test
	public void testGlobal() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser bill = login.findByEmail("bill@example.com");
		Assert.assertNotNull(bill);
		
		SessionService serv = service;
		serv.setCurrentPerson(bill);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		
		
		Assert.assertFalse(fac.matches(service.getRelationshipRoleFilter(fac, "Manager"), t));
		
		serv.setTempRole("Manager");
		
		Assert.assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, "Manager"), t));
		
		
	
	}
	
	@Test
	public void testRelation() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser fred = login.findByEmail("fred@example.com");
		Assert.assertNotNull(fred);
		
		SessionService serv = service;
		serv.setCurrentPerson(fred);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		Relationship<AppUser, Dummy3> rel = new Relationship<AppUser, Dummy3>(ctx, "TestRelationship");
		Assert.assertTrue(rel.hasRole(fred, t, "shark"));
		
		Assert.assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, "Manager"), t));
		
		serv.setTempRole("Manager");
		
		Assert.assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, "Manager"), t));
		
	}
	
	
	@Test
	public void testDirect() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser bill = login.findByEmail("bill@example.com");
		Assert.assertNotNull(bill);
		
		SessionService serv = service;
		serv.setCurrentPerson(bill);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		
		Dummy3 t2 = fac.find(fac.new StringFilter("Test2"));
		
		Assert.assertNotNull(t2);
		
		Assert.assertFalse(fac.matches(service.getRelationshipRoleFilter(fac, "self"), t));
		
		
		Assert.assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, "self"), t2));
		
		
	
	}
	
	@Test
	public void testBogus() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser bill = login.findByEmail("bill@example.com");
		Assert.assertNotNull(bill);
		
		SessionService serv = service;
		serv.setCurrentPerson(bill);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		
		Dummy3 t2 = fac.find(fac.new StringFilter("Test2"));
		
		Assert.assertNotNull(t2);
		boolean thrown=false;
		try{
		
		boolean matches = fac.matches(service.getRelationshipRoleFilter(fac, "bogus"), t);
		Assert.assertFalse(matches);
		}catch(UnknownRelationshipException e){
			System.out.println("bad role "+e.getMessage());
			// expected
			thrown=true;
		}
		Assert.assertTrue("Expecting exception",thrown);
		thrown = false;
		try{
		boolean matches2 = fac.matches(service.getRelationshipRoleFilter(fac, "really.bogus"), t2);
		Assert.assertFalse(matches2);
		}catch(UnknownRelationshipException e2){
			System.out.println("bad role "+e2.getMessage());
			thrown=true;
		}
		Assert.assertTrue("Expecting exception",thrown);
	
	}
}
