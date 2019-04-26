//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import org.junit.Test;

import org.junit.Assert;

/**
 * Unit tests of the class {@link HtmlPrinter}.
 * 
 * @author aheyrovs
 *
 */

public class HtmlPrinterTest {
	
	/**
	 * Tests the method {@link HtmlPrinter#longLines(int, String)}.
	 */
	@Test
	public void testLongLines() {
		HtmlPrinter p = new HtmlPrinter();
		Assert.assertTrue(p.longLines(4, "line1\nline2"));
		Assert.assertEquals("<p class='longlines'>\nline1<br/>\nline2<br/>\n</p>\n", p.toString());
	}
	
	/**
	 * Tests the method {@link HtmlPrinter#cleanFormatted(int, String)}.
	 */
	@Test
	public void testCleanFormatted() {
		HtmlPrinter p = new HtmlPrinter();
		Assert.assertTrue(p.cleanFormatted(4, "line1\nline2"));
		Assert.assertEquals("<p class='longlines'>\nline1<br/>\nline2<br/>\n</p>\n", p.toString());
		p.clear();
		Assert.assertFalse(p.cleanFormatted(10, "line1\nline2"));
		Assert.assertEquals("<pre class='loose'>line1\nline2</pre>", p.toString());
	}
}