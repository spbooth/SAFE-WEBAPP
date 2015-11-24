package uk.ac.ed.epcc.webapp.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.MysqlDateConverter;
import uk.ac.ed.epcc.webapp.jdbc.expr.MysqlMillisecondConverter;
import uk.ac.ed.epcc.webapp.jdbc.expr.MysqlSQLHashExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor;
import uk.ac.ed.epcc.webapp.jdbc.table.MySqlCreateTableVisitor;
import uk.ac.ed.epcc.webapp.session.Hash;

public class MysqlSQLContext implements SQLContext {
	private final AppContext ctx;
	private final Connection conn;

	public MysqlSQLContext(AppContext ctx,Connection conn) {
		this.ctx=ctx;
		this.conn=conn;
	}

	public Connection getConnection() {
		return conn;
	}

	public StringBuilder quote(StringBuilder sb,String name){
		  sb.append("`");
		  sb.append(name);
		  sb.append("`");
		  return sb;
	  }

	public StringBuilder quoteQualified(StringBuilder sb, String table,
			String name) {
		quote(sb,table);
		sb.append(".");
		quote(sb,name);
		return sb;
	}
	public SQLExpression<Number> convertToMilliseconds(SQLExpression<Date> expr) {
		return new MysqlMillisecondConverter(expr);
	}
	public DateSQLExpression convertToDate(SQLExpression<? extends Number> val, long res) {
		return new MysqlDateConverter(res, val);
	}


	public FieldTypeVisitor getCreateVisitor(StringBuilder sb, List<Object> args) {
		return new MySqlCreateTableVisitor(this, sb, args);
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
		return new MysqlSQLHashExpression(h, arg);
	}

}
