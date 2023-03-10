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
package uk.ac.ed.epcc.webapp.session.twofactor;

import static org.junit.Assert.*;
import static uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite.CODE;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.logging.debug.DebugLoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.servlet.LoginServlet;
import uk.ac.ed.epcc.webapp.servlet.MockRandomService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author Stephen Booth
 *
 */
@DataBaseFixtures("initial_auth.xml")
@ConfigFixtures("twofactor.properties")
public class TwoFactorAuthTests<A extends AppUser> extends AbstractTransitionServletTest {

	@Test
	public void testNoCookie() throws DataException, TransitionException, ServletException, IOException {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 278504);
		runTransition();
		checkMessage("invalid_input");
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
	}
	@Test
	public void testGoodCode() throws Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		takeBaseline();
		
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		sess.setAttribute(TwoFactorHandler.AUTH_USER_ATTR, user.getID());
		sess.setAttribute(TwoFactorHandler.AUTH_RESULT_ATTR, new RedirectResult(LoginServlet.getMainPage(ctx)));
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 278504);
		runTransition();
		assertTrue(ctx.getService(SessionService.class).haveCurrentUser());
		checkRedirect(LoginServlet.getMainPage(ctx));
		checkDiff("/cleanup.xsl", "good_code.xml");
		
	}
	
	@Test
	@DataBaseFixtures("good_code.xml")
	public void testReUseGoodCode() throws Exception {
		Feature.setTempFeature(ctx, DebugLoggerService.FATAL_FEATURE, false);
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		cal.set(Calendar.SECOND,20); // 20 seconds later still in same code window
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		takeBaseline();
		
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		sess.setAttribute(TwoFactorHandler.AUTH_USER_ATTR, user.getID());
		sess.setAttribute(TwoFactorHandler.AUTH_RESULT_ATTR, new RedirectResult(LoginServlet.getMainPage(ctx)));
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 278504);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		
	}
	@Test
	public void testBadCode() throws Exception {
		MockTansport.clear();
		takeBaseline();
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		sess.setAttribute(TwoFactorHandler.AUTH_USER_ATTR, user.getID());
		sess.setAttribute(TwoFactorHandler.AUTH_RESULT_ATTR, new RedirectResult(LoginServlet.getMainPage(ctx)));
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 123456);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		TotpCodeAuthComposite comp = fac.getComposite(FormAuthComposite.class);
		assertNotNull(comp);
		//assertEquals(1,comp.getFailCount(user));
		user = fac.findByEmail("fred@example.com");
		assertEquals(1,comp.getFailCount(user));
		//checkRedirect(LoginServlet.getMainPage(ctx));
		checkDiff("/cleanup.xsl", "bad_code.xml");
		assertEquals(0, MockTansport.nSent());
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 123456);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		user = fac.findByEmail("fred@example.com");
		assertEquals(2,comp.getFailCount(user));
		assertEquals(0, MockTansport.nSent());
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 123456);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		user = fac.findByEmail("fred@example.com");
		assertEquals(3,comp.getFailCount(user));
		assertEquals(0, MockTansport.nSent());
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 123456);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		user = fac.findByEmail("fred@example.com");
		assertEquals(4,comp.getFailCount(user));
		assertEquals(1, MockTansport.nSent());
		assertEquals("test 2FA token has been locked", MockTansport.getMessage(0).getSubject());
		MockTansport.clear();
		Feature.setTempFeature(ctx,  DebugLoggerService.FATAL_FEATURE, false);
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 278504);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		user = fac.findByEmail("fred@example.com");
		assertEquals(4,comp.getFailCount(user));  // 
		assertEquals(0, MockTansport.nSent());
	}
	@Test
	public void testBadCodeCorrected() throws Exception {
		MockTansport.clear();
		takeBaseline();
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		sess.setAttribute(TwoFactorHandler.AUTH_USER_ATTR, user.getID());
		sess.setAttribute(TwoFactorHandler.AUTH_RESULT_ATTR, new RedirectResult(LoginServlet.getMainPage(ctx)));
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 123456);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CODE, "Incorrect");
		TotpCodeAuthComposite comp = fac.getComposite(FormAuthComposite.class);
		assertNotNull(comp);
		//assertEquals(1,comp.getFailCount(user));
		user = fac.findByEmail("fred@example.com");
		assertEquals(1,comp.getFailCount(user));
		//checkRedirect(LoginServlet.getMainPage(ctx));
		checkDiff("/cleanup.xsl", "bad_code.xml");
		assertEquals(0, MockTansport.nSent());
		
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 278504);
		runTransition();
		user = fac.findByEmail("fred@example.com");
		assertEquals(0,comp.getFailCount(user)); // fail count reset by sucessful login
		assertEquals(0, MockTansport.nSent());
		assertTrue(ctx.getService(SessionService.class).haveCurrentUser());
		checkRedirect(LoginServlet.getMainPage(ctx));
	}
	@Test
	public void testClearKey() throws ConsistencyError, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		setTransition(prov, TotpCodeAuthComposite.CLEAR_KEY, user);
		addParam(CODE,278504);
		setConfirmTransition(true);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_cleared.xml");
	}
	@Test
	public void testClearKeyNoCode() throws ConsistencyError, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		setTransition(prov, TotpCodeAuthComposite.CLEAR_KEY, user);
		setConfirmTransition(true);
		runTransition();
		checkMissing(CODE);
		
	}
	
	@Test
	public void testClearKeyNoCodeRecentAuth() throws ConsistencyError, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 20);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		FormAuthComposite comp = fac.getComposite(FormAuthComposite.class);
		comp.authenticated();	
		cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22); // 2 min later
		serv.setResult(cal.getTime());		
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		setTransition(prov, TotpCodeAuthComposite.CLEAR_KEY, user);
		
		setConfirmTransition(true);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_cleared.xml");
	}
	
	@Test
	public void testClearKeyNoCodeNonRecentAuth() throws ConsistencyError, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 19, 00);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		FormAuthComposite comp = fac.getComposite(FormAuthComposite.class);
		comp.authenticated();	
		cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22); // lhr 20 min later
		serv.setResult(cal.getTime());		
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		setTransition(prov, TotpCodeAuthComposite.CLEAR_KEY, user);
		
		setConfirmTransition(true);
		runTransition();
		checkMissing(CODE);
	}
	@Test
	public void testClearKeyWithRole() throws ConsistencyError, Exception {
		
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		sess.setTempRole(TotpCodeAuthComposite.REMOVE_KEY_ROLE);
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		setTransition(prov, TotpCodeAuthComposite.CLEAR_KEY, user);
		
		setConfirmTransition(true);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_cleared.xml");
	}
	
	@Test
	@DataBaseFixtures("key_cleared.xml")
	public void setNewKey() throws DataException, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		sess.setAttribute(TotpCodeAuthComposite.SetToptTransition.NEW_AUTH_KEY_ATTR, "UJ4SLJJPNPXVGIPLXDTQUGVKNI");
		setTransition(prov, TotpCodeAuthComposite.SET_KEY, user);
		checkFormContent("/normalize.xsl", "key_set_form.xml");
		addParam(TotpCodeAuthComposite.SetToptTransition.NEW_CODE, 278504);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_set.xml");
	}
	
	
	@Test
	@DataBaseFixtures({"key_cleared.xml","key_set.xml"})
	public void changeKey() throws DataException, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		sess.setAttribute(TotpCodeAuthComposite.SetToptTransition.NEW_AUTH_KEY_ATTR, "BZSQEG4RK7RLWFW6XFS7AUGOLM");
		setTransition(prov, TotpCodeAuthComposite.SET_KEY, user);
		checkFormContent("/normalize.xsl", "key_change_form.xml");
		addParam(CodeAuthComposite.CODE, 278504);
		addParam(TotpCodeAuthComposite.SetToptTransition.NEW_CODE, 176952);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_changed.xml");
	}
	@Test
	@DataBaseFixtures({"key_cleared.xml","key_set.xml"})
	public void recoveryCode() throws DataException, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		ctx.setService(new MockRandomService());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		sess.setCurrentPerson(user);
		takeBaseline();
		
		AppUserTransitionProvider prov = AppUserTransitionProvider.getInstance(ctx);
		assertNotNull("No Person transition",prov);
		
		setTransition(prov, TotpCodeAuthComposite.RECOVERY, user);
		checkFormContent("/normalize.xsl", "recovery_form.xml");
		setAction("CreateCode");
		runTransition();
		checkMessage("recovery_token_set");
		checkDiff("/cleanup.xsl", "recovery_code.xml");
	}
	
	@Test
	@DataBaseFixtures({"key_cleared.xml","key_set.xml","recovery_code.xml"})
	public void testUseRecoveryCode() throws Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 3, 20, 22);
		serv.setResult(cal.getTime());
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		A user = fac.findByEmail("fred@example.com");
		takeBaseline();
		
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		sess.setAttribute(TwoFactorHandler.AUTH_USER_ATTR, user.getID());
		sess.setAttribute(TwoFactorHandler.AUTH_RESULT_ATTR, new RedirectResult(LoginServlet.getMainPage(ctx)));
		setTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		addParam(CODE, 1000000);
		runTransition();
		assertTrue(ctx.getService(SessionService.class).haveCurrentUser());
		checkRedirect(LoginServlet.getMainPage(ctx));
		checkDiff("/cleanup.xsl", "use_recovery_code.xml");
	}
}
