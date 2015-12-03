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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public class PropertyTestCase extends DataObjectFactoryTestCase<PropertyFactory, Property> {

	

	@Override
	public PropertyFactory getFactory() {
		return new PropertyFactory(ctx);
	}

	@Test
	public void testSet() throws DataFault, DataException{
		PropertyFactory fac = getFactory();
		
		fac.setProperty("junk.value", "frog");
		Property val = fac.findByName("junk.value");
		
		assertEquals(val.getValue(), "frog");
		assertEquals(val.getName(), "junk.value");
		
	}
	
	@Test
	public void testNullProp() throws DataException{
        PropertyFactory fac = getFactory();

		fac.setProperty("junk.value2", null);
		Property val = fac.findByName("junk.value2");
		
		assertEquals(val, null);
		
	}
	
}