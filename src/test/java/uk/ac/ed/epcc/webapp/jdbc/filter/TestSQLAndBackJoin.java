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

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.DummyReference;
import uk.ac.ed.epcc.webapp.model.DummyReferenceFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
/**
 * @author spb
 *
 */
public class TestSQLAndBackJoin extends WebappTestBase {
	Dummy1.Factory fac;
	DummyReferenceFactory ref;
	Dummy1 fred, bill,simon;
	DummyReference fred_ref, bill_ref2, bill_ref, simon_ref;
	
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
		
		bill_ref2 = ref.makeBDO();
		bill_ref2.setReference(bill);
		bill_ref2.setName("RefBill2");
		bill_ref2.commit();
		
		simon = fac.makeBDO();
		simon.setName("simon");
		simon.setNumber(3);
		simon.commit();
		
		simon_ref = ref.makeBDO();
		simon_ref.setReference(simon);
		simon_ref.setName("RefSimon");
		simon_ref.commit();
	}
	
	@Test
	public void testAndBackJoins() throws DataException {
		// A destination filter selects any record pointed to be by any record that matches
		// the original filter
		// An and combination should still work if different source records match different branches
		// provided they all point to the same desitiona
		AndFilter<Dummy1> fil = new AndFilter(fac.getTag());
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefBill"));
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefBill2"));
		
		assertEquals(1L,fac.getCount(fil));
		assertFalse(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		
		
	}
	@Test
	public void testAndBackJoins2() throws DataException {
		// A destination filter selects any record pointed to be by any record that matches
		// the original filter
		// An and combination should still work if different source records match different branches
		// provided they all point to the same desitiona
		AndFilter<Dummy1> fil = new AndFilter(fac.getTag());
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefBill"));
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefBill2"));
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefFred"));
		assertEquals(0L,fac.getCount(fil));
		assertFalse(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		
		
	}
}
