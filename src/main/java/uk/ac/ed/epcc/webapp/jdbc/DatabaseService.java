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
import java.sql.SQLTransientException;
import java.util.Map;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.*;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;


/** A service that supplies the DataBase Connection
 * 
 * @author spb
 *
 */

public interface DatabaseService extends Contexed , AppContextService<DatabaseService>, CloseRegistry{
  public static final Feature LOG_QUERY_FEATURE = new Feature("log_query",false,"log all SQL queries");
public static final Feature USE_SQL_DISTICT_FEATURE = new Feature("use_sql_distict",false,"add distinct clause when selecting objects from table. Should not need this unless filters are malformed");
public static final Feature LOG_INSERT_FEATURE = new Feature("log_insert",false,"log all SQL inserts");
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

/** Are we already in a transaction
 * 
 * @return boolean
 */

public boolean inTransaction();

/** returns the number of times {@link #commitTransaction()} has been called
 * since the last transaction start. 
 * If this returns 0 then a roll-back will return to the state at {@link #startTransaction()} if greater than zero
 * there have been intermediate commits. 
 * 
 * To aid in testing the value is not reset to zero until the next call to {@link #startTransaction()}
 * 
 * 
 * @return int
 */
public int transactionStage();

/** Start a database transaction 
 * Transactions cannot be nested so use {@link #inTransaction()} if possible doubt.
 */
public void startTransaction();

/** Register a Runnable as belonging to the current transaction.
 * (If we are currently within a transaction)
 * If the transaction is rolled back these will be removed from the {@link CleanupService}
 * 
 * @param r
 */
public void addCleanup(Runnable r);

/** Abort changes since start or last commit
 * 
 */
public void rollbackTransaction();

/** flush changes since start of last commit.
 * Unlike the commit method of a Connection this is a NOP if
 * not in a transaction.
 * 
 * Call before an operation that modifies external state as these won't roll-back. However probably better to
 * defer the external state change using the {@link CleanupService} unless the transaction phases all
 * make sense in their own right.
 * 
 */
public void commitTransaction();

/** finish transaction (includes commit).
 * 
 */
public void stopTransaction();

/** Handle an unexpected database exception.
 * 
 * Most of the time this will just re-throw the exception wrapped in a {@link DataFault} however
 * this also allow special handing of certain classes of error such as {@link SQLTransientException}s
 * 
 * @param e
 * @throws DataFault
 */
public void handleError(String message,SQLException e)throws DataFault;

/** handle an {@link SQLException} directly. Normally this will just log the error however
 * this also allow special handing of certain classes of error such as {@link SQLTransientException}s
 * @param message
 * @param e
 */
public void logError(String message,SQLException e);
/** get a map of information about the connection
 * 
 * @return
 * @throws Exception 
 */
public Map<String,Object> getConnectionAttributes() throws Exception;
}