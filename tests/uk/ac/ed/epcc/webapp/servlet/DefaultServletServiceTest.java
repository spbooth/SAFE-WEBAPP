// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.servlet;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class DefaultServletServiceTest extends TestCase {

	
	@Test
	public void testDecode(){
		// example from RFC
		assertEquals("Aladdin:open sesame", DefaultServletService.decode("QWxhZGRpbjpvcGVuIHNlc2FtZQ=="));
		
		assertFalse("Aladdin:open parsnip".equals(DefaultServletService.decode("QWxhZGRpbjpvcGVuIHNlc2FtZQ==")));
	}

}
