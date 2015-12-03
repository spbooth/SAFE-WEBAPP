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
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DurationDataObjectFactory.DurationObject;
/**
 * @author spb
 *
 */

public class DurationPersistanceTest extends WebappTestBase {

	/**
	 * 
	 */
	public DurationPersistanceTest() {

	}

	
	@Test
	public void testPersistance() throws DataException{
		DurationDataObjectFactory fac = new DurationDataObjectFactory(getContext());
		
		Duration orig = new Duration(5); // 5 seconds;
		DurationObject object = fac.makeBDO();
		
		object.setDuration(orig);
		object.commit();
		
		DurationObject object2 = fac.find(object.getID());
		
		Duration ret = object2.getDuration();
		
		assertEquals(orig, ret);
		assertEquals(ret.getSeconds(), 5);
		
		
		
		
	}
	
}