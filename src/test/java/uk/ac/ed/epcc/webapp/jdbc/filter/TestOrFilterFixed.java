//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.Dummy1;
/**
 * @author spb
 *
 */
public class TestOrFilterFixed extends WebappTestBase {
	

	@Test
	public void testAddFixed() throws NoSQLFilterException{
		Dummy1.Factory fac = new Dummy1.Factory(getContext());
		OrFilter<Dummy1> fil = fac.getOrFilter();
		
		assertFalse(fil.getBooleanResult());
		fil.addFilter(new FalseFilter<>());
		assertFalse(fil.getBooleanResult());
		fil.addFilter(new GenericBinaryFilter<>(true));
		assertTrue(fil.getBooleanResult());
		assertFalse(fil.nonSQL());
		assertEquals(fil.getSQLFilter(), new GenericBinaryFilter<>(true));
		
	}
	
	
	
}
