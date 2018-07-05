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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.servlet.LoginServlet;
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
		addParam(CodeAuthTransitionProvider.AuthenticateTransition.CODE_FIELD, 278504);
		runTransition();
		checkMessage("invalid_input");
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
	}
	@Test
	public void testGoodCode() throws DataException, TransitionException, ServletException, IOException {
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
		addParam(CodeAuthTransitionProvider.AuthenticateTransition.CODE_FIELD, 278504);
		runTransition();
		assertTrue(ctx.getService(SessionService.class).haveCurrentUser());
		checkRedirect(LoginServlet.getMainPage(ctx));
	}
	@Test
	public void testBadCode() throws DataException, TransitionException, ServletException, IOException {
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
		addParam(CodeAuthTransitionProvider.AuthenticateTransition.CODE_FIELD, 123456);
		runTransition();
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
		checkError(CodeAuthTransitionProvider.AuthenticateTransition.CODE_FIELD, "Incorrect");
		
		//checkRedirect(LoginServlet.getMainPage(ctx));
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
		addParam(AuthorisedConfirmTransition.CODE,278504);
		setConfirmTransition(true);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_cleared.xml");
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
		addParam(TotpCodeAuthComposite.SetToptTransition.CODE, 278504);
		runTransition();
		checkViewRedirect(prov, user);
		checkDiff("/cleanup.xsl", "key_set.xml");
	}
}
