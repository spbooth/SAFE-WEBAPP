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
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Unit tests of the class {@link XMLBuilderSaxHandler}.
 * 
 * @author aheyrovs
 *
 */

public class XMLBuilderSaxHandlerTest {

	/**
	 * Unit test of the class {@link XMLBuilderSaxHandler}.
	 */
	@Test
	public void testXMLBuilderSaxHandler() {
		XMLPrinter printer = new XMLPrinter();
		XMLBuilderSaxHandler saxHandler = new XMLBuilderSaxHandler(printer);
		AttributesImpl attrs = new AttributesImpl();
		attrs.addAttribute(null, "attr1_name", null, null, "attr1_value");
		try {
			saxHandler.startElement(null, null, "tag1", attrs);
			saxHandler.characters("zzz test text".toCharArray(), 4, 13);
			saxHandler.endElement(null, null, null);
		} catch (SAXException e) {
			Assert.fail("SAXException.");
		}
		Assert.assertEquals("<tag1 attr1_name='attr1_value'>test text</tag1>", printer.toString());
	}
}