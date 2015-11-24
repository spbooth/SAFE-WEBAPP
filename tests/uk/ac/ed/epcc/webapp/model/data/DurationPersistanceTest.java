// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
