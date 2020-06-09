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
package uk.ac.ed.epcc.webapp.jdbc.wrap;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor;
import uk.ac.ed.epcc.webapp.session.Hash;

/**
 * @author Stephen Booth
 *
 */
public abstract class SQLContextWrapper implements SQLContext {
	/**
	 * @param db
	 * @param nested
	 */
	public SQLContextWrapper(DatabaseService db, SQLContext nested) {
		super();
		this.db = db;
		this.nested = nested;
	}

	private final DatabaseService db;
	private final SQLContext nested;

	public SQLContext getNested() {
		return nested;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return nested.getContext();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#getConnection()
	 */
	@Override
	public Connection getConnection() {
		return nested.getConnection();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#quote(java.lang.StringBuilder, java.lang.String)
	 */
	@Override
	public StringBuilder quote(StringBuilder sb, String name) {
		return nested.quote(sb, name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#quoteQualified(java.lang.StringBuilder, java.lang.String, java.lang.String)
	 */
	@Override
	public StringBuilder quoteQualified(StringBuilder sb, String table, String name) {
		return nested.quoteQualified(sb, table, name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#convertToMilliseconds(uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression)
	 */
	@Override
	public SQLExpression<? extends Number> convertToMilliseconds(SQLExpression<Date> expr) {
		return nested.convertToMilliseconds(expr);
	}

	@Override
	public SQLExpression<? extends Number> dateDifference(long resolution, SQLExpression<Date> start,
			SQLExpression<Date> end) {
		return nested.dateDifference(resolution, start, end);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#convertToDate(uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression, long)
	 */
	@Override
	public DateSQLExpression convertToDate(SQLExpression<? extends Number> val, long res) {
		return nested.convertToDate(val, res);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#hashFunction(uk.ac.ed.epcc.webapp.session.Hash, uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression)
	 */
	@Override
	public SQLExpression<String> hashFunction(Hash h, SQLExpression<String> arg) throws CannotUseSQLException {
		return nested.hashFunction(h, arg);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#getCreateVisitor(java.lang.StringBuilder, java.util.List)
	 */
	@Override
	public FieldTypeVisitor getCreateVisitor(StringBuilder sb, List<Object> args) {
		return nested.getCreateVisitor(sb, args);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#getConnectionHost()
	 */
	@Override
	public String getConnectionHost() {
		return nested.getConnectionHost();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#close()
	 */
	@Override
	public void close() throws Exception {
		nested.close();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#getService()
	 */
	@Override
	public DatabaseService getService() {
		return db;
	}

	@Override
	public boolean isReadOnly() {
		return nested.isReadOnly();
	}

}
