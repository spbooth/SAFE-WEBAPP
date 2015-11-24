package uk.ac.ed.epcc.webapp.editors.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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
