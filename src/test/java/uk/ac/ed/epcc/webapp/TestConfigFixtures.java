//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;

/**
 * @author spb
 *
 */

public class TestConfigFixtures extends WebappTestBase {

	/**
	 * 
	 */
	public TestConfigFixtures() {
		// TODO Auto-generated constructor stub
	}

	@ConfigFixtures("additional.properties")
	@Test
	public void testAdditional(){
		Assert.assertEquals("first", ctx.getInitParameter("additional"));
	}
	
	@Test
	public void testNoAdditional(){
		Assert.assertEquals("Unknown", ctx.getInitParameter("additional","Unknown"));
	}
	
	@ConfigFixtures("/toplevel.properties")
	@Test
	public void testToplevel(){
		Assert.assertEquals("top", ctx.getInitParameter("toplevel"));
	}
}