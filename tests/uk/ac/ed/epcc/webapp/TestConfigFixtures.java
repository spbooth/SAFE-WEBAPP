// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
