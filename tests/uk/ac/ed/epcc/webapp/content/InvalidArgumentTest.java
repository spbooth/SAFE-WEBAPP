// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.content;

import org.junit.Test;

/**
 * Unit tests of the class {@link InvalidArgument}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class InvalidArgumentTest {

	/**
	 * Unit test of the constructor {@link InvalidArgument#InvalidArgument(String)}.
	 * 
	 * @throws InvalidArgument 
	 */
	@Test(expected = InvalidArgument.class)
	public void testInvalidArgument() throws InvalidArgument {
		throw new InvalidArgument("test exception");
	}
}
