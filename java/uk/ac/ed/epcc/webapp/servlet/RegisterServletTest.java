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
package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.EmailNameFinder;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class RegisterServletTest extends AbstractRegisterServletTest {

	/**
	 * 
	 */
	public RegisterServletTest() {
		
	}
	@Test
	public void testRegister() throws Exception{
		MockTansport.clear();
		takeBaseline();
		addParam(EmailNameFinder.EMAIL, "thing@example.com");
		doPost();
		checkMessage("signup_ok_password");
		checkDiff("/cleanup.xsl","signup.xml");
		assertEquals(1,MockTansport.nSent());
	}

	
	@ConfigFixtures("/extauth.properties")
	@Test
	public void testExtAuthRegister() throws ConsistencyError, Exception{
		req.remote_user="fred";
		MockTansport.clear();
		takeBaseline();
		addParam(EmailNameFinder.EMAIL, "thing@example.com");
		doPost();
		checkMessage("signup_ok");
		checkDiff("/cleanup.xsl","signup_extauth.xml");
		assertEquals(0,MockTansport.nSent());
	}
	
	
	
	@ConfigFixtures("/extauth.properties")
	@Test
	public void testExtAuthRegisterNoWebname() throws ConsistencyError, Exception{
		req.remote_user=null;
		MockTansport.clear();
		takeBaseline();
		addParam(EmailNameFinder.EMAIL, "thing@example.com");
		doPost();
		checkMessage("access_denied");
		checkUnchanged();
		assertEquals(0,MockTansport.nSent());
	}
	
	
	@ConfigFixtures("/auto_create.properties")
	@DataBaseFixtures("noregister.xml")
	@Test
	public void testAutoCreateRegister() throws ConsistencyError, Exception{
		req.remote_user="fred";
		MockTansport.clear();
		
		AppUserFactory login = getContext().getService(SessionService.class).getLoginFactory();
		AppUser fred = login.findFromString("fred");
		assertNotNull(fred);
		assertNull(fred.getEmail());
		assertTrue(login.mustRegister(fred));
		takeBaseline();
		addParam(EmailNameFinder.EMAIL, "thing@example.com");
		doPost();
		checkMessage("signup_ok");
		fred = login.findFromString("fred");
		assertNotNull(fred);
		assertEquals("thing@example.com",fred.getEmail());
		checkDiff("/cleanup.xsl","signup_autocreate.xml");
		assertEquals(0,MockTansport.nSent());
		assertFalse(login.mustRegister(fred));
	}
}
