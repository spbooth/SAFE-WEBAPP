// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests of the class {@link HourTransform}.
 * 
 * @author aheyrovs
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class HourTransformTest {

	/**
	 * Tests the method {@link HourTransform#convert(Object)}.
	 */
	@Test
	public void testConvert() {
		HourTransform ht = new HourTransform();
		// tests the default value
		Assert.assertEquals("0:00:00", ht.convert(null));
		// tests a string value
		Assert.assertEquals("test_string", ht.convert("test_string"));
		// tests numeric values
		Assert.assertEquals("1:00:00", ht.convert(3600));
		Assert.assertEquals("1:10:10", ht.convert(4210));
		Assert.assertEquals("-1:00:00", ht.convert(-3600));
	}
}