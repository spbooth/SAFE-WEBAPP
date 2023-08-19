//| Copyright - The University of Edinburgh 2015                            |
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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
	
	@Test
	public void testConfigTags() throws Exception {
		Map<String,String> config = new HashMap<>();
		fac.addConfigTags(config);
		assertEquals("Dummy", config.get(Dummy1.MANDATORY));
		assertEquals("Dummy", config.get(Dummy1.NAME));
	}
}