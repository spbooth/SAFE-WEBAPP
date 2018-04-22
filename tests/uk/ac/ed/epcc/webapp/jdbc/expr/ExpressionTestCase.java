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

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.ExpressionTestFactory.ExpressionTest;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.model.data.Duration;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.expr.DurationConvertSQLValue;
import uk.ac.ed.epcc.webapp.model.data.expr.DurationSQLExpression;

/**
 * @author spb
 *
 */
public class ExpressionTestCase extends WebappTestBase {

	/**
	 * 
	 */
	public ExpressionTestCase() {
		// TODO Auto-generated constructor stub
	}
	
	public ExpressionTestFactory fac;
	public ExpressionTest obj;
	
	@Before
	public void setup() throws DataFault {
		fac = new ExpressionTestFactory(ctx);
		obj=fac.makeFromString("Boris");
	}

	
	@Test
	public void addInteger() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getIntA(), Operator.ADD, fac.getIntB()));
		// always double from sql		
		assertEquals(12.0, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getIntA(), Operator.ADD, fac.getIntB()));	
		assertEquals(12, res);
		
	}
	@Test
	public void subInteger() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getIntA(), Operator.SUB, fac.getIntB()));
		// always double from sql		
		assertEquals(2.0, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getIntA(), Operator.SUB, fac.getIntB()));	
		assertEquals(2, res);
		
	}
	@Test
	public void mulInteger() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getIntA(), Operator.MUL, fac.getIntB()));
		// always double from sql		
		assertEquals(35.0, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getIntA(), Operator.MUL, fac.getIntB()));	
		assertEquals(35, res);
		
	}
	@Test
	public void divInteger() throws DataException {
		obj.setIntA(10);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getIntA(), Operator.DIV, fac.getIntB()));
		// always double from sql		
		assertEquals(2.0, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getIntA(), Operator.DIV, fac.getIntB()));	
		assertEquals(2, res);
		
	}
	@Test
	public void divInteger2() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getIntA(), Operator.DIV, fac.getIntB()));
		// always double from sql		
		assertEquals(1.4, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getIntA(), Operator.DIV, fac.getIntB()));	
		assertEquals(1.4, res);
		
	}
	
	@Test
	public void addDouble() throws DataException {
		obj.setDoubleA(7.0);
		obj.setDoubleB(5.0);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getDoubleA(), Operator.ADD, fac.getDoubleB()));
		// always double from sql		
		assertEquals(12.0, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getDoubleA(), Operator.ADD, fac.getDoubleB()));	
		assertEquals(12.0, res);
		
	}
	
	@Test
	public void addConstDouble() throws DataException {
		obj.setDoubleA(7.0);
		//obj.setDoubleB(5.0);
		obj.commit();
		Number res = fac.evaluate(obj, BinaryExpression.create(ctx, fac.getDoubleA(), Operator.ADD, new ConstExpression<Double,ExpressionTest>(Double.class, 5.0)));
		// always double from sql		
		assertEquals(12.0, res);
		
		res = fac.evaluate(obj, new BinarySQLValue(ctx, fac.getDoubleA(), Operator.ADD, new ConstExpression<Double,ExpressionTest>(Double.class, 5.0)));	
		assertEquals(12.0, res);
		
	}
	
	@Test
	public void testDurationExpression() throws DataException, SQLException {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(1965,Calendar.DECEMBER, 12);
		
		obj.setDateA( c.getTime());
		
		c.set(1965,Calendar.DECEMBER,25);
		obj.setDateB( c.getTime());
		obj.commit();
		Duration d = fac.evaluate(obj, new DurationSQLExpression(fac.convertDateExpression(fac.getDateA()),fac.convertDateExpression( fac.getDateB())));
		assertEquals(13*24*60*60, d.getSeconds());
	}
	
	@Test
	public void testDurationSeconds() throws DataException, SQLException {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(1965,Calendar.DECEMBER, 12);
		
		obj.setDateA( c.getTime());
		
		c.set(1965,Calendar.DECEMBER,25);
		obj.setDateB( c.getTime());
		obj.commit();
		Number n = fac.evaluate(obj, new DurationSecondConvertSQLValue(new DurationSQLExpression(fac.convertDateExpression(fac.getDateA()),fac.convertDateExpression( fac.getDateB()))));
		assertEquals(13*24*60*60, n.intValue());
	}
	@Test
	public void stringExpression() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Object res = fac.evaluate(obj, new StringConvertSQLExpression<>( BinaryExpression.create(ctx, fac.getIntA(), Operator.ADD, fac.getIntB())));
		// always double from sql		
		assertEquals("12", res);
		
		res = fac.evaluate(obj, new StringConvertSQLValue<>( new BinarySQLValue(ctx, fac.getIntA(), Operator.ADD, fac.getIntB())));	
		assertEquals("12", res);
		
	}
	@Test
	public void intExpression() throws DataException {
		obj.setStringA("12");
		obj.commit();
		Object res = fac.evaluate(obj, new IntConvertSQLValue<>(fac.getStringA()) );
		assertEquals(12, res);
	}
	@Test
	public void intExpression2() throws DataException {
		obj.setDoubleA(12.0);
		obj.commit();
		Object res = fac.evaluate(obj, new IntConvertSQLValue<>(fac.getDoubleA()) );
		assertEquals(12, res);
	}
	@Test
	public void longExpression() throws DataException {
		obj.setStringA("12");
		obj.commit();
		Object res = fac.evaluate(obj, new CastLongSQLExpression(fac.getStringA()) );
		assertEquals(12L, res);
	}
	@Test
	public void longExpression2() throws DataException {
		obj.setDoubleA(12.0);
		obj.commit();
		Object res = fac.evaluate(obj, new CastLongSQLExpression<>(fac.getDoubleA()) );
		assertEquals(12L, res);
	}
	@Test
	public void doubleExpression() throws DataException {
		obj.setStringA("12");
		obj.commit();
		Object res = fac.evaluate(obj, new CastDoubleSQLExpression(fac.getStringA()) );
		assertEquals(12.0, res);
	}
	@Test
	public void doubleExpression2() throws DataException {
		obj.setIntA(12);
		obj.commit();
		Object res = fac.evaluate(obj, new CastDoubleSQLExpression<>(fac.getIntA()) );
		assertEquals(12.0, res);
	}
	@Test
	public void durationCast() throws DataException {
		obj.setDoubleA(12.0);
		obj.commit();
		Duration res = fac.evaluate(obj, new DurationConvertSQLValue<>(fac.getDoubleA(),1000L) );
		assertEquals(12, res.getSeconds());
	}
	@Test
	public void testMillisecondToDate() throws DataException, SQLException {
		
		Calendar c = Calendar.getInstance();
		
		c.clear();
		// IT does NOT work for dates before 1970
		c.set(1975,Calendar.DECEMBER, 12);
		obj.setLongA(c.getTimeInMillis());
		obj.commit();
		SQLContext sql = ctx.getService(DatabaseService.class).getSQLContext();
		Date d = fac.evaluate(obj,sql.convertToDate(fac.getLongA(),1L));
		assertEquals(c.getTime(),d);
	}
	
	@Test
	public void testArrayFunc() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, 
				new ArrayFuncExpression<>(fac.getTarget(), ArrayFunc.GREATEST, Number.class, fac.getIntA(),fac.getIntB()));
		assertEquals(7, res);
		res = fac.evaluate(obj, 
				new ArrayFuncExpression<>(fac.getTarget(), ArrayFunc.LEAST, Number.class, fac.getIntA(),fac.getIntB()));
		assertEquals(5, res);
	}
	@Test
	public void testArrayFuncValue() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		Number res = fac.evaluate(obj, 
				new ArrayFuncValue<>(fac.getTarget(), ArrayFunc.GREATEST, Number.class, fac.getIntA(),fac.getIntB()));
		assertEquals(7, res);
		res = fac.evaluate(obj, 
				new ArrayFuncValue<>(fac.getTarget(), ArrayFunc.LEAST, Number.class, fac.getIntA(),fac.getIntB()));
		assertEquals(5, res);
	}
	
	@Test
	public void testArrayFuncDate() throws DataException {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(1975,Calendar.DECEMBER, 12);
		
		Date least = c.getTime();
		obj.setDateA( least);
		
		c.set(1975,Calendar.DECEMBER,25);
		Date later = c.getTime();
		obj.setDateB(later);
		obj.commit();
		Date res = (Date)fac.evaluate(obj, 
				new ArrayFuncExpression<>(fac.getTarget(), ArrayFunc.GREATEST, Date.class,new SQLExpression[] { (SQLExpression<Date>)fac.getDateA(),(SQLExpression<Date>)fac.getDateB()}));
		assertEquals(later, res);
		res = (Date)fac.evaluate(obj, 
				new ArrayFuncExpression<>(fac.getTarget(), ArrayFunc.LEAST, Date.class,new SQLExpression[] { (SQLExpression<Date>)fac.getDateA(),(SQLExpression<Date>)fac.getDateB()}));
		assertEquals(least, res);
	}
	@Test
	public void testArrayFuncValueDate() throws DataException {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(1975,Calendar.DECEMBER, 12);
		
		Date least = c.getTime();
		obj.setDateA( least);
		
		c.set(1975,Calendar.DECEMBER,25);
		Date later = c.getTime();
		obj.setDateB(later);
		obj.commit();
		Date res = (Date)fac.evaluate(obj, 
				new ArrayFuncValue<>(fac.getTarget(), ArrayFunc.GREATEST, Date.class,new SQLExpression[] { (SQLExpression<Date>)fac.getDateA(),(SQLExpression<Date>)fac.getDateB()}));
		assertEquals(later, res);
		res = (Date)fac.evaluate(obj, 
				new ArrayFuncValue<>(fac.getTarget(), ArrayFunc.LEAST, Date.class,new SQLExpression[] { (SQLExpression<Date>)fac.getDateA(),(SQLExpression<Date>)fac.getDateB()}));
		assertEquals(least, res);
	}
	@Test 
	public void testCompare() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		
		Boolean res = fac.evaluate(obj, new CompareSQLExpression<>(fac.getIntA(), MatchCondition.GT, fac.getIntB()));
		assertTrue(res);
		res = fac.evaluate(obj, new CompareSQLExpression<>(fac.getIntA(), null, fac.getIntB()));
		assertFalse(res);
		res = fac.evaluate(obj, new CompareSQLExpression<>(fac.getIntA(), MatchCondition.LT, fac.getIntB()));
		assertFalse(res);
		
		obj.setIntB(7);
		obj.commit();
		res = fac.evaluate(obj, new CompareSQLExpression<>(fac.getIntA(), null, fac.getIntB()));
		assertTrue(res);
	}
	@Test 
	public void testCompareValue() throws DataException {
		obj.setIntA(7);
		obj.setIntB(5);
		obj.commit();
		
		Boolean res = (Boolean)fac.evaluate(obj, new CompareSQLValue(ctx,fac.getIntA(), MatchCondition.GT, fac.getIntB()));
		assertTrue(res);
		res = fac.evaluate(obj, new CompareSQLValue<>(ctx,fac.getIntA(), null, fac.getIntB()));
		assertFalse(res);
		res = fac.evaluate(obj, new CompareSQLValue<>(ctx,fac.getIntA(), MatchCondition.LT, fac.getIntB()));
		assertFalse(res);
		
		obj.setIntB(7);
		obj.commit();
		res = fac.evaluate(obj, new CompareSQLValue<>(ctx,fac.getIntA(), null, fac.getIntB()));
		assertTrue(res);
	}
	@Test 
	public void testCompareDate() throws DataException {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(1975,Calendar.DECEMBER, 12);
		
		Date least = c.getTime();
		obj.setDateB( least);
		
		c.set(1975,Calendar.DECEMBER,25);
		Date later = c.getTime();
		obj.setDateA(later);
		obj.commit();
		
		Boolean res = fac.evaluate(obj, new CompareSQLExpression<>((SQLExpression<Date>)fac.getDateA(), MatchCondition.GT,(SQLExpression<Date>) fac.getDateB()));
		assertTrue(res);
		res = fac.evaluate(obj, new CompareSQLExpression<>((SQLExpression<Date>)fac.getDateA(), null,(SQLExpression<Date>) fac.getDateB()));
		assertFalse(res);
		res = fac.evaluate(obj, new CompareSQLExpression<>((SQLExpression<Date>)fac.getDateA(), MatchCondition.LT,(SQLExpression<Date>) fac.getDateB()));
		assertFalse(res);
		
		obj.setDateB(later);
		obj.commit();
		res = fac.evaluate(obj, new CompareSQLExpression<>((SQLExpression<Date>)fac.getDateA(), null,(SQLExpression<Date>) fac.getDateB()));
		assertTrue(res);
	}
	@Test 
	public void testCompareValueDate() throws DataException {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(1975,Calendar.DECEMBER, 12);
		
		Date least = c.getTime();
		obj.setDateB( least);
		
		c.set(1975,Calendar.DECEMBER,25);
		Date later = c.getTime();
		obj.setDateA(later);
		obj.commit();
		
		Boolean res = fac.evaluate(obj, new CompareSQLValue<>(ctx,(SQLExpression<Date>)fac.getDateA(), MatchCondition.GT,(SQLExpression<Date>) fac.getDateB()));
		assertTrue(res);
		res = fac.evaluate(obj, new CompareSQLValue<>(ctx,(SQLExpression<Date>)fac.getDateA(), null,(SQLExpression<Date>) fac.getDateB()));
		assertFalse(res);
		res = fac.evaluate(obj, new CompareSQLValue<>(ctx,(SQLExpression<Date>)fac.getDateA(), MatchCondition.LT,(SQLExpression<Date>) fac.getDateB()));
		assertFalse(res);
		
		obj.setDateB(later);
		obj.commit();
		res = fac.evaluate(obj, new CompareSQLValue<>(ctx,(SQLExpression<Date>)fac.getDateA(), null,(SQLExpression<Date>) fac.getDateB()));
		assertTrue(res);
	}
}
