//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.DummyReferenceFactory;

/**
 * @author Stephen Booth
 *
 */
public class FilterConverterTest extends WebappTestBase {

	public Dummy1.Factory fac;
	public FilterConverter conv;
	public DummyReferenceFactory ref;
	@Before
	public void setup() {
		fac = new Dummy1.Factory(ctx);
		conv = new FilterConverter<>();
		ref= new DummyReferenceFactory(ctx);
	}
	
	@Test
	public void testPatterFilter() throws Exception {
		PatternFilter fil  = fac.getStringFilter("bill"); 
		assertEquals(fil, fil.acceptVisitor(conv));
	}
	
	@Test
	public void testAcceptFilter() throws Exception {
		try {
		AcceptFilter fil  = fac.getNumberAcceptFilter(3); 
		fil.acceptVisitor(conv);
		assertFalse("Expecting exception",true);
		}catch(NoSQLFilterException e) {
			
		}
	}
	@Test
	public void testDualFilter() throws Exception {
		BaseFilter fil  = fac.getNumberFilter(3); 
		DualFilter<Dummy1> actual = new DualFilter<>(fac.getNumberFilter(3), fac.getNumberAcceptFilter(3));
		assertEquals(fil, actual.acceptVisitor(conv));
	}
	
	@Test
	public void testAndFilter() throws Exception {
		AndFilter fil = new AndFilter<>(fac.getTag());
		fil.addFilter(fac.getStringFilter("bill"));
		fil.addFilter(fac.getNumberFilter(2));
		SQLAndFilter and = new SQLAndFilter<>(fac.getTag());
		and.addFilter(fac.getStringFilter("bill"));
		and.addFilter(fac.getNumberFilter(2));
		assertEquals(and, fil.acceptVisitor(conv));
		assertEquals(and, fil.acceptVisitor(conv));
	}
	@Test
	public void testOrFilter() throws Exception {
		OrFilter fil = new OrFilter<>(fac.getTag(),fac);
		fil.addFilter(fac.getStringFilter("bill"));
		fil.addFilter(fac.getNumberFilter(2));
		SQLOrFilter or = new SQLOrFilter<>(fac.getTag());
		or.addFilter(fac.getStringFilter("bill"));
		or.addFilter(fac.getNumberFilter(2));
		assertEquals(or, fil.acceptVisitor(conv));
		
		assertEquals(or, fil.acceptVisitor(conv));
	}
	
	@Test
	public void testBinaryFilter() throws Exception {
		BinaryFilter fil = new GenericBinaryFilter<>( false);
		
		assertEquals(fil,fil.acceptVisitor(conv));
	}
	
	@Test
	public void testBinaryAcceptFilter() throws Exception {
		BinaryFilter fil = new GenericBinaryFilter<>( false);
		BinaryAcceptFilter a = new BinaryAcceptFilter<>(fil);
		assertEquals(fil,a.acceptVisitor(conv));
	}
	
	@Test
	public void testRemoteFilter() throws Exception {
		BaseFilter fil= ref.getRemoteNameFilter("bill");
		assertEquals(fil,fil.acceptVisitor(conv));
	}
	
	@Test
	public void testRemoteAcceptFilter() throws Exception {
		try {
		BaseFilter fil= ref.getRemoteNumberAcceptFilter(3);
		assertEquals(fil,fil.acceptVisitor(conv));
		assertFalse("Expecting exception",true);
		}catch(NoSQLFilterException e) {
			
		}
	}
}
