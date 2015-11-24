// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
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
		LoginServlet serv = new LoginServlet();
		
		req.addParameter("username", "fred@example.com");
		req.addParameter("password", "FredIsDead");
		serv.doPost(req, res,ctx);
		assertEquals("test/main.jsp",res.redirect);
		
		
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
		LoginServlet serv = new LoginServlet();
		
		req.addParameter("username", "bill@example.com");
		req.addParameter("password", "FredIsDead");
		serv.doPost(req, res,ctx);
		assertEquals("test/login.jsp?error=login",res.redirect);
		
		
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
		LoginServlet serv = new LoginServlet();
		
		req.addParameter("username", "fred@example.com");
		req.addParameter("password", "FredIsAlive");
		serv.doPost(req, res,ctx);
		assertEquals("test/login.jsp?error=login",res.redirect);
		
		
	}


	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		req.servlet_path="LoginServlet";
	}

	@Override
	public void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
