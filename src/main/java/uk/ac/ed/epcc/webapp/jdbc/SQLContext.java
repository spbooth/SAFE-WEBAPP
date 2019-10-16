//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.jdbc;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.expr.BinaryExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.BinarySQLValue;
import uk.ac.ed.epcc.webapp.jdbc.expr.ConstExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.Operator;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor;
import uk.ac.ed.epcc.webapp.model.data.expr.DurationSQLExpression;
import uk.ac.ed.epcc.webapp.session.Hash;
/** This encodes the differences between different databases and
 * dialects of SQL.
 * 
 * @author spb
 *
 */
public interface SQLContext extends Contexed{
	/** Get a database connection for the current context
	 * 
	 * @return
	 */
	public Connection getConnection();
	/** Quote a field or table name
	 * 
	 * @param sb StringBuilder
	 * @param name
	 * @return modified StringBuilder
	 */
	public StringBuilder quote(StringBuilder sb, String name);
	/** Quote a field or table name
	 * 
	 * @param sb StringBuilder
	 * @param table
	 * @param name
	 * @return modified StringBuilder
	 */
	public StringBuilder quoteQualified(StringBuilder sb, String table, String name);
	/** Convert a Date {@link SQLExpression} to milliseconds
	 * 
	 * @param expr
	 * @return SQLExpression
	 */
	
	public SQLExpression<? extends Number> convertToMilliseconds(SQLExpression<Date> expr);
	
	/** generate a {@link SQLExpression} for the difference between two dates.
	 * 
	 * @param resolution  size of time unit (in milliseconds)
	 * @param start 
	 * @param end
	 * @return {@link SQLExpression}
	 */
	default public SQLExpression<? extends Number> dateDifference(long resolution, SQLExpression<Date> start, SQLExpression<Date> end){
		if( resolution == 1L) {
			return new DurationSQLExpression(convertToMilliseconds(start), convertToMilliseconds(end));
		}
		
		return new DurationSQLExpression(resolution, 
				BinaryExpression.create(getContext(), convertToMilliseconds(start), Operator.DIV, new ConstExpression(Long.class, resolution)),
				BinaryExpression.create(getContext(), convertToMilliseconds(end), Operator.DIV, new ConstExpression(Long.class, resolution)));
	}
	/** Convert a Number {@link SQLExpression} to a date.
	 * 
	 * @param val
	 * @param res resolution in milliseconds
	 * @return {@link SQLExpression}
	 */
	public DateSQLExpression convertToDate(SQLExpression<? extends Number> val, long res);
	/** Generate a SQL hash function.
	 * 
	 * @param h  {@link Hash} algorithm to apply
	 * @param arg {@link SQLExpression} to hash
	 * @return {@link SQLExpression}
	 * @throws CannotUseSQLException
	 */
	public SQLExpression<String> hashFunction(Hash h, SQLExpression<String> arg) throws CannotUseSQLException;
	/** Get a {@link FieldTypeVisitor} used to generate table specifications.
	 * 
	 * @param sb StringBuilder  to add SQL fragments to
	 * @param args List query parameters
	 * @return {@link FieldTypeVisitor}
	 */
	public FieldTypeVisitor getCreateVisitor(StringBuilder sb, List<Object> args);
	
	/** Get an identifying string for the database host we are connected to.
	 * 
	 * @return
	 */
	public String getConnectionHost();
	/** Clean up internal state
	 * @throws Exception 
	 * 
	 */
	public void close() throws Exception;
	
	/** get the {@link DatabaseService}
	 * 
	 * @return
	 */
	public DatabaseService getService();
	
}