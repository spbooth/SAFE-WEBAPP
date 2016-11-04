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

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

import static org.junit.Assert.*;

import org.junit.Test;
/**
 * @author spb
 *
 */
public class SessionServiceTest extends WebappTestBase {

	/**
	 * 
	 */
	public SessionServiceTest() {
		// TODO Auto-generated constructor stub
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
	public void testSet() throws DataFault{
		AppUserFactory login = ctx.getService(SessionService.class).getLoginFactory();
		AppUser fred = (AppUser) login.makeBDO();
		fred.setEmail("fred@example.com");
		SessionService serv = ctx.getService(SessionService.class);
		serv.setCurrentPerson(fred);
		
		
		assertFalse(serv.hasRole("Boris"));
		serv.setRole(fred, "Boris", true);
		assertTrue(serv.hasRole("Boris"));
		serv.setRole(fred, "Boris", false);
		assertFalse(serv.hasRole("Boris"));
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
	
	
}
