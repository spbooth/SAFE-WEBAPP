package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;

public class MessageServletTest extends WebappTestBase {

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
	}
	
	
}
