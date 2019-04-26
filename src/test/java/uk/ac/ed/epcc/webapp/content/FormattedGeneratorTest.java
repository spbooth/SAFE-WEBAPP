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
 * Unit tests of the class {@link FormattedGenerator}.
 * 
 * @author aheyrovs
 *
 */

public class FormattedGeneratorTest {

	/**
	 * Tests the method {@link FormattedGenerator#addContent(ContentBuilder)}.
	 */
	@Test
	public void testAddContent() {
		FormattedGenerator fg1 = new FormattedGenerator(2, "line1");
		HtmlBuilder builder = new HtmlBuilder();
		fg1.addContent(builder);
		Assert.assertEquals("<div class='para'>line1</div>", builder.toString());
		builder.clear();
		FormattedGenerator fg2 = new FormattedGenerator(2, "line1\nline2");
		fg2.addContent(builder);
		Assert.assertEquals("<p class='longlines'>\nline1<br/>\nline2<br/>\n</p>\n", builder.toString());
	}
}