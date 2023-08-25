//| Copyright - The University of Edinburgh 2015                            |
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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;

import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;
import uk.ac.ed.epcc.webapp.resource.ResourceService;

/** Mock based tests of {@link Emailer}.
 * 
 * Note you need test_settings in the classpath to reconfigure
 * javamail to use the MockTransport
 * 
 * @author spb
 *
 */
public class EmailerTest extends WebappTestBase {


	@Before
	public void clear(){
		MockTansport.clear();
	}
	@Test
	public void testMocked() throws NoSuchProviderException{
		Emailer mailer = Emailer.getFactory(getContext());
		Session sess = mailer.getSession();
		Transport t = sess.getTransport("smtp");
		assertEquals("Transport", MockTansport.class,t.getClass());
	}
	@Test
	public void testUndisclosed() throws AddressException{
		new InternetAddress("undisclosed-recipients:;");
	}
	
	@Test
	public void testInfoMail() throws IOException, MessagingException{
		
		Emailer.infoEmail(ctx, "Some info text");
		assertEquals("One message sent",1,MockTansport.nSent());
		assertEquals("info@example.org", MockTansport.getAddress(0)[0].toString());
		assertEquals("from@example.org", MockTansport.getMessage(0).getFrom()[0].toString());
		
	}
	
	@Test
	public void testErrorMail() throws IOException, MessagingException{
		Emailer mailer = Emailer.getFactory(ctx);
		mailer.errorEmail(log, null,"Some error text");
		assertEquals("One message sent",1,MockTansport.nSent());
		assertEquals("error@example.org", MockTansport.getAddress(0)[0].toString());
		assertEquals("from@example.org", MockTansport.getMessage(0).getFrom()[0].toString());
	}
	
	@Test
	public void testErrorMailWithThrowable() throws Exception{
		Emailer mailer = Emailer.getFactory(ctx);
		mailer.errorEmail( log,new Exception("HairyHamster",new Exception("Penguins")),new HashMap(),"A test error");
		
		assertEquals("One message sent",1,MockTansport.nSent());
		assertEquals("error@example.org", MockTansport.getAddress(0)[0].toString());
		assertEquals("from@example.org", MockTansport.getMessage(0).getFrom()[0].toString());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		MockTansport.getMessage(0).writeTo(stream);
		System.out.println(stream.toString());
		assertTrue(stream.toString().contains("HairyHamster"));
		assertTrue(stream.toString().contains("Penguins"));
	}
	@Test
	public void testTemplateEmail() throws IOException, MessagingException, InvalidArgument, DataFault{
		Emailer mailer = Emailer.getFactory(ctx);
	
		TemplateFile tf = TemplateFile.getFromString(getResourceAsString("/test_templates/test_email.txt")); // Load the page template
		mailer.doSend(mailer.templateMessage("user@example.com", null, tf));
		deferredEmails();
		assertTrue(MockTansport.nSent()==1);
		assertEquals("user@example.com", MockTansport.getAddress(0)[0].toString());
		assertEquals("A test Email",MockTansport.getMessage(0).getSubject());
		
	}
	
	@Test
	@ConfigFixtures("queued.properties")
	public void testQueuedTemplateEmail() throws Exception{
		MockTansport.clear();
		MockTansport.setConnectFail(true);
		setTime(2023, Calendar.AUGUST, 25, 15, 30);
		takeBaseline();
		Emailer mailer = Emailer.getFactory(ctx);
		assertTrue(Emailer.EMAIL_QUEUE_FAILS_FEATURE.isEnabled(ctx));
	
		TemplateFile tf = TemplateFile.getFromString(getResourceAsString("/test_templates/test_email.txt")); // Load the page template
		mailer.doSend(mailer.templateMessage("user@example.com", null, tf));
		deferredEmails();
		assertTrue(MockTansport.nSent()==0);
		checkDiff(null, "queued.xml");
		
		// Attempt a retry with connection still failing
		setTime(2023, Calendar.AUGUST, 25, 15, 35);
		QueuedMessages fac = QueuedMessages.getFactory(ctx);
		fac.retry();
		assertTrue(MockTansport.nSent()==0);
		assertEquals(1, fac.getCount(null));
		checkDiff(null, "retry_fail.xml");
		
		// attempt second retry with good conenction
		setTime(2023, Calendar.AUGUST, 25, 15, 40);
		MockTansport.setConnectFail(false);
		fac.retry();
		assertTrue(MockTansport.nSent()==1);
		assertEquals("user@example.com", MockTansport.getAddress(0)[0].toString());
		assertEquals("A test Email",MockTansport.getMessage(0).getSubject());
		assertEquals(0, fac.getCount(null));
		checkDiff(null, "retry_send.xml");
		MockTansport.clear();
	}
	
	
	@Test
	@ConfigFixtures("force_queued.properties")
	public void testForceQueuedTemplateEmail() throws Exception{
		MockTansport.clear();
		MockTansport.setConnectFail(false);
		setTime(2023, Calendar.AUGUST, 25, 15, 30);
		takeBaseline();
		Emailer mailer = Emailer.getFactory(ctx);
		assertTrue(Emailer.EMAIL_FORCE_QUEUE_FEATURE.isEnabled(ctx));
	
		TemplateFile tf = TemplateFile.getFromString(getResourceAsString("/test_templates/test_email.txt")); // Load the page template
		mailer.doSend(mailer.templateMessage("user@example.com", null, tf));
		
		mailer.doSend(mailer.templateMessage("user2@example.com", null, tf));
		deferredEmails();
		assertTrue(MockTansport.nSent()==0);
		checkDiff(null, "queued2.xml");
		takeBaseline();
		
		// Attempt a retry with connection still failing
		setTime(2023, Calendar.AUGUST, 25, 15, 35);
		QueuedMessages fac = QueuedMessages.getFactory(ctx);
		fac.retry();
		assertTrue(MockTansport.nSent()==0);
		assertEquals(2, fac.getCount(null));
		checkUnchanged(); // Should be disabled if queue forced
		
		StreamData sd = fac.exportMessages();
		assertEquals(0, fac.getCount(null)); // should be removed
		checkDiff(null, "exported.xml");
		System.out.println(sd.toString());
		
		takeBaseline();
		fac.importMessages(sd);
		assertEquals(2, fac.getCount(null)); // should be restored
		checkDiff(null, "imported.xml");
		
	}
	@Test(expected=jakarta.mail.SendFailedException.class)
	@ConfigFixtures("blacklist.properties")
	public void testBlacklist() throws IOException, MessagingException, InvalidArgument, DataFault{
		Emailer mailer = Emailer.getFactory(ctx);
	
		TemplateFile tf = TemplateFile.getFromString(getResourceAsString("/test_templates/test_email.txt")); // Load the page template

		mailer.doSendNow(mailer.templateMessage("user@example.com", null, tf));
		
	}
	
	@Test
	public void testGroupAddress() throws AddressException, UnsupportedEncodingException {
		
		InternetAddress from = new InternetAddress("project-managers:;");
		System.out.println(from.toString());
	}
}