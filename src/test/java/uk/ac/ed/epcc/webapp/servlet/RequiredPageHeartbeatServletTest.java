//| Copyright - The University of Edinburgh 2017                            |
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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;

import org.junit.Test;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.EmailNameFinder;
import uk.ac.ed.epcc.webapp.session.MaxNotifyComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class RequiredPageHeartbeatServletTest extends HeartbeatServletTest {

	/**
	 * 
	 */
	public RequiredPageHeartbeatServletTest() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.HeartbeatServletTest#makeService()
	 */
	@Override
	public ServletSessionService makeService() {
		return new ServletSessionService(ctx);
	}

	
	
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** No reminders sent till 1 year is up
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 */
	public void testNotifyBeforeThreshold() throws ServletException, IOException, DataException{
		
			MockTansport.clear();
			setTime(2019, Calendar.JULY, 1, 9, 0);
			SessionService p = setupPerson("person1@example.com");
			EmailNameFinder finder = (EmailNameFinder) p.getLoginFactory().getComposite(EmailNameFinder.class);
			AppUser person1 = p.getCurrentPerson();
			person1.markDetailsUpdated();
			finder.verified(person1);
			person1.commit();
			System.out.println(person1.nextRequiredUpdate());
			setTime(2019, Calendar.DECEMBER, 1, 9, 0);
			p = setupPerson("person2@example.com");
			AppUser person2 = p.getCurrentPerson();
			person2.markDetailsUpdated();
			finder.verified(person2);
			person2.commit();
			System.out.println(person2.nextRequiredUpdate());
		
		setTime(2020, Calendar.FEBRUARY, 1, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(0, MockTansport.nSent());
	}
	
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** One account expired the other is  up
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 * @throws MessagingException
	 */
	public void testNotifyThreshold2() throws ServletException, IOException, DataException, MessagingException{
		MockTansport.clear();
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		EmailNameFinder finder = (EmailNameFinder) p.getLoginFactory().getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		person1.commit();
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		setTime(2020, Calendar.JUNE, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(1, MockTansport.nSent());
		Message m = MockTansport.getMessage(0);
		assertEquals("[test] Account update required", m.getSubject());
		assertEquals("\n"
				+ "Dear person1@example.com,\n"
				+ "\n"
				+ "There are some account updates or other actions that need your attention \n"
				+ "in the test Web site:\n"
				+ "\n"
				+ "* Your email will need verifying soon. You will have to so this before 2020-06-30 09:00.\n"
				+ "* Your user details need to be updated/verified before 2020-06-30 09:00\n"
				+ "\n"
				+ "Please log into the test Web site to provide these updates.\n"
				+ "  http://www.example.com/test\n"
				+ "\n"
				+ "\n"
				+ "Regards,\n"
				+ "\n"
				+ "  The test Team\n"
				+ "  \n"
				+ "", m.getContent().toString());
		AppUserFactory<?> login = p.getLoginFactory();
		MaxNotifyComposite comp = login.getComposite(MaxNotifyComposite.class);
		assertNotNull(comp);
		assertEquals(1, comp.getNotifiedCount(login.find(person1.getID())));
	}
	
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** Both accounts are expired
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 * @throws MessagingException
	 */
	public void testNotifyThreshold3() throws ServletException, IOException, DataException, MessagingException{
		MockTansport.clear();
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		EmailNameFinder finder = (EmailNameFinder) p.getLoginFactory().getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		person1.commit();
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		finder.verified(person2);
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		setTime(2021, Calendar.JANUARY, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(2, MockTansport.nSent());
		
		
	}
	
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** Both accounts are expired but global send limit enforced
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 * @throws MessagingException
	 */
	public void testNotifyThreshold3WithGlobalLimit() throws ServletException, IOException, DataException, MessagingException{
		MockTansport.clear();
		ctx.getService(ConfigService.class).setProperty("required_page.notifications.max_batch", "1");
		
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		EmailNameFinder finder = (EmailNameFinder) p.getLoginFactory().getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		person1.commit();
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		finder.verified(person2);
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		setTime(2021, Calendar.JANUARY, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(1, MockTansport.nSent());
		
		
	}
	
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** Both accounts are expired but notification role supresses one
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 * @throws MessagingException
	 */
	public void testNotifyThreshold3WithSelectRole() throws ServletException, IOException, DataException, MessagingException{
		MockTansport.clear();
		ctx.getService(ConfigService.class).setProperty("required_page.notifications.role", "wombat");
		
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		EmailNameFinder finder = (EmailNameFinder) p.getLoginFactory().getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		person1.commit();
		
		p.setRole(person1, "wombat", true);
		
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		finder.verified(person2);
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		setTime(2021, Calendar.JANUARY, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(1, MockTansport.nSent());
		
		
	}
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** Linited number of reminders per person
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 */
	public void testNotifyMaxRemind() throws ServletException, IOException, DataException{
		
		MockTansport.clear();
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		AppUserFactory login = p.getLoginFactory();
		EmailNameFinder finder = (EmailNameFinder) login.getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		MaxNotifyComposite max = (MaxNotifyComposite) p.getLoginFactory().getComposite(MaxNotifyComposite.class);
		max.stopNotified(person1);
		person1.commit();
		person1.commit();
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		setTime(2020, Calendar.JUNE, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(0, MockTansport.nSent());
		
		
		assertEquals(5, max.getNotifiedCount((AppUser) login.find(person1.getID())));
	}
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** Linited number of reminders per person
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 */
	public void testNotifyMinWaitBlock() throws ServletException, IOException, DataException{
		
		MockTansport.clear();
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		AppUserFactory login = p.getLoginFactory();
		EmailNameFinder finder = (EmailNameFinder) login.getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		MaxNotifyComposite max = (MaxNotifyComposite) p.getLoginFactory().getComposite(MaxNotifyComposite.class);
		person1.commit();
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		// mark a notification at 8 am on the same say
		setTime(2020, Calendar.JUNE, 20, 8, 0);
		max.addNotified(person1);
		person1.commit();
		
		setTime(2020, Calendar.JUNE, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(0, MockTansport.nSent());
		
		
		assertEquals(1, max.getNotifiedCount((AppUser) login.find(person1.getID())));
	}
	
	@Test
	@ConfigFixtures("required_page_heartbeat.properties")
	/** Linited number of reminders per person
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws DataException
	 */
	public void testNotifyMinWaitPass() throws ServletException, IOException, DataException{
		
		MockTansport.clear();
		setTime(2019, Calendar.JULY, 1, 9, 0);
		SessionService p = setupPerson("person1@example.com");
		AppUserFactory login = p.getLoginFactory();
		EmailNameFinder finder = (EmailNameFinder) login.getComposite(EmailNameFinder.class);
		AppUser person1 = p.getCurrentPerson();
		person1.markDetailsUpdated();
		finder.verified(person1);
		MaxNotifyComposite max = (MaxNotifyComposite) p.getLoginFactory().getComposite(MaxNotifyComposite.class);
		person1.commit();
		System.out.println(person1.nextRequiredUpdate());
		setTime(2019, Calendar.DECEMBER, 1, 9, 0);
		p = setupPerson("person2@example.com");
		AppUser person2 = p.getCurrentPerson();
		person2.markDetailsUpdated();
		person2.commit();
		System.out.println(person2.nextRequiredUpdate());
		
		// mark a notification at 8 am on the same say
		setTime(2020, Calendar.JUNE, 17, 8, 0);
		max.addNotified(person1);
		person1.commit();
		
		setTime(2020, Calendar.JUNE, 20, 10, 0);
		req.remote_user="fred";
		doPost();
		
		assertEquals(1, MockTansport.nSent());
		
		
		assertEquals(2, max.getNotifiedCount((AppUser) login.find(person1.getID())));
	}
}
