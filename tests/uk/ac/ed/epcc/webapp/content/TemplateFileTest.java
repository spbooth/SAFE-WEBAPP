// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.content;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link TemplateFile}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class TemplateFileTest {

	/**
	 * Unit test of the class {@link TemplateFile}.
	 */
	@Test
	public void testTemplateFile() {
		try {
			File f = new File("test_templates/test_template.txt");
			TemplateFile tf = TemplateFile.getTemplateFile(f.getAbsolutePath()); // Load the page template
			tf.setProperty("test_text1", "abc"); // Apply the property
			tf.setRegionEnabled("additional_info_region", true);
			tf.setProperty("additional_info", "zzz"); // Apply the property
			Assert.assertEquals(
					"Test text1: abc" + "\n" +
					"Test text2: default text" + "\n" +
					"\n" +
					"Additional info: zzz" +
					"\n",
					tf.toString().replace("\r", ""));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
}
