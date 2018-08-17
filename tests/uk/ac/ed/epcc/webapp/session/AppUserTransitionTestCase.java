//| Copyright - The University of Edinburgh 2018                            |
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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.mail.Message;
import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.session.AppUserFactory.UpdatePersonRequiredPage;

/**
 * @author Stephen Booth
 *
 */
public class AppUserTransitionTestCase<A extends AppUser> extends AbstractTransitionServletTest {
	@Test
	public void testPasswordChange() throws DataFault, ServletException, IOException, TransitionException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, PasswordAuthComposite.CHANGE_PASSWORD, user);

		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheSpider");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,sent);
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		runTransition();
		checkMessage("password_changed");
		assertTrue(composite.checkPassword(user, "BorisTheSpider"));
	}
	
	@Test
	@ConfigFixtures("old_password.properties")
	public void testWrongPassword() throws DataFault, ServletException, IOException, TransitionException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, PasswordAuthComposite.CHANGE_PASSWORD, user);

		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheSpider");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,"womble"); 
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		runTransition();
		
		checkError(PasswordUpdateFormBuilder.PASSWORD_FIELD,"Does not match your current password");
	}
	@Test
	public void testMisMatch() throws DataFault, ServletException, IOException, TransitionException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, PasswordAuthComposite.CHANGE_PASSWORD, user);

		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheRussian");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,sent);
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		runTransition();
		checkError(MapForm.GENERAL_ERROR,"New Passwords don't match");
	}
	
	@Test
	public void testSimple() throws DataFault, ServletException, IOException, TransitionException {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		String sent = composite.firstPassword(user);
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, PasswordAuthComposite.CHANGE_PASSWORD, user);

		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"Boris");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"Boris");
		// Do we really need this to be provided on a forced update
		addParam(PasswordUpdateFormBuilder.PASSWORD_FIELD,sent); 
		runTransition();
		checkError(PasswordUpdateFormBuilder.NEW_PASSWORD1,"Password is too short must be at least 8 characters");
	}
	
	
	@Test
	public void testRequestEmailChange() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
	
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		user.setEmail("fred@example.com");
		user.commit();
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, EmailNameFinder.CHANGE_EMAIL, user);
		addParam(EmailNameFinder.EMAIL, "bilbo@example.com");
		setAction(EmailChangeRequestFactory.REQUEST_ACTION);
		runTransition();
		checkMessage("email_change_request_made");
		assertEquals(1,MockTansport.nSent());
		Message message = MockTansport.getMessage(0);
		assertEquals(ctx.expandText("${service.name} Email Change request Request"),message.getSubject());
		assertEquals("bilbo@example.com",message.getAllRecipients()[0].toString());
		checkDiff("/cleanup.xsl", "../servlet/email_change.xml");
	
	}
	
	@Test
	public void testRequirePasswordChange() throws Exception {
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.randomisePassword(user);
		user.commit();
		assertTrue(composite.mustResetPassword(user));
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		Set<RequiredPage<A>> requiredPages = fac.getRequiredPages();
		assertEquals(1,requiredPages.size());
		RequiredPage<A> page = requiredPages.iterator().next();
		assertTrue(page.getClass().getCanonicalName(),page instanceof PasswordAuthComposite.PasswordResetRequiredPage);
		assertTrue(page.required(sess));
		FormResult result = page.getPage(sess);
		setTransition((ChainedTransitionResult) result);
		checkFormContent(null, "password_change_form.xml");
	}
	@Test
	@ConfigFixtures("require_update.properties")
	public void testUpdateDetails() throws Exception {
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user, "ThisIsaPassword");
		user.commit();
		assertFalse(composite.mustResetPassword(user));
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		Set<RequiredPage<A>> requiredPages = fac.getRequiredPages();
		assertEquals(2,requiredPages.size());
		Iterator<RequiredPage<A>> it = requiredPages.iterator();
		RequiredPage<A> page = it.next();
		//assertTrue(page.getClass().getCanonicalName(),page instanceof PasswordAuthComposite.PasswordResetRequiredPage);
		//assertFalse(page.required(sess));
		//page = it.next();
		assertTrue(page.getClass().getCanonicalName(),page instanceof UpdatePersonRequiredPage);
		assertTrue(page.required(sess));
		FormResult result = page.getPage(sess);
		setTransition((ChainedTransitionResult) result);
		// This is actually an empty form as no user settable details configured
		checkFormContent(null, "details_form.xml");
		addParam(RealNameComposite.FIRSTNAME,"Albert");
		addParam(RealNameComposite.LASTNAME,"Spangler");
		runTransition();
		checkMessage("object_updated");
		checkDiff("/cleanup.xsl", "details.xml");
	}
}
