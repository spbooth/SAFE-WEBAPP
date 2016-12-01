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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/**
 * @author spb
 *
 */
public class DateDerefSQLExpression<H extends DataObject,R extends DataObject> extends DerefSQLExpression<H,R,Date> implements DateSQLExpression{

	/**
	 * @param a
	 * @param expr
	 * @throws Exception
	 */
	public DateDerefSQLExpression(IndexedSQLValue a, DateSQLExpression expr) throws Exception {
		super(a, expr);
	}
	public DateDerefSQLExpression(IndexedSQLValue<H, R> a, DateSQLExpression expr, SQLFilter fil) {
		super(a, expr, fil);
	}
	/** helper method to allow a {@link SQLContext} to convert {@link DerefSQLExpression}s
	 * 
	 * 
	 * @param sql  {@link SQLContext}
	 * @param expr 
	 * @param res  long resolution in milliseconds of numeric expression
	 * @return
	 */
	public static <H extends DataObject,R extends DataObject> DateDerefSQLExpression<H, R> convertToDate(SQLContext sql, DerefSQLExpression<H, R, ? extends Number> expr,long res){
		
		return new DateDerefSQLExpression<H,R>(expr.a, sql.convertToDate(expr.remote_expression, res),expr.required_filter);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression#getMillis()
	 */
	@Override
	public SQLExpression<? extends Number> getMillis() {
		
		return new DerefSQLExpression<>(a,((DateSQLExpression)remote_expression).getMillis(),required_filter);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression#getSeconds()
	 */
	@Override
	public SQLExpression<? extends Number> getSeconds() {
		return new DerefSQLExpression<>(a,((DateSQLExpression)remote_expression).getSeconds(),required_filter);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression#preferSeconds()
	 */
	@Override
	public boolean preferSeconds() {
		return ((DateSQLExpression)remote_expression).preferSeconds();
	}
}
