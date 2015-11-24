// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.content;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link HTMLTransform}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
