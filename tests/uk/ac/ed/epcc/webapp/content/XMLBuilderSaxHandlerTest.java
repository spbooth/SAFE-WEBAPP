// Copyright - The University of Edinburgh 2012
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
