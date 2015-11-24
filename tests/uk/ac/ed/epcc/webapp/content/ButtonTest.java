// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.content;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;

/**
 * Unit tests of the class {@link Button}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class ButtonTest extends WebappTestBase {

	
	/**
	 * Tests the method {@link Button#addContent(SimpleXMLBuilder)}.
	 */
	@Test
	public void testAddContent() {
		Button b = new Button(getContext(), "test button", new RedirectResult("http://test.url"));
		SimpleXMLBuilder hb = new HtmlBuilder();
		b.addContent(hb);
		Assert.assertEquals("<form action='http://test.url'><input type='submit' value='test button'/></form>", hb.toString());
		SimpleXMLBuilder p = new XMLPrinter();
		b.addContent(p);
		Assert.assertEquals("test button", p.toString());
	}
	
	/**
	 * Tests the method {@link Button#getContext()}.
	 */
	@Test
	public void testGetContext() {
		Button b = new Button(getContext(), "test button", new RedirectResult("http://test.url"));
		Assert.assertEquals(getContext(), b.getContext());
	}

	/**
	 * Tests the method {@link Button#toString()}.
	 */
	@Test
	public void testToString() {
		Button b = new Button(getContext(), "test button", new RedirectResult("http://test.url"));
		Assert.assertEquals("test button", b.toString());
	}
}
