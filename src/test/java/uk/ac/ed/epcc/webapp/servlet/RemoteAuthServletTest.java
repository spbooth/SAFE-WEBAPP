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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

/**
 * @author spb
 * @param <A>
 *
 */
public class RemoteAuthServletTest<A extends AppUser> extends ServletTest {

	/**
	 * 
	 */
	public RemoteAuthServletTest() {
		
	}
	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		servlet=new RemoteAuthServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "RemoteServlet");
		servlet.init(config);
		req.servlet_path="RemoteServlet";
	}
	
	@Test
	public void testRegister() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		ctx.getService(SessionService.class).setCurrentPerson(user);
		
		req.remote_user="fred";
		doPost();
		checkMessage("remote_auth_set");
		checkDiff("/cleanup.xsl", "remote_set.xml");
	}
	@Test
	@ConfigFixtures("multi_remote.properties")
	public void testRegisterMulti() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		ctx.getService(SessionService.class).setCurrentPerson(user);
		
		req.remote_user="fred";
		doPost();
		checkMessage("remote_auth_set");
		checkDiff("/cleanup.xsl", "remote_set2.xml");
	}
	
	@Test
	public void testSignupRegister() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		//  Calling registerNewuser should allow a user to bind
		// an existing is and login
		AppUserFactory.registerNewUser(ctx, user);
		assertFalse(sess.haveCurrentUser());
		
		req.remote_user="fred";
		doPost();
		checkMessage("remote_auth_set");
		assertTrue(sess.isCurrentPerson(user));
		checkDiff("/cleanup.xsl", "remote_set.xml");
	}
	
	@Test
	@ConfigFixtures("multi_remote.properties")
	public void testSignupRegisterMulti() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		//  Calling registerNewuser should allow a user to bind
		// an existing is and login
		AppUserFactory.registerNewUser(ctx, user);
		assertFalse(sess.haveCurrentUser());
		
		req.remote_user="fred";
		doPost();
		checkMessage("remote_auth_set");
		assertTrue(sess.isCurrentPerson(user));
		checkDiff("/cleanup.xsl", "remote_set2.xml");
	}
	@Test
	@DataBaseFixtures("remote_set.xml")
	public void testReRegister() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred2@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		ctx.getService(SessionService.class).setCurrentPerson(user);
		
		req.remote_user="fred";
		doPost();
		checkMessage("remote_auth_set");
		checkDiff("/cleanup.xsl", "remote_reset.xml");
		
		A old_user  = fac.findByEmail("fred@example.com");
		assertNull(old_user.getRealmName(WebNameFinder.WEB_NAME));
	}
	
	@Test
	@DataBaseFixtures("remote_set2.xml")
	@ConfigFixtures("multi_remote.properties")
	public void testReRegisterMulti() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred2@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		ctx.getService(SessionService.class).setCurrentPerson(user);
		
		req.remote_user="fred";
		doPost();
		checkMessage("remote_auth_set");
		checkDiff("/cleanup.xsl", "remote_reset2.xml");
		
		A old_user  = fac.findByEmail("fred@example.com");
		assertNull(old_user.getRealmName(WebNameFinder.WEB_NAME));
	}
	
	@Test
	@DataBaseFixtures("remote_set.xml")
	public void testLogin() throws ConsistencyError, Exception{
		
		req.remote_user="fred";
		doPost();
		checkRedirect("/main.jsp");
		assertEquals("fred@example.com",ctx.getService(SessionService.class).getCurrentPerson().getEmail());
	}
	
	@Test
	@DataBaseFixtures("remote_set2.xml")
	@ConfigFixtures("multi_remote.properties")
	public void testLoginMulti() throws ConsistencyError, Exception{
		
		req.remote_user="fred";
		doPost();
		checkRedirect("/main.jsp");
		assertEquals("fred@example.com",ctx.getService(SessionService.class).getCurrentPerson().getEmail());
	}
}
