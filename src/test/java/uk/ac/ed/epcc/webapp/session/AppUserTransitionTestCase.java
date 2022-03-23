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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;

import org.junit.Test;

import jakarta.mail.Message;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUserFactory.UpdatePersonRequiredPage;
import uk.ac.ed.epcc.webapp.session.EmailNameFinder.VerifyEmailRequiredPage;

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
		assertTrue(page.getClass().getCanonicalName(),page instanceof PasswordAuthComposite.PasswordResetRequiredPage);
		assertFalse(page.required(sess));
		page = it.next();
		assertTrue(page.getClass().getCanonicalName(),page instanceof UpdatePersonRequiredPage);
		assertTrue(page.required(sess));
		String return_page = "/noddy.jsp";
		sess.setAttribute(RequiredPage.REQUIRED_PAGE_RETURN_ATTR, return_page);
		FormResult result = page.getPage(sess);
		setTransition((ChainedTransitionResult) result);
		// This is actually an empty form as no user settable details configured
		checkFormContent(null, "details_form.xml");
		addParam(RealNameComposite.FIRSTNAME,"Albert");
		addParam(RealNameComposite.LASTNAME,"Spangler");
		addParam(CertificateComposite.PERSONAL_CERTIFICATE,"/c=UK/o=eScience/ou=Edinburgh/l=NeSC/cn=stephen booth");
		addParam(PublicKeyComposite.PUBLIC_KEY,"ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQBRQkTnsRzUM9mLrgEMFk78CLdOxtepxPp1JQSfRc3/A1cy\n"+
				"D8NV/gxINRNhMIVkIofUexxtLfAfmNRf666SSei/w2kPX9ndOJ32y2OUUKkijJvEdeMEuFido9Kifc79\n"+
				"p0q1KcOhAdRNmmE+LriqsbhJJVQz0OeOKw7wPN9KNYfTevZleQAJBRKr99rBgyRrtrXBhnjYu3yb8E/l\n"+
				"f4g8MiBuLGcezzi310RwKMFnamr6MTbA3KBvgvFrPmsjVyedn1IyMdgQ0x8OZMQbr6hesvnR8HuKYfFt\n"+
				"m4Vjx7bS+Dyqn+PlPrWH/fjs1957fe57gtZ9eM2S0lsv5cagcWghPAZP rsa-key-20110308");
		runTransition();
		if( return_page != null ) {
			checkRedirect(return_page);
		}else {
			AppUserTransitionProvider<A> provider = AppUserTransitionProvider.getInstance(getContext());
			if( provider != null) {
				checkViewRedirect(provider, user);
			}else{
				checkMessage("object_updated");
			}
		}
		checkDiff("/cleanup.xsl", "details.xml");
	}
	@Test
	@ConfigFixtures("require_update2.properties")
	@DataBaseFixtures("details.xml")
	public void testRemoveKey() throws Exception {
		MockTansport.clear();
		takeBaseline();
		setTime(2021, Calendar.MARCH, 1, 9, 0); // so update not forced.
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.findByEmail("fred@example.com");
		
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		AppUserTransitionProvider prov = (AppUserTransitionProvider) TransitionServlet.getProviderFromName(ctx, "Person");
		setTransition(prov, AppUserTransitionProvider.UPDATE, user);
		
		
		
		checkFormContent(null, "details_form2.xml");
		
		addParam(PublicKeyComposite.PUBLIC_KEY+".Text","");
		runTransition();
	
			AppUserTransitionProvider<A> provider = AppUserTransitionProvider.getInstance(getContext());
			if( provider != null) {
				checkViewRedirect(provider, user);
			}else{
				checkMessage("object_updated");
			}
		checkDiff("/cleanup.xsl", "details2.xml");
	}
	@Test
	@DataBaseFixtures("details.xml")
	@ConfigFixtures("require_update.properties")
	public void testEraseTransition() throws Exception {
		MockTansport.clear();
		takeBaseline();
		setTime(2021, Calendar.MARCH, 1, 9, 0); // avoid details warning in view
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
	
		user.setEmail("bill@example.com");
		user.commit();
		
		SessionService<A> sess = ctx.getService(SessionService.class);
		
	
		sess.setCurrentPerson(user);
		sess.setTempRole("TestErase");
		assertTrue(sess.haveCurrentUser());
		
		AppUser target = fac.findByEmail("fred@example.com");
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider,AppUserTransitionProvider.ERASE,target);
		setConfirmTransition(true);
		runTransition();
		
		checkViewRedirect(provider, target);
		checkDiff("/cleanup.xsl", "erased.xml");
		checkViewContent(null, "view_erased.xml");
		
	}
	@Test
	public void testSUTransition() throws DataFault, DataException, ConsistencyError, IOException, TransitionException, ServletException {
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
	
		user.setEmail("fred@example.com");
		user.commit();
		
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setRole(user,ServletSessionService.BECOME_USER_ROLE, true);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		A target = fac.makeBDO();
		target.setEmail("bill@example.com");
		target.commit();
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, AppUserTransitionProvider.SU_KEY, target);
		runTransition();
		checkRedirect("/main.jsp");
		sess = ctx.getService(SessionService.class);
		assertEquals("bill@example.com",sess.getCurrentPerson().getEmail());
		assertTrue(((ServletSessionService)sess).isSU());
	}
	
	@Test
	public void testSetRoleTransition() throws ConsistencyError, Exception {
		MockTansport.clear();
		takeBaseline();
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
	
		user.setEmail("fred@example.com");
		user.commit();
		
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setRole(user,AppUserTransitionProvider.SET_ROLES_ROLE, true);
		sess.setCurrentPerson(user);
		assertTrue(sess.haveCurrentUser());
		
		A target = fac.makeBDO();
		target.setEmail("bill@example.com");
		target.commit();
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, AppUserTransitionProvider.SET_ROLE_KEY, target);
		checkFormContent(null, "set_roles_form.xml");
		addParam("Pig", "Y");
		runTransition();
		checkMessage("roles_updated");
		checkDiff("/cleanup.xsl", "roles.xml");
	}
	
	@Test
	@ConfigFixtures("email_status.properties")
	public void testMarkEmailInvalid() throws Exception {
		MockTansport.clear();
		takeBaseline();
		setTime(2022, Calendar.MARCH, 23, 14, 00);
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		
		Set<RequiredPage<A>> pages = fac.getRequiredPages();
		assertEquals(2, pages.size());
		
		VerifyEmailRequiredPage p = null;
		for(RequiredPage<A> x : pages) {
			if( x instanceof VerifyEmailRequiredPage) {
				p = (VerifyEmailRequiredPage) x;
			}
		}
		assertNotNull("Expect a VerifyEmailRequiredPage",p);
		A user =  fac.makeBDO();
	
		user.setEmail("fred@example.com");
		user.commit();
		EmailNameFinder<A> finder = fac.getComposite(EmailNameFinder.class);
		assertNotNull(finder);
		finder.verified(user);
		assertTrue(finder.useEmailStatus());
		assertTrue(user.allowEmail());
		assertFalse(finder.emailMarkedInvalid(user));
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		assertFalse(p.required(sess));
		
		
		
		A manager = fac.makeBDO();
		manager.setEmail("bill@example.com");
		manager.commit();
		
		
		sess.setRole(manager,EmailNameFinder.INVALIDATE_EMAIL_ROLE, true);
		sess.setCurrentPerson(manager);
		assertTrue(sess.haveCurrentUser());
		
		AppUserTransitionProvider provider = AppUserTransitionProvider.getInstance(ctx);
		setTransition(provider, EmailNameFinder.INVALIDATE_EMAIL, user);
		checkFormContent(null, "invalidate_email_form.xml");
		setAction("Yes");
		runTransition();
		checkViewRedirect(provider, user);
		checkDiff("/cleanup.xsl", "invalidate_email.xml");
		user = fac.find(user.getID());
		assertFalse(user.allowEmail());
		assertTrue(finder.emailMarkedInvalid(user));
		sess.setCurrentPerson(user);
		assertTrue(p.required(sess)); // user now required to verify
	}
}
