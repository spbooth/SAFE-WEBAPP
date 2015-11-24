/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

public class DummyTest extends WebappTestBase {
    
	Dummy1.Factory fac;
	
	
	
	@Before
	public void setUp() {
		fac = new Dummy1.Factory(ctx);
	}

	@Test
	public void testSQLInsert() throws DataException{
		Dummy1 t = new Dummy1(ctx);
		t.setName("Test");
		t.setNumber(16);
		t.commit();
		try{
		int id = t.getID();
		String test_vector[] = {
				"Hello world\n",
			    "`",
			    "\"",
			    "\\",
			    null,
			    "\0"
		};
		for(int i=0 ;i<test_vector.length;i++){
			t.setName(test_vector[i]);
			t.setNumber(i);
			t.setUnsigned(i);
			t.commit();
			Dummy1 res = fac.find(id);
			assertEquals(test_vector[i],res.getName());
			assertEquals("number wrong",res.getNumber(),i);
			assertEquals("unsigned wrong",res.getUnsigned(),(long)i);
			System.out.println("Test "+i+" "+test_vector[i]+" "+res.getName());
			
		}
		}finally{
			t.delete();
		}
	}
}
