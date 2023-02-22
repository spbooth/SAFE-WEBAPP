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

import static org.junit.Assert.assertEquals;

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
		HTMLTransform t = new HTMLTransform();
		assertEquals(3.14, t.convert(3.14));
	}
	
	
	@Test
	public void testConvert2() {
		HTMLTransform t = new HTMLTransform();
		assertEquals(12, t.convert( 12));
		HtmlSpaceGenerator g = (HtmlSpaceGenerator) t.convert( "test text");
		HtmlPrinter builder = new HtmlPrinter();
		g.addContent(builder);
		assertEquals("test&nbsp;text", builder.toString());
	}
}