// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import static org.junit.Assert.*;

import org.junit.Test;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class TemplateFinderTest extends WebappTestBase {

	@Test
	public void testInclude() throws Exception{
		TemplateFinder finder = new TemplateFinder(getContext());
		String text = finder.getText("toplevel.txt");
		System.out.println(text);
		assertTrue(text.contains("This is the top\nThis is sub\nThis is also the top"));
	}
}
