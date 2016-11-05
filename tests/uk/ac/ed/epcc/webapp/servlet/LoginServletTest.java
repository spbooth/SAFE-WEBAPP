//| Copyright - The University of Edinburgh 2014                            |
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.RequiredPage;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class LoginServletTest<A extends AppUser> extends ServletTest {

	/**
	 * 
	 */
	public LoginServletTest() {
		// TODO Auto-generated constructor stub
	}
	
	@ConfigFixtures("password_auth.properties")
	@Test
	public void testLogin() throws DataFault, ServletException, IOException{
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		
		
		addParam("username", "fred@example.com");
		addParam("password", "FredIsDead");
		doPost();
		checkRedirect("/main.jsp");
		SessionService<A> sess = ctx.getService(SessionService.class);
		assertTrue(sess.haveCurrentUser());
		
		Set<RequiredPage<A>> pages = fac.getRequiredPages();
		assertEquals(1,pages.size());
		RequiredPage page = pages.iterator().next();
		assertFalse(page.required(sess));
		
	}
	
	@ConfigFixtures({"password_auth.properties","wtmp.properties"})
	@Test
	public void testLoginWithWtmp() throws DataException, Exception{
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		takeBaseline();
		
		req.header.put("user-agent", "junit");
		addParam("username", "fred@example.com");
		addParam("password", "FredIsDead");
		doPost();
		checkRedirect("/main.jsp");
		SessionService<A> sess = ctx.getService(SessionService.class);
		assertTrue(sess.haveCurrentUser());
		
		Set<RequiredPage<A>> pages = fac.getRequiredPages();
		assertEquals(1,pages.size());
		RequiredPage page = pages.iterator().next();
		assertFalse(page.required(sess));
		checkDiff("/cleanup.xsl", "login_wtmp.xml");
	}
	@ConfigFixtures("password_auth.properties")
	@Test
	public void testFirstPassword() throws Exception{
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String first = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		//sess.setCurrentPerson(user);
		addParam("username","fred@example.com");
		addParam("password",first);
		doPost();
		checkRedirect("/welcome.jsp");
		assertTrue(sess.haveCurrentUser());
		
		Set<RequiredPage<A>> pages = fac.getRequiredPages();
		assertEquals(1,pages.size());
		RequiredPage page = pages.iterator().next();
		assertTrue(page.required(sess));
		doFormResult(page.getPage());
		checkRedirect("/password_update.jsp");
	}
	
	
	@ConfigFixtures("password_auth.properties")
	@Test
	public void testBadLogin() throws DataFault, ServletException, IOException{
		AppUserFactory<A> fac =  ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		
		addParam("username", "bill@example.com");
		addParam("password", "FredIsDead");
		doPost();
		checkRedirect("/login.jsp?error=login");
	
		
	}
	@ConfigFixtures("password_auth.properties")
	@Test
	public void testBadPassword() throws DataFault, ServletException, IOException{
		AppUserFactory<A> fac =  ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		
		
		addParam("username", "fred@example.com");
		addParam("password", "FredIsAlive");
		doPost();
		checkRedirect("/login.jsp?error=login");
		
		
		
	}
	
	@Test
	public void testRequestNewPassword() throws DataException, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		addParam("username","fred@example.com");
		addParam("email_password","true");
		doPost();
		checkMessage("new_password_emailed");
		assertEquals(1,MockTansport.nSent());
		assertEquals(ctx.expandText("${service.name} Account Password Request"),MockTansport.getMessage(0).getSubject());
		checkDiff("/cleanup.xsl", "new_password.xml");
		
		user=fac.find(user.getID());
		assertTrue(composite.mustResetPassword(user));
	}

	@Test
	@ConfigFixtures("password_server.properties")
	public void testRequestNewPasswordFromServlet() throws DataException, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		addParam("username","fred@example.com");
		addParam("email_password","true");
		doPost();
		checkMessage("new_password_emailed");
		assertEquals(1,MockTansport.nSent());
		assertEquals(ctx.expandText("${service.name} Account Password Request"),MockTansport.getMessage(0).getSubject());
		checkDiff("/cleanup.xsl", "new_password_from_server.xml");
		
		user=fac.find(user.getID());
		assertTrue(composite.mustResetPassword(user)); 
	}

	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		servlet=new LoginServlet<A>();
		MockServletConfig config = new MockServletConfig(serv_ctx, "LoginServlet");
		servlet.init(config);
		req.servlet_path="LoginServlet";
	}

	

}