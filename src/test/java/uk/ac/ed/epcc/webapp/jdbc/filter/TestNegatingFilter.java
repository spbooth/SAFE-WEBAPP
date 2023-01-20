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

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.DummyReference;
import uk.ac.ed.epcc.webapp.model.DummyReferenceFactory;
/**
 * @author Stephen Booth
 *
 */
public class TestNegatingFilter extends WebappTestBase {
	Dummy1.Factory fac;
	DummyReferenceFactory ref;
	NegatingFilterVisitor<Dummy1> vis;
	NegatingFilterVisitor<DummyReference> ref_vis;
	@Before
	public void setup() {
		fac = new Dummy1.Factory(getContext());
		vis = new NegatingFilterVisitor<>(fac);
		ref=new DummyReferenceFactory(ctx);
		ref_vis = new NegatingFilterVisitor<>(ref);
	}
	
	@Test
	public void testBinaryFilter() throws Exception {
		
		
		GenericBinaryFilter orig = new GenericBinaryFilter<>(true);
		
		assertEquals(new GenericBinaryFilter<>(false), orig.acceptVisitor(vis));
		
	}
	
	@Test
	public void testBinaryFilter2() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		GenericBinaryFilter<Dummy1> orig = new GenericBinaryFilter<>( true);
		
		assertEquals(2, fac.getCount(orig));
		assertEquals(0, fac.getCount((BaseFilter<Dummy1>) orig.acceptVisitor(vis)));
	}
	
	@Test
	public void testBinaryAcceptFilter() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		BaseFilter<Dummy1> orig = new BinaryAcceptFilter<>(new GenericBinaryFilter<>(true));
		
		assertEquals(2, fac.getCount(orig));
		assertEquals(0, fac.getCount((BaseFilter<Dummy1>) orig.acceptVisitor(vis)));
	}
	@Test
	public void testPattern() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.commit();
		
		PatternFilter<Dummy1> fred_filter = fac.new StringFilter("fred");
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
		
	}
	
	@Test
	public void testAccept() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		AcceptFilter<Dummy1> fred_filter = fac.new NumberAcceptFilter(1);
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		BaseFilter<Dummy1> neg_filter = (BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis);
		Dummy1 bill2 = fac.find(neg_filter);
		assertEquals("bill",bill2.getName());
		
		assertEquals( (BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis),neg_filter);
		
		
	}
	@Test
	public void testDual() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		BaseFilter<Dummy1> fred_filter = new DualFilter<>(fac.new NumberFilter(1), fac.new NumberAcceptFilter(1));
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
		
	}
	
	@Test
	public void testSQLAnd() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		BaseFilter<Dummy1> fred_filter = new SQLAndFilter<>(fac.getTag(),fac.new StringFilter("fred"),fac.new NumberFilter(1));
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
		BaseFilter<Dummy1> no_filter = new SQLAndFilter<>(fac.getTag(),fac.new StringFilter("fred"),fac.new NumberFilter(2));
		
		assertEquals(0,fac.getCount(no_filter));
		
		BaseFilter<Dummy1> not_no = (BaseFilter<Dummy1>) no_filter.acceptVisitor(vis);
		assertEquals(2,fac.getCount(not_no));
	}
	
	@Test
	public void testAnd() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		BaseFilter<Dummy1> fred_filter = new AndFilter<>(fac.getTag(),fac.new StringFilter("fred"),fac.new NumberFilter(1));
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
		BaseFilter<Dummy1> no_filter = new SQLAndFilter<>(fac.getTag(),fac.new StringFilter("fred"),fac.new NumberFilter(2));
		
		assertEquals(0,fac.getCount(no_filter));
		
		BaseFilter<Dummy1> not_no = (BaseFilter<Dummy1>) no_filter.acceptVisitor(vis);
		assertEquals(2,fac.getCount(not_no));
	}
	
	@Test
	public void testAndMixed() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		BaseFilter<Dummy1> fred_filter = new AndFilter<>(fac.getTag(),fac.new StringFilter("fred"),fac.new NumberAcceptFilter(1));
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
		BaseFilter<Dummy1> no_filter = new SQLAndFilter<>(fac.getTag(),fac.new StringFilter("fred"),fac.new NumberFilter(2));
		
		assertEquals(0,fac.getCount(no_filter));
		
		BaseFilter<Dummy1> not_no = (BaseFilter<Dummy1>) no_filter.acceptVisitor(vis);
		assertEquals(2,fac.getCount(not_no));
	}
	
	@Test
	public void testOr() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		OrFilter<Dummy1> fred_filter = new OrFilter<>(fac.getTag(),fac);
		
		fred_filter.addFilter(fac.new StringFilter("fred"));
		fred_filter.addFilter(fac.new NumberFilter(1));
		fred_filter.addFilter(fac.new NumberFilter(19));
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
	}
	@Test
	public void testOrMixed() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		OrFilter<Dummy1> fred_filter = new OrFilter<>(fac.getTag(),fac);
		
		fred_filter.addFilter(fac.new StringFilter("fred"));
		fred_filter.addFilter(fac.new NumberFilter(1));
		fred_filter.addFilter(fac.new NumberAcceptFilter(19));
		
		Dummy1 fred2 = fac.find(fred_filter);
		assertEquals("fred", fred2.getName());
		
		Dummy1 bill2 = fac.find((BaseFilter<Dummy1>)fred_filter.acceptVisitor(vis));
		assertEquals("bill",bill2.getName());
		
	}
	@Test
	public void testJoin() throws Exception {
		Dummy1 fred = fac.makeBDO();
		fred.setName("fred");
		fred.setNumber(1);
		fred.commit();
		
		DummyReference fred_ref = ref.makeBDO();
		fred_ref.setReference(fred);
		fred_ref.setName("RefFred");
		fred_ref.commit();
		
		Dummy1 bill = fac.makeBDO();
		bill.setName("bill");
		bill.setNumber(2);
		bill.commit();
		
		DummyReference bill_ref = ref.makeBDO();
		bill_ref.setReference(bill);
		bill_ref.setName("RefBill");
		bill_ref.commit();
		
		SQLOrFilter<DummyReference> fred_filter = new SQLOrFilter<>(ref.getTag());
	
		fred_filter.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNameFilter("fred"));
		fred_filter.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNumberFilter(1));
		fred_filter.addFilter((SQLFilter<? super DummyReference>) ref.getRemoteNumberDualFilter(19));
		
		DummyReference fred2 = ref.find(fred_filter);
		assertEquals("RefFred", fred2.getName());
		
		DummyReference bill2 = ref.find((BaseFilter<DummyReference>)fred_filter.acceptVisitor(ref_vis));
		assertEquals("RefBill",bill2.getName());
		
	}
	
}
