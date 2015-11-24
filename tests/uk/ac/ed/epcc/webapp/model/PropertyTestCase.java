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
