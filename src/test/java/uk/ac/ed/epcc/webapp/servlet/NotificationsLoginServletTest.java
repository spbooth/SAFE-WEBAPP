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
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider;
import uk.ac.ed.epcc.webapp.session.DatabasePasswordComposite;
import uk.ac.ed.epcc.webapp.session.Hash;
import uk.ac.ed.epcc.webapp.session.MaxNotifyComposite;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.RequiredPage;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.SignupDateComposite;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

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
	 */
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	public void testLoginNotificationReset() throws DataFault, ServletException, IOException{
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.makeBDO();
		PasswordAuthComposite<A> composite = fac.getComposite(PasswordAuthComposite.class);
		user.setEmail("fred@example.com");
		composite.setPassword(user,"FredIsDead");
		MaxNotifyComposite<A> max = fac.getComposite(MaxNotifyComposite.class);
		max.stopNotified(user);
		user.commit();
		assertEquals(5, max.getNotifiedCount(user));
		SessionService<A> sess = ctx.getService(SessionService.class);
		assertFalse(sess.isAuthenticated());
		assertFalse(sess.haveCurrentUser());
		addParam("username", "fred@example.com");
		addParam("password", "FredIsDead");
		doPost();
		loginRedirects();
		 sess = ctx.getService(SessionService.class);
		assertTrue(sess.isAuthenticated());
		assertTrue(sess.haveCurrentUser());
		
		assertEquals(0, max.getNotifiedCount(sess.getCurrentPerson()));
		
	}
	
	
	

}