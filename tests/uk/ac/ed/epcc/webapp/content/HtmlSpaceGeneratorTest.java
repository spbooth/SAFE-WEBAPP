// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
