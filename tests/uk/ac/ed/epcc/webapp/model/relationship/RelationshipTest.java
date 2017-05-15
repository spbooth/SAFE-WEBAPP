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

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
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
	/**
	 * 
	 */
	private static final String MANAGER = "Manager";
	private static final String DOUBLE_MANAGER = "DoubleManager";
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
		t.setPerson(fred); // fred should have self role wrt Test1
		t.commit();
		
		Dummy3 t2 = new Dummy3(ctx);
		t2.setName("Test2");
		t2.setPerson(bill); // bill should have self role wrt Test2
		t2.commit();
		
		
		
		Relationship<AppUser, Dummy3> rel = new Relationship<AppUser, Dummy3>(ctx, "TestRelationship");
		rel.setRole(fred, t, "shark", true);		
		
	}
	@Test
	public void testGlobalMissing() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser bill = login.findByEmail("bill@example.com");
		Assert.assertNotNull(bill);
		
		SessionService serv = service;
		serv.setCurrentPerson(bill);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		
		// bill is not manager
		Assert.assertFalse(fac.matches(service.getRelationshipRoleFilter(fac, MANAGER), t));
		assertFalse(service.hasRelationship(fac, t,MANAGER));
		
	
	}
	@Test
	public void testGlobalSet() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser bill = login.findByEmail("bill@example.com");
		Assert.assertNotNull(bill);
		
		SessionService serv = service;
		serv.setCurrentPerson(bill);
		serv.setTempRole(MANAGER); // Note role state is cached as part of the filter so set this before querying roles
		serv.setToggle(MANAGER, true);
		assertTrue(serv.getToggleRoles().contains(MANAGER));
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		
		// global manager role should be sufficient
		assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, MANAGER), t));
		assertTrue(service.hasRelationship(fac, t, MANAGER));
		serv.setToggle(MANAGER, false);
		assertFalse(fac.matches(service.getRelationshipRoleFilter(fac, MANAGER), t));
		assertFalse(service.hasRelationship(fac, t, MANAGER));
	}
	@Test
	public void testRelation() throws DataException, UnknownRelationshipException{
		SessionService<AppUser> service = ctx.getService(SessionService.class);
		AppUserFactory login = service.getLoginFactory();
		
		AppUser fred = login.findByEmail("fred@example.com");
		Assert.assertNotNull(fred);
		
		SessionService<AppUser> serv = service;
		serv.setCurrentPerson(fred);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		Relationship<AppUser, Dummy3> rel = ctx.makeObject(Relationship.class, "TestRelationship");
		// the shark relationship should make fred a manager
		Assert.assertTrue(rel.hasRole(fred, t, "shark"));
		
		Assert.assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, MANAGER), t));
		assertFalse(fac.matches(service.getRelationshipRoleFilter(fac, DOUBLE_MANAGER), t));
		
		
		// caching call
		assertTrue(service.hasRelationship(fac, t, MANAGER));
		assertFalse(service.hasRelationship(fac, t, DOUBLE_MANAGER));
		
		//people with role should be fred 
		AppUser manager = (AppUser) login.find(service.getPersonInRelationshipRoleFilter(fac, MANAGER, t),true);
		assertNotNull(manager);
		assertTrue(fred.equals(manager));
		assertNull(login.find(service.getPersonInRelationshipRoleFilter(fac, DOUBLE_MANAGER, t),true));
		
		// fred 
		Dummy3.Factory fac = new Dummy3.Factory(ctx);
		Dummy3 client = fac.find((BaseFilter<Dummy3>) service.getTargetInRelationshipRoleFilter(fac, MANAGER, fred),true);
		assertNull(fac.find((BaseFilter<Dummy3>) service.getTargetInRelationshipRoleFilter(fac, DOUBLE_MANAGER, fred),true));
		assertNotNull(client);
		assertTrue(client.equals(t));
		service.setTempRole(MANAGER);
		service.setToggle(MANAGER, true);
		assertTrue(service.hasRelationship(fac, t, DOUBLE_MANAGER));
		assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, DOUBLE_MANAGER), t));

		
		// does not affect filters
		assertNull(login.find(service.getPersonInRelationshipRoleFilter(fac, DOUBLE_MANAGER, t),true));
		assertNull(fac.find((BaseFilter<Dummy3>) service.getTargetInRelationshipRoleFilter(fac, DOUBLE_MANAGER, fred),true));
		
		
	}
	
	
	@Test
	public void testDirect() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> login = service.getLoginFactory();
		
		AppUser bill = login.findByEmail("bill@example.com");
		Assert.assertNotNull(bill);
		
		SessionService serv = service;
		serv.setCurrentPerson(bill);
		
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		
		Assert.assertNotNull(t);
		
		Dummy3 t2 = fac.find(fac.new StringFilter("Test2"));
		
		Assert.assertNotNull(t2);
		
		// bill should not have self role wrt Test1
		Assert.assertFalse(fac.matches(service.getRelationshipRoleFilter(fac, "self"), t));
		assertFalse(service.hasRelationship(fac, t, "self"));
		
		
		// bill should have self role wrt Test2
		Assert.assertTrue(fac.matches(service.getRelationshipRoleFilter(fac, "self"), t2));
		assertTrue(service.hasRelationship(fac, t2, "self"));
		
		AppUser owner = login.find(service.getPersonInRelationshipRoleFilter(fac, "self", t2),true);
	    assertTrue(bill.equals(owner));
	    
	    Dummy3 target = fac.find(service.getTargetInRelationshipRoleFilter(fac, "self", bill));
	    assertTrue(t2.equals(target));
	    
	    //Test globals
	    assertTrue(service.hasRelationship(fac, t, "YES"));
	    assertFalse(service.hasRelationship(fac, t, "NO"));
	    
	    // Named filters
	    assertTrue(service.hasRelationship(fac, t, "CalledTest1"));
	    assertFalse(service.hasRelationship(fac, t2, "CalledTest1"));
	}
	@Test
	public void testNoUser() throws DataException, UnknownRelationshipException{
		SessionService service = ctx.getService(SessionService.class);
		Dummy3 t = fac.find(fac.new StringFilter("Test1"));
		Dummy3 t2 = fac.find(fac.new StringFilter("Test2"));
		assertFalse(service.haveCurrentUser());
		//Test globals
	    assertTrue(service.hasRelationship(fac, t, "YES"));
	    assertFalse(service.hasRelationship(fac, t, "NO"));
	    assertFalse(service.hasRelationship(fac, t, MANAGER));
	    assertFalse(service.hasRelationship(fac, t, DOUBLE_MANAGER));
	 // Named filters
	    assertTrue(service.hasRelationship(fac, t, "CalledTest1"));
	    assertFalse(service.hasRelationship(fac, t2, "CalledTest1"));
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
			
			boolean matches = service.hasRelationship( fac,t, "bogus");
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
