package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.Button;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryFinder;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.ViewDummyTransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class MessageServletTest extends ServletTest {
	private static final String DUMMY_INPUT = "@ViewTransitionMapper@Dummy@Henry~1Hoover@@1@";
	private static final String DUMMY_ENCODE = "QFZpZXdUcmFuc2l0aW9uTWFwcGVyQER1bW15QEhlbnJ5fjFIb292ZXJAQDFA";

	@Before
	public void setup() throws ServletException {
		servlet = new MessageServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "MessageServlet");
		servlet.init(config);
	}
	
	private static final String INPUT = "@ViewTransitionMapper@User@jondiscenza~1cirrus@@138689@";
	private static final String ENCODE = "QFZpZXdUcmFuc2l0aW9uTWFwcGVyQFVzZXJAam9uZGlzY2VuemF-MWNpcnJ1c0BAMTM4Njg5QA";

	public MessageServletTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testEncodeString() {
		assertEquals(ENCODE, MessageServlet.encodeString(INPUT));
	}
	
	@Test
	public void testDecodeString() {
		assertEquals(INPUT, MessageServlet.decodeString(ENCODE));
		assertEquals(DUMMY_INPUT, MessageServlet.decodeString(DUMMY_ENCODE));
	}
	/** Test that a ViewTransitonGenerator from a DataObejct is mapped correctly
	 * 
	 * @throws DataException
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void testMapMessage() throws DataException, ServletException, IOException {
		Feature.setTempFeature(ctx, MessageServlet.MAP_MESSAGE,true);
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		Dummy1 d = fac.makeBDO();
		d.setName("Henry@Hoover");
		d.commit();
		MessageResult r = new MessageResult("object_created","Dummy",d);
		RedirectResult f = MessageServlet.mapResult(ctx, r);
		assertNull(f);  // no map without user
		setupPerson("fred@example.com");
		f = MessageServlet.mapResult(ctx, r);
		assertNotNull(f);
		assertEquals(f.getURL(), MessageServlet.MESSAGE_PATH+"1/");
		LinkedList<String> l = (LinkedList<String>) ctx.getService(SessionService.class).getAttribute("SessionPathReWriter_MSG_1");
		assertNotNull(l);
		assertEquals(3,l.size());
		assertEquals("object_created", l.get(0));
		assertEquals(DUMMY_ENCODE, l.get(2));
		req.path_info="/1/";
		doGet();
		checkMessage("object_created");
		Object args[] = getMessageArgs();
		assertEquals(2, args.length);
		assertEquals("Dummy", args[0]);
		assertEquals(d.getUIGenerator(), args[1]);
		checkMessageText("<a href='test/TransitionServlet/Dummy/1'>Henry@Hoover</a> created");
	}
	
	/** Test Buttons also map
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws DataException 
	 * 
	 */
	@Test
	public void testButton() throws ServletException, IOException, DataException {
		Feature.setTempFeature(ctx, MessageServlet.MAP_MESSAGE,true);
		Dummy1.Factory fac = new Dummy1.Factory(ctx);
		Dummy1 d = fac.makeBDO();
		d.setName("Henry@Hoover");
		d.commit();
		TransitionFactoryFinder finder = new TransitionFactoryFinder(ctx);
		// This enures we use the same provider instance throughout so comparison works
		ViewDummyTransitionProvider prov = finder.getProviderFromName(ViewDummyTransitionProvider.class, ViewDummyTransitionProvider.DUMMY_TRANISTION_TAG);
		
		
		//Buttons only work with chained transitions
		
		
		
		Button b= new Button(ctx, "A % Button /", new ChainedTransitionResult(prov, d, null));
		MessageResult r = new MessageResult("object_created","Button",b);
		RedirectResult f = MessageServlet.mapResult(ctx, r);
		assertNull(f);  // no map without user
		setupPerson("fred@example.com");
		f = MessageServlet.mapResult(ctx, r);
		assertNotNull(f);
		assertEquals(f.getURL(), MessageServlet.MESSAGE_PATH+"1/");
		LinkedList<String> l = (LinkedList<String>) ctx.getService(SessionService.class).getAttribute("SessionPathReWriter_MSG_1");
		assertNotNull(l);
		assertEquals(3,l.size());
		assertEquals("object_created", l.get(0));
		assertEquals("QEZybUBCQER1bW15QEBBICUgQnV0dG9uIC9AQDFA", l.get(2));
		req.path_info="/1/";
		doGet();
		checkMessage("object_created");
		Object args[] = getMessageArgs();
		assertEquals(2, args.length);
		assertEquals("Button", args[0]);
		assertEquals(b, args[1]);
		checkMessageText("<form class='button' method='post' action='test/TransitionServlet/Dummy/1'><input class='input_button' type='submit' value='A % Button /'/></form> created");
	}
	
}
