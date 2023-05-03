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

import java.sql.SQLException;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Classification;
import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.FieldSQLExpression;
import uk.ac.ed.epcc.webapp.model.data.FieldValue;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.StringFieldExpression;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLIdFilter;

/** Support class for expression tests.
 * 
 * Data is added to a record via the DataObject then
 * queried directly from the factory.
 * @author spb
 *
 */
public class ExpressionTestFactory extends ClassificationFactory<ExpressionTestFactory.ExpressionTest> {

	/**
	 * 
	 */
	public static final String LONG_A = "LongA";
	/**
	 * 
	 */
	public static final String STRING_A = "StringA";
	/**
	 * 
	 */
	public static final String DATE_B = "DateB";
	/**
	 * 
	 */
	public static final String DATE_A = "DateA";
	/**
	 * 
	 */
	public static final String DOUBLE_B = "DoubleB";
	/**
	 * 
	 */
	public static final String DOUBLE_A = "DoubleA";
	/**
	 * 
	 */
	public static final String INT_B = "IntB";
	/**
	 * 
	 */
	public static final String INT_A = "IntA";
	/**
	 * 
	 */
	public static final String EXPRESSION_TEST_TABLE = "ExpressionTest";

	/**
	 * @param ctx
	 */
	public ExpressionTestFactory(AppContext ctx) {
		super(ctx, EXPRESSION_TEST_TABLE);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ClassificationFactory#getDefaultTableSpecification(uk.ac.ed.epcc.webapp.AppContext, java.lang.String)
	 */
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c, String homeTable) {
		TableSpecification spec = super.getDefaultTableSpecification(c, homeTable);
		spec.setField(INT_A, new IntegerFieldType());
		spec.setField(INT_B, new IntegerFieldType());
		spec.setField(DOUBLE_A, new DoubleFieldType(true,null));
		spec.setField(DOUBLE_B, new DoubleFieldType(true,null));
		spec.setField(DATE_A, new DateFieldType(true, null));
		spec.setField(DATE_B, new DateFieldType(true, null));
		spec.setField(STRING_A, new StringFieldType(true, null, 64));
		spec.setField(LONG_A, new LongFieldType(true, null));
		return spec;
	}
	public static class ExpressionTest extends Classification{

		/**
		 * @param res
		 * @param fac
		 */
		protected ExpressionTest(Record res, ExpressionTestFactory fac) {
			super(res, fac);
		}
		public void setIntA(int val) {
			record.setProperty(INT_A, val);
		}
		public void setIntB(int val) {
			record.setProperty(INT_B, val);
		}
		public void setDoubleA(double val) {
			record.setProperty(DOUBLE_A, val);
		}
		public void setDoubleB(double val) {
			record.setProperty(DOUBLE_B, val);
		}
		public void setDateA(Date val) {
			record.setProperty(DATE_A, val);
		}
		public void setDateB(Date val) {
			record.setProperty(DATE_B, val);
		}
		public void setStringA(String val) {
			record.setProperty(STRING_A, val);
		}
		public void setLongA(long val) {
			record.setProperty(LONG_A, val);
		}
	
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ClassificationFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected ExpressionTest makeBDO(Record res) throws DataFault {
		return new ExpressionTest(res, this);
	}

	
	public class ExpressionFinder<X> extends AbstractFinder<X>{
		public ExpressionFinder(SQLValue<X> val){
			setMapper(new ValueResultMapper<>(val));
		}
	}
	/** Evaluate a {@link SQLValue} on a target object
	 * 
	 * @param target
	 * @param expr
	 * @return
	 * @throws DataException 
	 */
	public <Y> Y evaluate(ExpressionTest target, SQLValue<Y> expr) throws DataException {
		ExpressionFinder<Y> finder = new ExpressionFinder<>(expr);
		return finder.find(new SQLIdFilter<>( res, target.getID()) , true);

	}
	
	public Repository getRepository() {
		return res;
	}
	public FieldSQLExpression<Integer, ExpressionTest> getIntA(){
		return res.getNumberExpression( Integer.class, INT_A);
	}
	public FieldSQLExpression<Integer, ExpressionTest> getIntB(){
		return res.getNumberExpression( Integer.class, INT_B);
	}
	public FieldSQLExpression<Double, ExpressionTest> getDoubleA(){
		return res.getNumberExpression( Double.class, DOUBLE_A);
	}
	public FieldSQLExpression<Double,ExpressionTest> getDoubleB(){
		return res.getNumberExpression( Double.class, DOUBLE_B);
	}
	public FieldValue<Date, ExpressionTest> getDateA(){
		return res.getDateExpression( DATE_A);
	}
	public FieldValue<Date, ExpressionTest> getDateB(){
		return res.getDateExpression( DATE_B);
	}
	public FieldSQLExpression<String, ExpressionTest> getStringA(){
		return res.getStringExpression( STRING_A);
	}
	public FieldSQLExpression<Long, ExpressionTest> getLongA(){
		return res.getNumberExpression( Long.class,LONG_A);
	}
	public SQLExpression<? extends Number> convertDateExpression(FieldValue<Date, ExpressionTest> d) throws SQLException{
		if( d instanceof DateSQLExpression){
			return ((DateSQLExpression)d).getMillis();
		}
		if( d instanceof SQLExpression) {
			
			SQLContext sql = getContext().getService(DatabaseService.class).getSQLContext();
			return sql.convertToMilliseconds((SQLExpression<Date>) d);
		}
		throw new ConsistencyError("Bad convert");
	}
	
	public SQLExpression<Integer> getLocateExpression(String sub,int pos){
		return new LocateSQLExpression(new ConstExpression(String.class, sub), res.getStringExpression( ClassificationFactory.NAME), new ConstExpression(Integer.class, pos));
	}
	
	public StringFieldExpression getNameSQLAccessor(){
		return res.getStringExpression(ClassificationFactory.NAME);
		
	}
	
	public SQLValue<Integer> getLocateValue(String sub,int pos){
		return new LocateSQLValue(new ConstExpression(String.class, sub), res.getStringExpression(ClassificationFactory.NAME), new ConstExpression(Integer.class, pos));
	}
}
