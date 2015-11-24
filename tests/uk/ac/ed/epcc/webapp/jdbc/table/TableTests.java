// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.table;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class TableTests extends WebappTestBase {

	
	
	@Test
	public void testAutoCreate(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		
		assertTrue(fac.isValid());
		assertTrue(fac.hasField("Name"));
		assertFalse(fac.hasField("SecretIdentity"));
		
	}
	
	@Test
	public void testAddStdField(){
		TableTransitionProvider prov = new TableTransitionProvider(ctx);
		
	}

}
