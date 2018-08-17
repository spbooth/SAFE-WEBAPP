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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.expr.ExpressionTestFactory.ExpressionTest;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.FieldSQLExpression;
import uk.ac.ed.epcc.webapp.model.data.FieldValue;
import uk.ac.ed.epcc.webapp.model.data.StringFieldExpression;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author Stephen Booth
 *
 */
public class ExpressionFilterTest extends WebappTestBase {
	public ExpressionTestFactory fac;
	public ExpressionTest obj;
	
	@Before
	public void setup() throws DataFault {
		fac = new ExpressionTestFactory(ctx);
		obj=fac.makeFromString("Boris");
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2000, Calendar.JANUARY, 1);
		obj.setDateA(c.getTime());
		obj.setIntA(12);
		obj.setDoubleA(12.0);
		obj.commit();
	}
	
	
	@Test
	public void testMatch() {
		StringFieldExpression exp = fac.getNameSQLAccessor();
		SQLFilter filter = SQLExpressionFilter.getFilter(fac.getTarget(),exp,"Boris");
		assertTrue(fac.matches(filter, obj));
		assertEquals("FieldExpression(ExpressionTest.Name->java.lang.String)",exp.toString());
		assertEquals("SQLExpressionFilter(FieldExpression(ExpressionTest.Name->java.lang.String)=Boris)",filter.toString());
	}
	
	@Test
	public void testMatch2() {
		FieldSQLExpression<Integer, ExpressionTest> exp = fac.getIntA();
		SQLFilter<ExpressionTest> filter = SQLExpressionFilter.getFilter(fac.getTarget(),exp,MatchCondition.GT,3);
		assertTrue(fac.matches(filter, obj));
		assertEquals("FieldExpression(ExpressionTest.IntA->java.lang.Integer)", exp.toString());
		assertEquals("SQLExpressionFilter(FieldExpression(ExpressionTest.IntA->java.lang.Integer)>3)",filter.toString());
	}
	@Test
	public void testMatch3() {
		FieldSQLExpression<Integer, ExpressionTest> exp = fac.getIntA();
		SQLFilter<ExpressionTest> filter = SQLExpressionFilter.getFilter(fac.getTarget(),exp,MatchCondition.LE,3);
		assertFalse(fac.matches(filter, obj));
		assertEquals("FieldExpression(ExpressionTest.IntA->java.lang.Integer)", exp.toString());
		assertEquals("SQLExpressionFilter(FieldExpression(ExpressionTest.IntA->java.lang.Integer)<=3)",filter.toString());
	}
	@Test
	public void testMatchDate() {
		SQLExpression<Date> exp = (SQLExpression<Date>) fac.getDateA();
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2018, Calendar.JANUARY, 1);
		SQLFilter<ExpressionTest> filter = SQLExpressionFilter.getFilter(fac.getTarget(),exp,MatchCondition.LT,c.getTime());
		assertTrue(fac.matches(filter, obj));
		assertEquals("FieldExpression(ExpressionTest.DateA->java.util.Date)", exp.toString());
		assertEquals("SQLExpressionFilter(FieldExpression(ExpressionTest.DateA->java.lang.Integer)<1514764800)",filter.toString());
	}
	@Test
	public void testMatchDate2() {
		SQLExpression<Date> exp = (SQLExpression<Date>) fac.getDateA();
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2018, Calendar.JANUARY, 1);
		SQLFilter<ExpressionTest> filter = SQLExpressionMatchFilter.getFilter(fac.getTarget(),exp,MatchCondition.LT,new ConstExpression<>(Date.class, c.getTime()));
		assertTrue(fac.matches(filter, obj));
		assertEquals("FieldExpression(ExpressionTest.DateA->java.util.Date)", exp.toString());
		assertEquals("SQLExpressionMatchFilter(FieldExpression(ExpressionTest.DateA->java.util.Date)<Const(Mon Jan 01 00:00:00 GMT 2018))", filter.toString());
		
	}
	@Test
	public void testConstant() {
		assertEquals(new GenericBinaryFilter(fac.getTarget(),true),SQLExpressionMatchFilter.getFilter(fac.getTarget(), new ConstExpression<>(Integer.class, 100),MatchCondition.GT ,new ConstExpression<>(Integer.class, 12)));
	}
}
