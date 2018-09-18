//| Copyright - The University of Edinburgh 2011                            |
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

public class JNDIDatabaseService extends DefaultDataBaseService {

	public JNDIDatabaseService(AppContext ctx) {
		super(ctx);
	}
	@Override
	protected SQLContext makeSQLContext(String tag,Properties prop) throws SQLException {
		if( tag == null){
			String pool_resource = prop.getProperty("connection.pool");
			if (pool_resource != null && pool_resource.trim().length() > 0) {
				// if we are using resource pooling get a connection now
				// otherwise wait until we actually need it.
				String lookup = "java:comp/env/" + pool_resource;
				try {
					Context ct = new InitialContext();

					DataSource ds = (DataSource) ct.lookup(lookup);
					Connection conn =  ds.getConnection();
					String type = prop.getProperty("db_type","").trim();
					if( type.contains(POSTGRESQL_TYPE) ){
						return new PostgresqlSQLContext(getContext(),this,conn);
					}
					return new MysqlSQLContext(getContext(),this,conn);
				} catch (Exception e) {
					getContext().error(e, "error attaching to connection pool " + lookup);
				}
			}
		}
		return super.makeSQLContext(tag,prop);
	}
}