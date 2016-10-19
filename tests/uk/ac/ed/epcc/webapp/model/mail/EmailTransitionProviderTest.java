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
package uk.ac.ed.epcc.webapp.model.mail;


import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.util.LinkedList;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.editors.mail.EditAction;
import uk.ac.ed.epcc.webapp.editors.mail.EmailTransitionProvider;
import uk.ac.ed.epcc.webapp.editors.mail.MailTarget;
import uk.ac.ed.epcc.webapp.editors.mail.MessageHandler;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockPart;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class EmailTransitionProviderTest extends AbstractTransitionServletTest {
	
	/** setup a valid operator 
	 * 
	 * @throws DataException
	 */
	public SessionService setupPerson() throws DataException{
		return setupPerson("dummy@example.com");
	}
	
	@Test
	public void testCreateEmail() throws Exception{
		setupPerson();
		TestingMessageHandlerFactory fac = new TestingMessageHandlerFactory(ctx,"TestMessage");
		EmailTransitionProvider prov = new EmailTransitionProvider(fac);
		takeBaseline();
		setTransition(prov,EditAction.New,null);
		runTransition();
		MailTarget target = (MailTarget) checkViewContent("/normalize.xsl","view_new_email.xml");
		checkViewForward(prov,target);
		checkDiff("/cleanup.xsl","new_compose.xml");
	}
	
	@Test
	@DataBaseFixtures("new_compose.xml")
	public void testEditSubject() throws Exception{
		SessionService user  = setupPerson();
		TestingMessageHandlerFactory<?> fac = new TestingMessageHandlerFactory(ctx,"TestMessage");
		EmailTransitionProvider prov = new EmailTransitionProvider(fac);
		MessageHandler hand = fac.getHandler(1,user);
		LinkedList<String> path = new LinkedList<String>();
		path.add("Subject");
		MailTarget target = new MailTarget(hand,0,path);
		takeBaseline();
		setTransition(prov,EditAction.EditSubject,target);
		checkFormContent("/normalize.xsl","edit_subject_form.xml");
		addParam("text","to change");
		runTransition();
		checkDiff("/cleanup.xsl","subject_edit.xml");
		target = new MailTarget(fac.getHandler(1,user));
		checkViewRedirect(prov,target);
		target = (MailTarget) checkViewContent("/normalize.xsl","view_change_subject.xml");
		assertEquals("to change",fac.find(1).getMessage().getSubject());
	
		
		
	}
	
	@Test
	@DataBaseFixtures({"new_compose.xml","subject_edit.xml"})
	public void testAddRecipient() throws Exception{
		SessionService user  = setupPerson();
		TestingMessageHandlerFactory<?> fac = new TestingMessageHandlerFactory(ctx,"TestMessage");
		EmailTransitionProvider prov = new EmailTransitionProvider(fac);
		MessageHandler hand = fac.getHandler(1,user);
		LinkedList<String> path = new LinkedList<String>();
		MailTarget target = new MailTarget(hand,0,path);
		takeBaseline();
		setTransition(prov,EditAction.AddRecipient,target);
		checkFormContent("/normalize.xsl","edit_recipient_form.xml");
		addParam("text","boris@example.xcom");
		setAction(EditAction.AddCC.toString());
		runTransition();
		target = new MailTarget(fac.getHandler(1,user));
		checkViewRedirect(prov,target);
		target = (MailTarget) checkViewContent("/normalize.xsl","view_add_recipient.xml");
		checkDiff("/cleanup.xsl","add_recipient.xml");
		
	}
	@Test
	@DataBaseFixtures({"new_compose.xml","subject_edit.xml","add_recipient.xml","edit_text.xml"})
	public void addAttachment() throws Exception{
		SessionService user  = setupPerson();
		TestingMessageHandlerFactory<?> fac = new TestingMessageHandlerFactory(ctx,"TestMessage");
		EmailTransitionProvider prov = new EmailTransitionProvider(fac);
		MessageHandler hand = fac.getHandler(1,user);
		LinkedList<String> path = new LinkedList<String>();
		MailTarget target = new MailTarget(hand,0,path);
		takeBaseline();
		setTransition(prov,EditAction.AddAttachment,target);
		checkFormContent("/normalize.xsl","add_attachment_form.xml");
		MockPart part = new MockPart("text");
		part.setFileName("hello.txt");
		part.data.setMimeType("text/plain");
		PrintWriter w = new PrintWriter(part.data.getOutputStream());
		w.println("hello world");
		w.close();
		req.addPart(part);
		runTransition();
		target = new MailTarget(fac.getHandler(1,user));
		checkViewRedirect(prov,target);
		target = (MailTarget) checkViewContent("/normalize.xsl","view_add_attachment.xml");
		checkDiff("/cleanup.xsl","add_attachment.xml");
	}
	
	@Test
	@DataBaseFixtures({"new_compose.xml","subject_edit.xml","add_recipient.xml"})
	public void editText() throws Exception{
		SessionService user  = setupPerson();
		TestingMessageHandlerFactory<?> fac = new TestingMessageHandlerFactory(ctx,"TestMessage");
		EmailTransitionProvider prov = new EmailTransitionProvider(fac);
		MessageHandler hand = fac.getHandler(1,user);
		LinkedList<String> path = new LinkedList<String>();
		//path.add("0");
		path.add("T");
		MailTarget target = new MailTarget(hand,0,path);
		takeBaseline();
		setTransition(prov,EditAction.Edit,target);
		checkFormContent("/normalize.xsl","edit_text_form.xml");
		addParam("text","Now is the winter of our discontent");
		runTransition();
		target = new MailTarget(fac.getHandler(1,user));
		checkViewRedirect(prov,target);
		target = (MailTarget) checkViewContent("/normalize.xsl","view_edit_text.xml");
		checkDiff("/cleanup.xsl","edit_text.xml");
	}
	
	@Test
	@DataBaseFixtures({"new_compose.xml","subject_edit.xml","add_recipient.xml"})
	public void editUnicodeText() throws Exception{
		SessionService user  = setupPerson();
		TestingMessageHandlerFactory<?> fac = new TestingMessageHandlerFactory(ctx,"TestMessage");
		EmailTransitionProvider prov = new EmailTransitionProvider(fac);
		MessageHandler hand = fac.getHandler(1,user);
		LinkedList<String> path = new LinkedList<String>();
		//path.add("0");
		path.add("T");
		MailTarget target = new MailTarget(hand,0,path);
		takeBaseline();
		setTransition(prov,EditAction.Edit,target);
		checkFormContent("/normalize.xsl","edit_text_form.xml");
		addParam("text","Look a \u00f6 character");
		runTransition();
		target = new MailTarget(fac.getHandler(1,user));
		checkViewRedirect(prov,target);
		target = (MailTarget) checkViewContent("/normalize.xsl","view_unicode_text.xml");
		checkDiff("/cleanup.xsl","edit_text.xml");
	}
}