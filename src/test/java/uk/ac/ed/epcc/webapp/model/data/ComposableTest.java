//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.Dummy2;

/**
 * @author Stephen Booth
 *
 */
public class ComposableTest extends WebappTestBase {

	/**
	 * 
	 */
	public ComposableTest() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Test
	public void testConstruct() {
		
		TestComposable test = ctx.makeObject(TestComposable.class,"Test");
		assertNotNull("Direct interface", test);
		assertTrue( test instanceof Dummy1.Factory);
		
		TestComposable test2 = ctx.makeObject(TestComposable.class,"Test2");
		assertNotNull("Config composite", test2);
		assertTrue(test2 instanceof TestComposableComposite);
		
		TestComposable test3 = ctx.makeObject(TestComposable.class, "Test3");
		assertNull("No implementation",test3);
		
	}
	
	@Test
	public void testClassMap() {
		Map<String,Class> m = ctx.getClassMap(TestComposable.class);
		
		assertEquals(2, m.size());
		
		assertEquals(Dummy1.Factory.class, m.get("Test"));
		assertEquals(Dummy2.Factory.class, m.get("Test2"));
	}

}
