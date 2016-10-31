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

import java.sql.SQLException;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.ConfigService;


/** A service that supplies the DataBase Connection
 * 
 * @author spb
 *
 */
public interface DatabaseService extends Contexed , AppContextService<DatabaseService>{
  public static final Feature LOG_QUERY_FEATURE = new Feature("log_query",false,"log all SQL queries");
public static final Feature USE_SQL_DISTICT_FEATURE = new Feature("use_sql_distict",false,"add distinct clause when selecting objects from table. Should not need this unless filters are malformed");
public static final Feature LOG_INSERT_FEATURE = new Feature("log_insert",false,"loag all SQL inserts");
public static final Feature LOG_UPDATE = new Feature("log_update",false,"log all SQL updates");

/** get a {SQLContext} based on properties
 * 
 * @param tag
 * @param config_props
 * @return {@link SQLContext}
 * @throws SQLException
 */
public SQLContext getSQLContext(String tag,Properties config_props) throws SQLException;
/** get {@link SQLContext} using the default {@link ConfigService}
 * 
 * @return {@link SQLContext} to database
 * @throws SQLException
 */
public SQLContext getSQLContext() throws SQLException;

/** get alternative database {@link SQLContext} 
 * 
 * If the tag is null it returns the same {@link SQLContext} as
 * {@link #getSQLContext()}.
 * 
 * @param tag 
 * @return Connection to database
 * @throws SQLException  
 */
public SQLContext getSQLContext(String tag) throws SQLException;

/** Start a database transaction 
 * 
 */
public void startTransaction();

/** Abort changes since start or last commit
 * 
 */
public void rollbackTransaction();

/** flush changes since start of last commit.
 * Unlike the commit method of a Connection this is a NOP if
 * not in a transaction.
 * 
 */
public void commitTransaction();

/** finish transaction (includes commit).
 * 
 */
public void stopTransaction();
}