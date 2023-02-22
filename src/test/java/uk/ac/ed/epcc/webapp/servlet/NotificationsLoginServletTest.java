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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.junit.Test;

import jakarta.mail.Message;
import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.*;

/**
 * @author spb
 *
 */

public class NotificationsLoginServletTest<A extends AppUser> extends AbstractLoginServletTest<A> {

	/**
	 * 
	 */
	public NotificationsLoginServletTest() {
		
	}
	
	
	/** Test that the MaxNotify counter is reset to zero when a user sucessfully logs in.
	 * 
	 * @throws DataFault
	 * @throws ServletException
	 * @throws IOException
	 * @throws TransitionException 
	 */
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	public void testLoginNotificationReset() throws DataFault, ServletException, IOException, TransitionException{
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();   // create a user
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");    // set email and password
		composite.setPassword(user,"FredIsDead");
		MaxNotifyComposite<A> max = fac.getComposite(MaxNotifyComposite.class);
		max.stopNotified(user);  // set notification value to maximum
		user.commit();
		assertEquals(5, max.getNotifiedCount(user)); // user is at maximum
		SessionService<A> sess = ctx.getService(SessionService.class);
		assertFalse(sess.isAuthenticated());
		assertFalse(sess.haveCurrentUser());
		addParam("username", "fred@example.com");
		addParam("password", "FredIsDead");
		doPost();          // simulate a login
		
		
		
		 sess = ctx.getService(SessionService.class);
		assertTrue(sess.isAuthenticated());  // user has logged in
		assertTrue(sess.haveCurrentUser());
		
		assertEquals(0, max.getNotifiedCount(sess.getCurrentPerson())); // counter has been reset
		checkRedirect(RequiredPageServlet.URL);
		resetRequest();
		servlet = new RequiredPageServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "RequiredPageServlet");
		servlet.init(config);
		req.servlet_path=RequiredPageServlet.URL;
		doPost();
		checkForwardToTransition(AppUserTransitionProvider.getInstance(ctx), EmailNameFinder.CHANGE_EMAIL, sess.getCurrentPerson());
	}
	
	
	

}