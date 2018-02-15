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
package uk.ac.ed.epcc.webapp.model;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpressionMatchFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.TupleFactory;
import uk.ac.ed.epcc.webapp.model.data.TupleFactory.Tuple;
import uk.ac.ed.epcc.webapp.model.data.TupleFactory.TupleAndFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import static org.junit.Assert.*;

import java.util.Iterator;
/**
 * @author spb
 *
 */
public class TupleFactoryTest<A extends DataObject,AF extends DataObjectFactory<A>,T extends TupleFactory.Tuple<A>> extends WebappTestBase {

	Dummy1.Factory fac1;
	Dummy2.Factory fac2;
	TupleFactory<A,AF,T> fac;
	
	@Before
	public void setUp() throws DataFault {
		fac1 = new Dummy1.Factory(ctx);
		fac2 = new Dummy2.Factory(ctx);
		fac = new TupleFactory<A,AF,T>(ctx, (AF)fac1,(AF)fac2);
		
		Dummy1 d1a = new Dummy1(ctx);
		d1a.setName("Name1");
		d1a.setNumber(1);
		d1a.commit();
		Dummy1 d1b = new Dummy1(ctx);
		d1b.setName("Name2");
		d1b.setNumber(2);
		d1b.commit();
		Dummy1 d1c = new Dummy1(ctx);
		d1c.setName("Name3");
		d1c.setNumber(3);
		d1c.commit();
		
		
		Dummy2 d2a = new Dummy2(ctx);
		d2a.setName("Name1");
		d2a.commit();
		Dummy2 d2b = new Dummy2(ctx);
		d2b.setName("Name2");
		d2b.commit();
		
	}

	
	
	@Test
	public void testCountWithoutFilter() throws DataException{
		assertEquals(3,fac1.getCount(null));
		assertEquals(2,fac2.getCount(null));
		
		assertEquals(6,fac.getCount(null));
	}
	
	@Test 
	public void testIterateWithoutFilter() throws DataFault{
		int count = 0;
		for(Tuple t : fac.makeResult(null)){
			assertEquals(2,t.size());
			assertTrue(t.containsKey(fac1.getTag()));
			assertTrue(t.containsKey(fac2.getTag()));
			count++;
		}
		assertEquals(6,count);
		
		count=0;
		
		Iterator<T> it =  fac.makeResult(null).iterator(); 
		for(Dummy1 d1 : fac1.all()){
			for(Dummy2 d2 : fac2.all()){
				Tuple t = it.next();
				assertEquals(d1, t.get(fac1.getTag()));
				assertEquals(d2, t.get(fac2.getTag()));
				count++;
			}
		}
		assertEquals(6,count);
	}

	
	@Test 
	public void testOffsetIterateWithoutFilter() throws DataFault{
		int count = 0;
		Iterator<T> it = fac.makeResult(null,0,4).iterator(); 
		for(Dummy1 d1 : fac1.getResult(null,0,2)){
			for(Dummy2 d2 : fac2.all()){
				Tuple t = it.next();
				assertEquals(d1, t.get(fac1.getTag()));
				assertEquals(d2, t.get(fac2.getTag()));
				count++;
			}
		}
		assertEquals(4,count);
		it =  fac.makeResult(null,4,100).iterator(); 
		for(Dummy1 d1 : fac1.getResult(null, 2, 100)){
			for(Dummy2 d2 : fac2.all()){
				Tuple t = it.next();
				assertEquals(d1, t.get(fac1.getTag()));
				assertEquals(d2, t.get(fac2.getTag()));
				count++;
			}
		}
		assertEquals(6,count);
	}
	
	@Test
	public void testFilteredJoin() throws DataException{
		TupleAndFilter fil = fac.new TupleAndFilter();
		SQLExpression<String> nameExpression = fac1.getNameExpression();
		SQLExpression<String> nameExpression2 = fac2.getNameExpression();
		SQLFilter join = SQLExpressionMatchFilter.getFilter(fac.getTarget(), nameExpression, nameExpression2);
		fil.addFilter(join);
		assertEquals(2,fac.getCount((BaseFilter<T>)fil));
	}
	
	@Test
	public void TestSQLTupleFilter() throws Exception{
		TupleAndFilter fil = fac.new TupleAndFilter();
		fil.addFilter(SQLExpressionMatchFilter.getFilter(fac.getTarget(), fac1.getNameExpression(), fac2.getNameExpression()));
		fil.addMemberFilter(fac1.getTag(), fac1.new NumberFilter(Integer.valueOf(1)));
		assertEquals(1,fac.getCount((BaseFilter<T>)fil));
	}
	
	@Test
	public void TestAcceptTupleFilter() throws Exception{
		TupleAndFilter fil = fac.new TupleAndFilter();
		fil.addFilter(SQLExpressionMatchFilter.getFilter(fac.getTarget(), fac1.getNameExpression(), fac2.getNameExpression()));
		fil.addMemberFilter(fac1.getTag(), fac1.new NumberAcceptFilter(Integer.valueOf(1)));
		assertEquals(1,fac.getCount((BaseFilter<T>)fil));
	}
}
