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
package uk.ac.ed.epcc.webapp.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/**
 * @author spb
 *
 */
public class SessionServiceTest extends WebappTestBase {

	/**
	 * 
	 */
	public SessionServiceTest() {
		
	}
	
	@Test
	public void testTempNoUser(){
		SessionService serv = ctx.getService(SessionService.class);
		
		assertFalse(serv.hasRole("Boris"));
		serv.setTempRole("Boris");
		assertTrue(serv.hasRole("Boris"));
		
	}

	@Test 
	public void testTempWithUser() throws DataFault{
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		SessionService serv = ctx.getService(SessionService.class);
		serv.setCurrentPerson(fred);
		
		
		assertFalse(serv.hasRole("Boris"));
		serv.setTempRole("Boris");
		assertTrue(serv.hasRole("Boris"));
	}
	
	@Test 
	public void testSet() throws DataException{
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		SessionService serv = ctx.getService(SessionService.class);
		serv.setCurrentPerson(fred);
		
		
		assertFalse(serv.hasRole("Boris"));
		serv.setRole(fred, "Boris", true);
		assertTrue(serv.hasRole("Boris"));
		
		NamedFilterWrapper wrapper = new NamedFilterWrapper<>(login);
		BaseFilter fil = wrapper.getNamedFilter("RoleFilterProvider.Boris");
		assertNotNull(fil);
		assertTrue(login.exists(fil));
		serv.setRole(fred, "Boris", false);
		assertFalse(serv.hasRole("Boris"));
		assertFalse(login.exists(fil));
	}
	@Test 
	public void testToggle() throws DataFault{
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		SessionService serv = ctx.getService(SessionService.class);
		serv.setCurrentPerson(fred);
		
		
		assertFalse(serv.hasRole("Manager"));
		serv.setRole(fred, "Manager", true);
		assertFalse(serv.hasRole("Manager"));
		serv.setToggle("Manager", true);
		assertTrue(serv.hasRole("Manager"));
		serv.setToggle("Manager", false);
		assertFalse(serv.hasRole("Manager"));
		serv.setToggle("Manager", true);
		serv.setRole(fred, "Boris", false);
		assertFalse(serv.hasRole("Boris"));
		
		
		assertNull(serv.getToggle("Wombat"));
	}
	
	@Test 
	public void testApplyToggle() throws DataFault{
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		SessionService serv = ctx.getService(SessionService.class);
		serv.setCurrentPerson(fred);
		serv.setApplyToggle(false);
		
		assertFalse(serv.hasRole("Manager"));
		serv.setRole(fred, "Manager", true);
		assertTrue(serv.hasRole("Manager"));
		serv.setToggle("Manager", true);
		assertTrue(serv.hasRole("Manager"));
		serv.setToggle("Manager", false);
		assertTrue(serv.hasRole("Manager"));
		serv.setToggle("Manager", true);
		serv.setRole(fred, "Boris", false);
		assertFalse(serv.hasRole("Boris"));
		
		assertNull(serv.getToggle("Manager"));
		assertNull(serv.getToggle("Wombat"));
	}
	@Test
	@ConfigFixtures("toggle.properties")
	public void testMultiValueRole() throws DataFault{
		
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		fred.commit();
		sess.setCurrentPerson(fred);
		
		// Role can be from either A or B
		// A has two equivalent roles AA,AB
		// All are toggle roles.
		//
		//
		
		// use_role.R=A,B
		// use_role.A=AA,AB
		// toggle_roles=A,B,AA,AB
		
		sess.setTempRole("B");
		sess.setToggle("B", false);
		sess.setTempRole("AA");
		sess.setToggle("AA", false);
		sess.setTempRole("AB");
		sess.setToggle("AB", false);
		
		comb(sess,false,false,false,false);
		comb(sess,false,false,false,true);
		comb(sess,false,false,true,false);
		comb(sess,false,false,true,true);
		
		comb(sess,false,true,false,false);
		comb(sess,true,true,false,true);
		comb(sess,true,true,true,false);
		comb(sess,true,true,true,true);
		
	}
	private void comb(SessionService s, boolean expected, boolean a, boolean aa, boolean ab){
		s.setToggle("A", a);
		s.setToggle("AB",ab);
		s.setToggle("AA", aa);
		assertEquals(expected, s.hasRole("R"));
	}
}
