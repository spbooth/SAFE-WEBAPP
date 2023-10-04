package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;

public class CSPUtilsTest extends WebappTestBase {

	public CSPUtilsTest() {
	}

	static final String policy = "default-src 'self';font-src 'self';media-src 'self' data:;style-src 'self';img-src 'self';script-src 'self';object-src 'none';frame-src 'self';frame-ancestors 'self';form-action 'self';report-uri {base-url}/ClientLogs";
	
	@Test
	public void testParse() {
		
			CSPUtils utils = new CSPUtils();
		utils.parse(policy);
		assertEquals(policy, utils.toString());
	}
	
	@Test
	public void testEdit() {
		
			CSPUtils utils = new CSPUtils();
		utils.parse(policy);
		utils.setClause("script-src", "'none'");
		assertEquals("default-src 'self';font-src 'self';media-src 'self' data:;style-src 'self';img-src 'self';script-src 'none';object-src 'none';frame-src 'self';frame-ancestors 'self';form-action 'self';report-uri {base-url}/ClientLogs", utils.toString());
	}
}
