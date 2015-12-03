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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.Dummy2;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;



public class TestSQLAndFilter extends WebappTestBase {
	
	
	Dummy1.Factory d1_fac;
	Dummy2.Factory d2_fac;
	
	@Before
	public void setUp()  {
	    d1_fac = new Dummy1.Factory(ctx);
	    d2_fac = new Dummy2.Factory(ctx);
	    try{
	    d1_fac.nuke();
		d2_fac.nuke();
	    }catch(Exception e){
	    	ctx.error(e, "Error nuking table");
	    }
	}
	
	@After
	public void tearDown() throws Exception {
		d1_fac.nuke();
		d2_fac.nuke();
	}

	@Test
	public void testGetNumber() throws DataException{
		
		for(int i=0 ;i < 4 ; i ++){
			Dummy1 t1 = new Dummy1(ctx);
			t1.setNumber(i);
			t1.setName("fred");
			t1.commit();
			Dummy1 t2 = new Dummy1(ctx);
			t2.setNumber(i);
			t2.setName("bill");
			t2.commit();
		}
		// check null
		assertEquals(8, d1_fac.count(null));
		assertEquals(8, inter_count(null,null,null));
		// check base filter
		assertEquals(4, d1_fac.count(d1_fac.new StringFilter("fred")));
		assertEquals(4, inter_count(d1_fac.new StringFilter("fred"),null,"fred"));
		assertEquals(4, d1_fac.count(d1_fac.new StringFilter("bill")));
		assertEquals(4, inter_count(d1_fac.new StringFilter("bill"),null,"bill"));
		assertEquals(2, d1_fac.count(d1_fac.new NumberFilter(1.0)));
		assertEquals(2, inter_count(d1_fac.new NumberFilter(1.0),1.0,null));
		SQLAndFilter<Dummy1> fil = new SQLAndFilter<Dummy1>(d1_fac.getTarget());
		assertEquals(8, d1_fac.count(fil));
		assertEquals(8, inter_count(fil,null,null));
		fil = new SQLAndFilter<Dummy1>(d1_fac.getTarget());
		fil.addFilter(d1_fac.new StringFilter("fred"));
		assertEquals(4,d1_fac.count(fil));
		assertEquals(4, inter_count(fil, null, "fred"));
		fil.addFilter(d1_fac.new NumberFilter(1.0));
		assertEquals(1, d1_fac.count(fil));
		assertEquals(1, inter_count(fil, 1.0, "fred"));
		// now try with AndFilter
		AndFilter<Dummy1> fil2= new AndFilter<Dummy1>(d1_fac.getTarget());
		assertEquals(8, inter_count(fil2,null,null));
		fil2= new AndFilter<Dummy1>(d1_fac.getTarget());
		fil2.addFilter(d1_fac.new StringFilter("fred"));
		assertEquals(4, inter_count(fil2, null, "fred"));
		fil2.addFilter(d1_fac.new NumberFilter(1.0));
		assertEquals(1, inter_count(fil2, 1.0, "fred"));
		
		fil2= new AndFilter<Dummy1>(d1_fac.getTarget());
		fil2.addFilter(d1_fac.new StringFilter("fred"));
		assertEquals(4, inter_count(fil2, null, "fred"));
		fil2.addFilter(d1_fac.new NumberAcceptFilter(1.0));
		assertEquals(1, inter_count(fil2, 1.0, "fred"));
		fil2.addFilter(d1_fac.new NumberAcceptFilter(0.0));
		assertEquals(0, inter_count(fil2, null, null));
	}
	
	private int inter_count(BaseFilter<Dummy1> f, Number n, String s) throws DataFault{
		int count=0;
		for(Iterator<Dummy1> it = d1_fac.new FilterIterator(f); it.hasNext();){
			Dummy1 v = it.next();
			if( n != null ){
				assertEquals(n.intValue(), v.getNumber());
			}
			if( s != null ){
				assertEquals(s, v.getName());
			}
			count++;
		}
		return count;
	}
}