// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link DatabaseService} that also looks for a pooled connection via JNDID.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: JNDIDatabaseService.java,v 1.4 2014/09/15 14:30:22 spb Exp $")
public class JNDIDatabaseService extends DefaultDataBaseService {

	public JNDIDatabaseService(AppContext ctx) {
		super(ctx);
	}
	@Override
	protected SQLContext makeSQLContext(String tag,Properties prop) throws SQLException {
		if( tag == null){
			String pool_resource = prop.getProperty("connection.pool");
			if (pool_resource != null) {
				// if we are using resource pooling get a connection now
				// otherwise wait until we actually need it.
				String lookup = "java:comp/env/" + pool_resource;
				try {
					Context ct = new InitialContext();

					DataSource ds = (DataSource) ct.lookup(lookup);
					Connection conn =  ds.getConnection();
					String type = prop.getProperty("db_type","").trim();
					if( type.contains(POSTGRESQL_TYPE) ){
						return new PostgresqlSQLContext(getContext(),conn);
					}
					return new MysqlSQLContext(getContext(),conn);
				} catch (Throwable e) {
					getContext().error(e, "error attaching to connection pool " + lookup);
				}
			}
		}
		return super.makeSQLContext(tag,prop);
	}
}