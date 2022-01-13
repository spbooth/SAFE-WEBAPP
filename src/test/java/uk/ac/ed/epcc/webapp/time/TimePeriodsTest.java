// Copyright - The University of Edinburgh 2015
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.time;

import java.util.Date;

import junit.framework.TestCase;

public class TimePeriodsTest extends TestCase {

	public void testTimePeriods() {
		Date a = new Date(1000000);
		Date b = new Date(2000000);
		Date c = new Date(3000000);
		Date d = new Date(4000000);
		TimePeriods p1 = new TimePeriods(a,b);
		TimePeriods p2 = new TimePeriods(c,d);
		TimePeriods p3 = new TimePeriods(b,c);
		assertEquals(1000,p1.getSeconds());
		assertEquals(1,p1.getCount());
		p1.add(p2);
		assertEquals(2000,p1.getSeconds());
		assertEquals(2,p1.getCount());
		p1.subtract(p3);
		assertEquals(2000,p1.getSeconds());
		assertEquals(2,p1.getCount());
		p1.add(p3);
		assertEquals(3000,p1.getSeconds());
		assertEquals(1,p1.getCount());
		p1.subtract(p3);
		assertEquals(2000,p1.getSeconds());
		assertEquals(2,p1.getCount());
		TimePeriods p4 = new TimePeriods(new Date(1500000),new Date(3500000));
		p1.subtract(p4);
		assertEquals(1000,p1.getSeconds());
		assertEquals(2,p1.getCount());
		p1.subtract(p1);
		assertEquals(0,p1.getSeconds());
		assertEquals(0,p1.getCount());
		p3.subtract(p4);
		assertEquals(0,p3.getSeconds());
		assertEquals(0,p3.getCount());
		
	}

	

}