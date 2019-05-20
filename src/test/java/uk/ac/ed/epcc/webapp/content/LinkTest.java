//| Copyright - The University of Edinburgh 2012                            |
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
package uk.ac.ed.epcc.webapp.content;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;

/**
 * Unit tests of the class {@link Link}.
 * 
 * @author aheyrovs
 *
 */

public class LinkTest extends WebappTestBase {

	

	/**
	 * Tests the method {@link Link#addContent(SimpleXMLBuilder)}.
	 */
	@Test
	public void testAddContent() {
		Link link = new Link(getContext(), "test link", new RedirectResult("http://test.url"));
		HtmlBuilder hb = new HtmlBuilder();
		link.addContent((SimpleXMLBuilder) hb);
		Assert.assertEquals("<a href='http://test.url'>test link</a>", hb.toString());
		XMLPrinter p = new XMLPrinter();
		link.addContent(p);
		Assert.assertEquals("test link", p.toString());
	}
	
	/**
	 * Tests the method {@link Link#getContext()}.
	 */
	@Test
	public void testGetContext() {
		Link link = new Link(getContext(), "test link", new RedirectResult("http://test.url"));
		Assert.assertEquals(getContext(), link.getContext());
	}
	
	/**
	 * Tests the method {@link Link#toString()}.
	 */
	@Test
	public void testToString() {
		Link link = new Link(getContext(), "test link", new RedirectResult("http://test.url"));
		Assert.assertEquals("test link", link.toString());
	}
	
	/**
	 * Tests the method {@link Link#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		Link link = new Link(getContext(), "test link", new RedirectResult("http://test.url"));
		Assert.assertEquals("test link".hashCode(), link.hashCode());
	}
	
	/**
	 * Tests the method {@link Link#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		Link link1 = new Link(getContext(), "test link", new RedirectResult("http://test.url"));
		Link link2 = new Link(getContext(), "test link", new RedirectResult("http://test.zzz"));
		Assert.assertFalse(link1.equals("test link"));
		Assert.assertTrue(link1.equals(link2));
	}
}