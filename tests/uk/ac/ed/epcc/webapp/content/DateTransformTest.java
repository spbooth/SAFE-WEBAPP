// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")

/**
 * Unit tests of the class {DateTransform@link}.
 * 
 * @author aheyrovs
 *
 */
public class DateTransformTest {

	/**
	 * Unit test of the method {@link DateTransform#convert(Object)}.
	 */
	@Test
	public void testConvert() {
		DateTransform dt = new DateTransform(new SimpleDateFormat("yyyy.MM.dd"));
		Assert.assertEquals("test string", dt.convert("test string"));
		Assert.assertEquals("1970.01.01", dt.convert(new Date(0)));
		Assert.assertNull(dt.convert(null));
	}

}