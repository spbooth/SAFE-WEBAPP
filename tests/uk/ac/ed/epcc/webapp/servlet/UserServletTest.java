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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.PasswordUpdateFormBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class UserServletTest<A extends AppUser> extends ServletTest {
	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		servlet=new UserServlet<A>();
		MockServletConfig config = new MockServletConfig(serv_ctx, "UserServlet");
		servlet.init(config);
		req.servlet_path="UserServlet";
	}

	
	@Test
	public void testPasswordChange() throws DataFault, ServletException, IOException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		addParam("form_url","/scripts/password_update.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheSpider");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,sent);
		addParam("action",UserServlet.CHANGE_PASSWORD); 
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		doPost();
		checkMessage("password_changed");
		assertTrue(composite.checkPassword(user, "BorisTheSpider"));
	}
	
	@Test
	@ConfigFixtures("old_password.properties")
	public void testWrongPassword() throws DataFault, ServletException, IOException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		addParam("form_url","/scripts/password_update.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheSpider");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,"womble");
		addParam("action",UserServlet.CHANGE_PASSWORD); 
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		doPost();
		checkError("/scripts/password_update.jsp",PasswordUpdateFormBuilder.PASSWORD_FIELD,"Does not match your current password");
	}
	@Test
	public void testMisMatch() throws DataFault, ServletException, IOException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		addParam("form_url","/scripts/password_update.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheRussian");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,sent);
		addParam("action",UserServlet.CHANGE_PASSWORD);
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		doPost();
		checkError("/scripts/password_update.jsp",MapForm.GENERAL_ERROR,"New Passwords don't match");
	}
	
	@Test
	public void testSimple() throws DataFault, ServletException, IOException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		addParam("form_url","/scripts/password_update.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"Boris");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"Boris");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,sent);
		addParam("action",UserServlet.CHANGE_PASSWORD); 
		doPost();
		checkError("/scripts/password_update.jsp",PasswordUpdateFormBuilder.NEW_PASSWORD1,"Password is too short must be at least 8 characters");
	}
}
