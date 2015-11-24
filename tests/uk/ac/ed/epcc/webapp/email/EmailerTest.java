/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.content.TemplateFile;

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
		Emailer mailer = new Emailer(getContext());
		Session sess = mailer.getSession();
		Transport t = sess.getTransport("smtp");
		assertEquals("Transport", MockTansport.class,t.getClass());;
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
		Emailer.errorEmail(ctx, "Some error text");
		assertEquals("One message sent",1,MockTansport.nSent());
		assertEquals("error@example.org", MockTansport.getAddress(0)[0].toString());
		assertEquals("from@example.org", MockTansport.getMessage(0).getFrom()[0].toString());
	}
	
	@Test
	public void testErrorMailWithThrowable() throws Exception{
		Emailer.errorEmail(ctx, new Exception("HairyHamster",new Exception("Penguins")),new HashMap(),"A test error");
		
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
	public void testTemplateEmail() throws IOException, MessagingException{
		Emailer mailer = new Emailer(ctx);
		File f = new File("test_templates/test_email.txt");
		TemplateFile tf = TemplateFile.getTemplateFile(f.getAbsolutePath()); // Load the page template
		mailer.templateEmail("user@example.com", tf);
		assertTrue(MockTansport.nSent()==1);
		assertEquals("user@example.com", MockTansport.getAddress(0)[0].toString());
		assertEquals("A test Email",MockTansport.getMessage(0).getSubject());
		
	}
}
