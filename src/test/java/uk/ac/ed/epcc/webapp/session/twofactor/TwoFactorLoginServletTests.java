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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Calendar;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.*;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author Stephen Booth
 *
 */
@ConfigFixtures("twofactor.properties")
public class TwoFactorLoginServletTests<A extends AppUser> extends AbstractLoginServletTest<A> {

	
	
	@Test
	public void testLoginWithTwoAuth() throws ConsistencyError, Exception{
		takeBaseline();
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory<A> fac = sess.getLoginFactory();
		
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		
		TotpCodeAuthComposite<A> ta = (TotpCodeAuthComposite<A>) fac.getComposite(FormAuthComposite.class);
		String external = "UJ4SLJJPNPXVGIPLXDTQUGVKNI";
		ta.setSecret(user, external);
		
		user.commit();
		TestTimeService time = new TestTimeService();
		ctx.setService(time);
		
		addParam("username", "fred@example.com");
		addParam("password", "FredIsDead");
		doPost();
		assertFalse(sess.haveCurrentUser());
		CodeAuthTransitionProvider<A> catp = new CodeAuthTransitionProvider<>(ctx);
		checkForwardToTransition(catp, CodeAuthTransitionProvider.AUTHENTICATE, user);
		assertEquals(sess.getAttribute(TwoFactorHandler.AUTH_USER_ATTR), user.getID());
		assertEquals(sess.getAttribute(TwoFactorHandler.AUTH_RESULT_ATTR), new RedirectResult(LoginServlet.getMainPage(ctx)));
		checkDiff("/cleanup.xsl", "initial_auth.xml");
		
	}
	
}
