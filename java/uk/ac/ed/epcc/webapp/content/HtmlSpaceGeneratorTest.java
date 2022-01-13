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

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link HtmlSpaceGenerator}. 
 * 
 * @author aheyrovs
 *
 */

public class HtmlSpaceGeneratorTest {

	/**
	 * Tests the method {@link HtmlSpaceGenerator#addContent(SimpleXMLBuilder)}.
	 */
	@Test
	public void testAddContent() {
		HtmlSpaceGenerator sg = new HtmlSpaceGenerator("test text");
		Assert.assertEquals("test text", sg.toString());
		Assert.assertEquals("test text".hashCode(), sg.hashCode());
		HtmlPrinter p = new HtmlPrinter();
		sg.addContent(p);
		Assert.assertEquals("test&nbsp;text", p.toString());
	}
}