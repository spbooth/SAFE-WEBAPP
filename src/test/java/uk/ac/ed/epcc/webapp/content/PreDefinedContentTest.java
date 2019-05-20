//| Copyright - The University of Edinburgh 2018                            |
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

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;

/**
 * @author Stephen Booth
 *
 */
public class PreDefinedContentTest extends WebappTestBase {

	@Test
	public void testText() {
		PreDefinedContent content = new PreDefinedContent(ctx, "testcontent", "formatted.content", "text");
		
		assertEquals("This is some bold <b>text</b> content", content.toString());
		
		HtmlBuilder hb = new HtmlBuilder();
		hb.addObject(content);
		assertEquals("This is some bold <b>text</b> content", hb.toString());
	}
	
	@Test
	public void testEscape() {
		PreDefinedContent content = new PreDefinedContent(ctx, "testcontent", "formatted.content", "<i>italic</i>");
		
		assertEquals("This is some bold <b><i>italic</i></b> content", content.toString());
		
		HtmlBuilder hb = new HtmlBuilder();
		hb.addObject(content);
		assertEquals("This is some bold <b>&lt;i&gt;italic&lt;/i&gt;</b> content", hb.toString());
	}
	
	
	@Test
	public void testNumber() {
		PreDefinedContent content = new PreDefinedContent(ctx, "testcontent", "numeric.content", 7.2, 0.82);
		
		String expected = "This is a 7 integer, This is a 82% percentage";
		assertEquals(expected, content.toString());
		
		HtmlBuilder hb = new HtmlBuilder();
		hb.addObject(content);
		assertEquals(expected, hb.toString());
	}
	
	@Test
	public void testCreateMessage() {
		ResourceBundle mess = getContext().getService(MessageBundleService.class).getBundle();
		PreDefinedContent content = new PreDefinedContent(ctx,mess,"object_created.text",new Object[] {"Object", "Hello" });
		
		String expected = "Hello created";
		assertEquals(expected, content.toString());
		
		HtmlBuilder hb = new HtmlBuilder();
		hb.addObject(content);
		assertEquals(expected, hb.toString());
	}
}
