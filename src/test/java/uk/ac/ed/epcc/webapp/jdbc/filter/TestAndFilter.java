//| Copyright - The University of Edinburgh 2016                            |
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.DummyReference;
import uk.ac.ed.epcc.webapp.model.DummyReferenceFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/**
 * @author spb
 *
 */
public class TestAndFilter extends WebappTestBase {
	Dummy1.Factory fac;
	DummyReferenceFactory ref;
	Dummy1 fred, bill,simon;
	DummyReference fred_ref, bill_ref, simon_ref;
	
	@Before
	public void setup() throws DataFault {
		fac = new Dummy1.Factory(getContext());
		ref=new DummyReferenceFactory(ctx);
		fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		
		fred_ref = ref.makeBDO();
		fred_ref.setReference(fred);
		fred_ref.setName("RefFred");
		fred_ref.commit();
		
		bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		bill_ref = ref.makeBDO();
		bill_ref.setReference(bill);
		bill_ref.setName("RefBill");
		bill_ref.commit();
		
		simon = fac.makeBDO();
		simon.setName("simon");
		simon.setNumber(3);
		simon.commit();
		
		simon_ref = ref.makeBDO();
		simon_ref.setReference(simon);
		simon_ref.setName("RefSimon");
		simon_ref.commit();
	}
	
	
	
	private <X> void checkStd(AndFilter<X> fil, String expected) {
		assertEquals(expected, fil.toString());
		AndFilter<X> dup = new AndFilter<>(fil.getTarget());
		dup.addFilter(fil);
	
		
		assertTrue(dup.equals(fil) && fil.equals(dup));	
		assertEquals(fil.hashCode(), dup.hashCode());
	}
	
	@Test 
	public void testEmptyAnd() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		
		assertEquals(3, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertTrue(fac.matches(fil, simon));
		assertFalse(fil.isForced());
		assertTrue(fil.isEmpty());
		checkStd(fil, "AndFilter( force=true)");
	}
	@Test 
	public void testPattern() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		fil.addFilter(fac.getStringFilter("fred"));
		fil.addFilter(fac.getNumberFilter(1));
		assertFalse(fil.hasAcceptFilters());
		assertTrue(fil.hasPatternFilters());
		assertFalse(fil.isEmpty());
		assertFalse(fil.isForced());
		assertEquals(1, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "AndFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 1)] force=true)");
	}
	@Test 
	public void testAccept() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		fil.addFilter(fac.getStringFilter("fred"));
		fil.addFilter(fac.getNumberAcceptFilter(1));
		assertTrue(fil.hasAcceptFilters());
		assertTrue(fil.hasPatternFilters());
		assertFalse(fil.isEmpty());
		assertFalse(fil.isForced());
		assertNull(fil.getAcceptFilter(null));
		assertEquals(1, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "AndFilter( accepts=[NumberAcceptFilter(1)] filters=[SQLValueFilter(Test.Name= fred)] force=true)");
	}
	@Test 
	public void testAccept2() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		fil.addFilter(fac.getStringFilter("fred"));
		fil.addFilter(fac.getNumberAcceptFilter(2));
		assertFalse(fil.isForced());
		assertFalse(fil.isEmpty());
		assertEquals(0, fac.getCount(fil));
		assertFalse(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "AndFilter( accepts=[NumberAcceptFilter(2)] filters=[SQLValueFilter(Test.Name= fred)] force=true)");
	}
	@Test 
	public void testAccept3() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		fil.addFilter(fac.getNumberAcceptFilter(1));
		assertTrue(fil.hasAcceptFilters());
		assertFalse(fil.hasPatternFilters());
		assertFalse(fil.isEmpty());
		assertFalse(fil.isForced());
		assertEquals(fac.getNumberAcceptFilter(1), fil.getAcceptFilter(null));
		assertEquals(1, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "AndFilter( accepts=[NumberAcceptFilter(1)] force=true)");
	}
	
	@Test 
	public void testAccept4() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		fil.addFilter(fac.getStringAcceptFilter("fred"));
		fil.addFilter(fac.getNumberAcceptFilter(1));
		assertTrue(fil.hasAcceptFilters());
		assertFalse(fil.hasPatternFilters());
		assertFalse(fil.isEmpty());
		assertFalse(fil.isForced());
		//assertNull(fil.getAcceptFilter(null));
		assertEquals(1, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "AndFilter( accepts=[StringAcceptFilter(fred), NumberAcceptFilter(1)] force=true)");
	}
	@Test 
	public void testDual() throws DataException {
		AndFilter<Dummy1> fil = new AndFilter<>(fac.getTarget());
		fil.addFilter(fac.getStringFilter("fred"));
		fil.addFilter(new DualFilter<>(fac.getNumberFilter(1),fac.getNumberAcceptFilter(1)));
		assertFalse(fil.hasAcceptFilters()); // promotes to SQL
		assertTrue(fil.hasPatternFilters());
		assertEquals(1, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "AndFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 1)] force=true)");
	}
}
