// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
		Assert.assertEquals("<pre>line1\nline2</pre>", p.toString());
	}
}