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

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import uk.ac.ed.epcc.webapp.WebappTestBase;

import org.junit.Test;

/**
 * Unit tests of the class {@link TemplateFile}.
 * 
 * @author aheyrovs
 *
 */

public class TemplateFileTest extends WebappTestBase{

	/**
	 * Unit test of the class {@link TemplateFile}.
	 */
	@Test
	public void testTemplateFile() {
		try {
			TemplateFile tf = TemplateFile.getFromString(getResourceAsString("/test_templates/test_template.txt")); // Load the page template
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