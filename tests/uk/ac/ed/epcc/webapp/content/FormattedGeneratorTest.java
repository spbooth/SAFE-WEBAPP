// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.content;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link FormattedGenerator}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
