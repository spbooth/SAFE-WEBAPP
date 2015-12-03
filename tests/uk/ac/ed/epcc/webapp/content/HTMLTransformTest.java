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

/**
 * Unit tests of the class {@link HTMLTransform}.
 * 
 * @author aheyrovs
 *
 */

public class HTMLTransformTest {
	
	/**
	 * Tests the method {@link HTMLTransform#convert(Object)}.
	 */
	@Test
	public void testConvert1() {
		HTMLTransform<String, String> t = new HTMLTransform<String, String>();
		Assert.assertEquals(3.14, t.convert(3.14));
	}
	
	/**
	 * Tests the method {@link HTMLTransform#convert(Table, Object, Object, Object)}.
	 */
	@Test
	public void testConvert2() {
		HTMLTransform<String, String> t = new HTMLTransform<String, String>();
		Assert.assertEquals(12, t.convert(null, null, null, 12));
		HtmlSpaceGenerator g = (HtmlSpaceGenerator) t.convert(null, null, null, "test text");
		HtmlPrinter builder = new HtmlPrinter();
		g.addContent(builder);
		Assert.assertEquals("test&nbsp;text", builder.toString());
	}
}