// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.content;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link NestedXMLBuilder}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class NestedXMLBuilderTest {
	
	/**
	 * Tests the method {@link NestedXMLBuilder#addContent(SimpleXMLBuilder)}.
	 */
	@Test
	public void testAddContent() {
		// DOM -> SimpleXMLBuilder
		NestedXMLBuilder domBuilder = null;
		try {
			domBuilder = new NestedXMLBuilder();
		} catch (ParserConfigurationException e) {
			Assert.fail("Exception while creating NestedXMLBuilder.");
		}
		Assert.assertNotNull(domBuilder);
		// constructs DOM document
		domBuilder.open("tag1", new String[][]{{"attr1_name", "attr1_value"}});
		domBuilder.clean("test text");
		domBuilder.close();
		SimpleXMLBuilder nested = domBuilder.getNested();
		Assert.assertEquals(domBuilder, nested.getParent());
		nested.open("tag2");
		nested.attr("attr2_name", "attr2_value");
		nested.clean('z');
		nested.clean(123);
		nested.close();
		nested.appendParent();
		// converts DOM to SimpleXMLBuilder
		XMLPrinter printer = new XMLPrinter();
		domBuilder.addContent(printer);
		Assert.assertEquals(
				"<tag1 attr1_name='attr1_value'>test text</tag1>" +
				"<tag2 attr2_name='attr2_value'>z123</tag2>",
				printer.toString());
	}
}
