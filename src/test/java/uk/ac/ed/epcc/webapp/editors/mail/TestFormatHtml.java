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
package uk.ac.ed.epcc.webapp.editors.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
public class TestFormatHtml extends WebappTestBase{

	@Test
	public void testFormat() throws MessagingException, WalkerException, IOException{
		Session session = Session.getInstance(getContext().getProperties(),
				null);
		MimeMessage m = new MimeMessage(session, getClass().getResourceAsStream("fake_mime"));
		HtmlBuilder builder = new HtmlBuilder();
		ContentMessageVisitor vis = new ContentMessageVisitor(getContext(),builder, new MessageLinker() {
			
			public MessageProvider getMessageProvider() throws Exception {
				return null;
			}
			
			public void addLink(ContentBuilder builder, List<String> args, String file,
					String text) {
				ExtendedXMLBuilder p = builder.getText();
				for(String s : args){
					p.clean(s);
					p.clean("/");
				}
				p.clean(text);
				p.appendParent();
				
			}
		});
		MessageWalker walker = new MessageWalker(getContext());
		walker.visitMessage(m, vis);
		String expected = readFileAsString("expected_mime");
		assertEquals(expected, builder.toString());
	}
	@Test
	public void testFormat2() throws MessagingException, WalkerException, IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException{
		Session session = Session.getInstance(getContext().getProperties(),
				null);
		MimeMessage m = new MimeMessage(session, getClass().getResourceAsStream("testing.msg"));
		HtmlBuilder builder = new HtmlBuilder();
		ContentMessageVisitor vis = new ContentMessageVisitor(getContext(),builder, new MessageLinker() {
			
			public MessageProvider getMessageProvider() throws Exception {
				return null;
			}
			
			public void addLink(ContentBuilder builder, List<String> args, String file,
					String text) {
				ExtendedXMLBuilder p = builder.getText();
				for(String s : args){
					p.clean(s);
					p.clean("/");
				}
				p.clean(text);
				p.appendParent();
				
			}
		});
		MessageWalker walker = new MessageWalker(getContext());
		walker.visitMessage(m, vis);
		checkContent(null, "expected_msg", builder.toString());
		
	}
	@Test
	public void testEdit() throws MessagingException, WalkerException, IOException{
		Session session = Session.getInstance(getContext().getProperties(),
				null);
		MimeMessage m = new MimeMessage(session, getClass().getResourceAsStream("fake_mime"));
		HtmlBuilder builder = new HtmlBuilder();
		ContentMessageVisitor vis = new EditMessageVisitor(getContext(),builder, new MessageEditLinker() {
			
			public MessageProvider getMessageProvider() throws Exception {
				return null;
			}
			
			public void addLink(ContentBuilder builder, List<String> args, String file,
					String text) {
				ExtendedXMLBuilder p = builder.getText();
				for(String s : args){
					p.clean(s);
					p.clean("/");
				}
				p.clean(text);
				p.appendParent();
				
			}
			
			public void addButton(ContentBuilder builder, EditAction action,
					List<String> path, String text) {
				ExtendedXMLBuilder p = builder.getText();
				for(String s : path){
					p.clean(s);
					p.clean("/");
				}
				p.clean("["+action.toString()+"]");
				p.clean("/");
				p.clean(text);
				p.appendParent();
				
			}
		});
		MessageWalker walker = new MessageWalker(getContext());
		walker.visitMessage(m, vis);
		//FileWriter writer = new FileWriter("tests/uk/ac/ed/epcc/webapp/editors/mail/edit_fake_mime");
		//writer.write(builder.toString());
		//writer.close();
		String expected = readFileAsString("edit_fake_mime");
		assertEquals(expected, builder.toString());
	}

}