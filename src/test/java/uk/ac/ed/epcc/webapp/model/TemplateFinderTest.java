//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import static org.junit.Assert.*;

import org.junit.Test;
/**
 * @author spb
 *
 */

public class TemplateFinderTest extends WebappTestBase {

	@Test
	public void testInclude() throws Exception{
		TemplateFinder finder = TemplateFinder.getTemplateFinder(getContext());
		String text = finder.getText("toplevel.txt");
		System.out.println(text);
		text = text.replace("\r", ""); // running tests on windows ?
		assertTrue(text.contains("This is the top\nThis is sub\nThis is also the top"));
	}
}