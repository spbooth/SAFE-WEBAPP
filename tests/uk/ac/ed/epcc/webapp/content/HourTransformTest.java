//| Copyright - The University of Edinburgh 2011                            |
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