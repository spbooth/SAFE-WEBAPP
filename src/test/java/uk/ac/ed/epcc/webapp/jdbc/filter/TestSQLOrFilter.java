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
public class TestSQLOrFilter extends WebappTestBase {
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
	public void testPattern() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		fil.addFilter(fac.new StringFilter("fred"));
		fil.addFilter(fac.new NumberFilter(2));
		assertEquals(2, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 2)] force=false)");
	}
	
	@Test
	public void testPatternJoin() throws DataException {
		SQLOrFilter<DummyReference> fil = ref.getSQLOrFilter();
		
		fil.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNameFilter("fred"));
		fil.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNumberFilter(2));
		assertEquals(3, ref.getCount(fil));
		assertTrue(ref.matches(fil, fred_ref));
		assertTrue(ref.matches(fil, bill_ref));
		assertTrue(ref.matches(fil, bill_ref2));
		assertFalse(ref.matches(fil, simon_ref));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 2)] join=[JoinerFilter(DummyReference.Reference=Test.`TestRecordID`)] force=false)");
	}
	
	@Test
	public void testBackJoin() throws DataException{
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		BaseFilter<Dummy1> freddest = ref.getDestFilter("RefFred");
		assertTrue(fac.matches(freddest, fred));
		fil.addFilter((SQLFilter<? super Dummy1>) freddest);
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefBill"));
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("Wombat"));
		assertEquals(2, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		//checkStd(or, "SQLOrFilter( filters=[BackJoinFilter(JoinerFilter(DummyReference.Reference=Test.`TestRecordID`) remote_filter=SQLValueFilter(DummyReference.Name= RefFred)), BackJoinFilter(JoinerFilter(DummyReference.Reference=Test.`TestRecordID`) remote_filter=SQLValueFilter(DummyReference.Name= RefBill)), BackJoinFilter(JoinerFilter(DummyReference.Reference=Test.`TestRecordID`) remote_filter=SQLValueFilter(DummyReference.Name= Wombat))] force=false)");
		checkStd(fil, "SQLOrFilter( force=false back_joins={JoinerFilter(DummyReference.Reference=Test.`TestRecordID`)=SQLOrFilter( filters=[SQLValueFilter(DummyReference.Name= RefFred), SQLValueFilter(DummyReference.Name= RefBill), SQLValueFilter(DummyReference.Name= Wombat)] force=false)})");

	}
	@Test
	public void testBackJoinMulti() throws DataException{
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		BaseFilter<Dummy1> freddest = ref.getDestFilter("RefBill");
		assertTrue(fac.matches(freddest, bill));
		fil.addFilter((SQLFilter<? super Dummy1>) freddest);
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("RefBill2"));
		fil.addFilter((SQLFilter<? super Dummy1>) ref.getDestFilter("Wombat"));
		assertEquals(1, fac.getCount(fil));
		assertFalse(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		//checkStd(or, "SQLOrFilter( filters=[BackJoinFilter(JoinerFilter(DummyReference.Reference=Test.`TestRecordID`) remote_filter=SQLValueFilter(DummyReference.Name= RefFred)), BackJoinFilter(JoinerFilter(DummyReference.Reference=Test.`TestRecordID`) remote_filter=SQLValueFilter(DummyReference.Name= RefBill)), BackJoinFilter(JoinerFilter(DummyReference.Reference=Test.`TestRecordID`) remote_filter=SQLValueFilter(DummyReference.Name= Wombat))] force=false)");
		checkStd(fil, "SQLOrFilter( force=false back_joins={JoinerFilter(DummyReference.Reference=Test.`TestRecordID`)=SQLOrFilter( filters=[SQLValueFilter(DummyReference.Name= RefBill), SQLValueFilter(DummyReference.Name= RefBill2), SQLValueFilter(DummyReference.Name= Wombat)] force=false)})");

	}
	
	@Test
	public void testFixedFalse() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		fil.addFilter(fac.new StringFilter("fred"));
		fil.addFilter(fac.new NumberFilter(2));
		// won't change result
		fil.addFilter(new GenericBinaryFilter<>(false));
		assertEquals(2, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 2)] force=false)");
	}
	@Test
	public void testFixedTrue() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		fil.addFilter(fac.new StringFilter("fred"));
		fil.addFilter(fac.new NumberFilter(2));
		fil.addFilter(new GenericBinaryFilter<>(true));
		assertEquals(3, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertTrue(fac.matches(fil, simon));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 2)] force=true)");
	}
	
	
	
	
	@Test
	public void testAndJoin() throws DataException {
		SQLOrFilter<DummyReference> fil = ref.getSQLOrFilter();
		
		fil.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNameFilter("fred"));
		fil.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNumberSQLAndFilter(2));
		assertEquals(3, ref.getCount(fil));
		assertTrue(ref.matches(fil, fred_ref));
		assertTrue(ref.matches(fil, bill_ref));
		assertTrue(ref.matches(fil, bill_ref2));
		assertFalse(ref.matches(fil, simon_ref));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Number= 2)] join=[JoinerFilter(DummyReference.Reference=Test.`TestRecordID`)] force=false)");
	}
	@Test
	public void testAnd() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		fil.addFilter(fac.new StringFilter("fred"));
		SQLAndFilter and = fac.getSQLAndFilter();
		and.addFilter(fac.new StringFilter("bill"));
		and.addFilter(fac.new NumberFilter(2));
		fil.addFilter(and);
		assertEquals(2, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLAndFilter( filters=[SQLValueFilter(Test.Name= bill), SQLValueFilter(Test.Number= 2)] force=true)] force=false)");
	}
	
	@Test
	public void testEmptyAnd() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		fil.addFilter(fac.new StringFilter("fred"));
		SQLAndFilter and = fac.getSQLAndFilter();
		
		fil.addFilter(and);
		assertEquals(3, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertTrue(fac.matches(fil, simon));
		
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred)] force=true)");
	}
	
	private <X> void checkStd(SQLOrFilter<X> fil, String expected) {
		assertEquals(expected, fil.toString());
		SQLOrFilter<X> dup = new SQLOrFilter<>(fil.getTag());
		dup.addFilter(fil);
		if( ! fil.isForced()) {
			// duplicates of forced filters won't have same string rep
			assertEquals(expected,dup.toString());
		}
		assertEquals(fil.hashCode(), dup.hashCode());
		assertTrue(dup.equals(fil) && fil.equals(dup));
		
		
		
		
	}
	
	
	
	
	
	@Test
	public void testNestedOr() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		
		fil.addFilter(fac.new StringFilter("fred"));
		SQLOrFilter or = fac.getSQLOrFilter();
		or.addFilter(fac.new StringFilter("bill"));
		or.addFilter(fac.new NumberFilter(2));
		or.addFilter(new GenericBinaryFilter<>(false));
		fil.addFilter(or);
		assertEquals(2, fac.getCount(fil));
		assertTrue(fac.matches(fil, fred));
		assertTrue(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		checkStd(fil, "SQLOrFilter( filters=[SQLValueFilter(Test.Name= fred), SQLValueFilter(Test.Name= bill), SQLValueFilter(Test.Number= 2)] force=false)");
	}
	@Test
	public void testEmptyOr() throws DataException {
		SQLOrFilter<Dummy1> fil = fac.getSQLOrFilter();
		assertEquals(0, fac.getCount(fil));
		assertFalse(fac.matches(fil, fred));
		assertFalse(fac.matches(fil, bill));
		assertFalse(fac.matches(fil, simon));
		
	}
}
