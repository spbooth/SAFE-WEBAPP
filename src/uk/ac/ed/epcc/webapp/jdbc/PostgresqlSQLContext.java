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
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.expr.DateDerefSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.DerefSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.PostgresqlDateConverter;
import uk.ac.ed.epcc.webapp.jdbc.expr.PostgresqlMillisecondConverter;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor;
import uk.ac.ed.epcc.webapp.jdbc.table.PostgresqlCreateTableVisitor;
import uk.ac.ed.epcc.webapp.session.Hash;

public class PostgresqlSQLContext implements SQLContext {
	private final AppContext ctx;
	private final Connection conn;

	public PostgresqlSQLContext(AppContext ctx,Connection conn) {
		this.ctx=ctx;
		this.conn=conn;
	}

	public Connection getConnection() {
		return conn;
	}

	public StringBuilder quote(StringBuilder sb,String name){
		  sb.append('"');
		  sb.append(name);
		  sb.append('"');
		  return sb;
	  }

	public StringBuilder quoteQualified(StringBuilder sb, String table,
			String name) {
		quote(sb,table);
		sb.append(".");
		quote(sb,name);
		return sb;
	}
	public SQLExpression<? extends Number> convertToMilliseconds(SQLExpression<Date> expr) {
		if( expr instanceof DateSQLExpression){
			return ((DateSQLExpression) expr).getMillis();
		}
		if( expr instanceof DerefSQLExpression){
			return DerefSQLExpression.convertToMillis(this, (DerefSQLExpression) expr);
		}
		return new PostgresqlMillisecondConverter(expr);
	}
	public DateSQLExpression convertToDate(SQLExpression<? extends Number> val, long res) {
		if( val instanceof DerefSQLExpression){
			return DateDerefSQLExpression.convertToDate(this, (DerefSQLExpression) val,res);
		}
		return new PostgresqlDateConverter(res, val);
	}


	public FieldTypeVisitor getCreateVisitor(StringBuilder sb, List<Object> args) {
		return new PostgresqlCreateTableVisitor(this, sb, args);
	}

	public void close() throws SQLException {
		if(conn != null){
			conn.close();
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return ctx;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#hashFunction(uk.ac.ed.epcc.webapp.session.Hash, uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression)
	 */
	public SQLExpression<String> hashFunction(Hash h, SQLExpression<String> arg)
			throws CannotUseSQLException {
		throw new CannotUseSQLException("Hash functions not yet implemetned in postrgresql");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.SQLContext#getConnectionHost()
	 */
	@Override
	public String getConnectionHost() {
		return "Unknown";
	}

}