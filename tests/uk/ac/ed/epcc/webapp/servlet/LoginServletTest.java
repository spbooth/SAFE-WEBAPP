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
import java.util.Calendar;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.RequiredPage;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.SignupDateComposite;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

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
	
	@ConfigFixtures("wtmp.properties")
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
	
	@ConfigFixtures({"wtmp.properties","crosscookie.properties"})
	@Test
	public void testLoginWithWtmpAndCrossCookie() throws DataException, Exception{
		takeBaseline();
		
		// Fix the random values
		ctx.setService(new MockRandomService());
		TestTimeService cts = new TestTimeService();
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2017, Calendar.JUNE, 23,9,00);
		cts.setResult(c.getTime());
		ctx.setService(cts);
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		
		
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
		checkDiff("/cleanup.xsl", "login_wtmp_cookie.xml");
	}
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
	public void testRequestNewPasswordForBadUser() throws DataException, Exception{
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		addParam("username","freddy@example.com");
		addParam("email_password","true");
		doPost();
		checkMessage("account_not_found");
		
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

	@Test
	public void testLogout() throws Exception{
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		sess.setCurrentPerson(user);
		addParam("logout", "");
		doPost();
		assertFalse(sess.haveCurrentUser());
		checkRedirect("/login.jsp");
	}
	
	@ConfigFixtures({"wtmp.properties","crosscookie.properties"})
	@DataBaseFixtures("login_wtmp_cookie.xml")
	@Test
	public void testCookie() throws Exception{
		takeBaseline();
		TestTimeService cts = new TestTimeService();
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2017, Calendar.JUNE, 23,9,15);
		cts.setResult(cal.getTime());
		ctx.setService(cts);
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		
		Cookie c = new Cookie(ServletSessionService.WEBAPP_SESSION_COOKIE_NAME, "1-qrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqr");
		req.cookies = new Cookie[]{c};
		
		A person = (A) sess.getCurrentPerson();
		
		assertNotNull(person);
	
		
		
		addParam("logout", "");
		doPost();
		assertFalse(sess.haveCurrentUser());
		checkRedirect("/login.jsp");
		checkDiff("/cleanup.xsl", "logged_out.xml");
	}
	@Test
	@ConfigFixtures("external_logout.properties")
	public void testLogoutWithExternal() throws Exception{
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		sess.setCurrentPerson(user);
		addParam("logout", "");
		doPost();
		assertFalse(sess.haveCurrentUser());
		checkRedirect("http://www.example.com/logout");
		
		
	}
	
	@Test
	@ConfigFixtures("external_logout.properties")
	public void testLogoutWithSU() throws Exception{
		ServletSessionService sess = (ServletSessionService) ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		user.commit();
		sess.setCurrentPerson(user);
		sess.setTempRole(ServletSessionService.BECOME_USER_ROLE);
		
		A user2 = fac.makeBDO();
		user2.setEmail("bill@example.com");
		composite.setPassword(user2,"BillIsDead");
		user2.commit();
		
		assertTrue(sess.canSU(user2));
		sess.su(user2);
		assertTrue(sess.isCurrentPerson(user2));
		addParam("logout", "");
		doPost();
		assertTrue(sess.haveCurrentUser());
		assertTrue(sess.isCurrentPerson(user));
		checkRedirect("/main.jsp");
		
		
	}
	
	@Test
	@ConfigFixtures("/extauth.properties")
	public void testExtAuthLogout() throws Exception{
		req.remote_user="fred";
		ServletSessionService sess = (ServletSessionService) ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		// Need to mark this as properly registered or the session won't auto-populate
		user.setRealmName(WebNameFinder.WEB_NAME, "fred");
		user.setEmail("fred@example.com");
		SignupDateComposite comp = fac.getComposite(SignupDateComposite.class); 
		comp.markSignup(user);
		user.commit();
		// This should auto-populate from remote_user
		assertTrue(sess.isCurrentPerson(user));
		assertTrue(sess.haveCurrentUser());
		addParam("logout", "");
		doPost();
		assertFalse(sess.haveCurrentUser());
		checkRedirect("/login.jsp");
		
	}
	
	@Test
	@ConfigFixtures("/extauth.properties")
	public void testExtAuthLogoutWithSU() throws Exception{
		req.remote_user="fred";
		ServletSessionService sess = (ServletSessionService) ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user =  fac.makeBDO();
		// Need to mark this as properly registered or the session won't auto-populate
		user.setRealmName(WebNameFinder.WEB_NAME, "fred");
		user.setEmail("fred@example.com");
		SignupDateComposite comp = fac.getComposite(SignupDateComposite.class); 
		comp.markSignup(user);
		user.commit();
		// This should auto-populate from remote_user
		assertTrue(sess.isCurrentPerson(user));
		assertTrue(sess.haveCurrentUser());
		sess.setTempRole(ServletSessionService.BECOME_USER_ROLE);
		A user2 = fac.makeBDO();
		user2.setEmail("bill@example.com");
		user2.setRealmName(WebNameFinder.WEB_NAME, "bill");
		user2.commit();
		
		assertTrue(sess.canSU(user2));
		sess.su(user2);
		assertTrue(sess.isCurrentPerson(user2));
		addParam("logout", "");
		doPost();
		assertTrue(sess.haveCurrentUser());
		assertTrue(sess.isCurrentPerson(user));
		checkRedirect("/main.jsp");
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