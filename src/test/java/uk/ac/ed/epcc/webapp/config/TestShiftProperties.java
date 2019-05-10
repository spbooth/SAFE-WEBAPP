//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

/**
 * @author Stephen Booth
 *
 */
public class TestShiftProperties extends uk.ac.ed.epcc.webapp.WebappTestBase {

	
	
	@Test
	public void testShift() throws IOException {
		Properties prop = ctx.getService(ConfigService.class).getServiceProperties();
		String a = prop.getProperty("shift.myprop");
		assertNotNull(a);
		assertEquals("prop", a);
		assertNull(prop.getProperty("myprop"));
		
		String b = prop.getProperty("shift.nested.myprop");
		assertNotNull(b);
		assertEquals("nested", b);
		assertNull(prop.getProperty("nested.myprop"));
	}
}
