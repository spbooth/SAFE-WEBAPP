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
import java.sql.SQLTransientException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.ForceRollBack;
import uk.ac.ed.epcc.webapp.jdbc.exception.TransactionError;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** Default implementation of the {@link DatabaseService}
 * 
 * This gets connection parameters from the {@link ConfigService} but this is only queried
 * at the point where a database connection is required.
 * 
 * The following config parameters are read:
 * <ul>
 * <li> <b>db_name<i>[.tag]</b> connection url</li>
 * <li> <b>db_type<i>[.tag]</b> connection type [mysql or postgres]</li>
 * <li> <b>db_name<i>[.tag]</b> connection url</li>
 * </ul>
 * 
 * @author spb
 *
 */

@PreRequisiteService(ConfigService.class)
public class DefaultDataBaseService implements DatabaseService {
	public static final Feature TRANSACTIONS_FEATURE = new Feature("database_transactions",true,"Database transactions are supported");
	public static final Feature TRANSACTIONS_SERIALIZE_FEATURE = new Feature("database_transactions.serialized",true,"Database transactions use serialized isolation (locks)");

	public static final Feature TRANSACTIONS_ROLLBACK_TRANSIENT_ERRORS = new Feature("database_transactions.rollback_transient",true,"Transient sql errors generate a ForceRollBack error");
	protected static final String POSTGRESQL_TYPE = "postgres";
	private static final String MYSQL_TYPE = "mysql";
	private AppContext ctx;
	// The database connection:
	private Map<String,SQLContext> map = new HashMap<String, SQLContext>();
	private Set<String> bad_tags = new HashSet<String>();
	// Have we already attempted to set the default connection
	// we remember this so that we we don't keep attempting on fail
    private boolean connection_set=false;
    private boolean closed=false;
    private final boolean force_rollback;
    private int old_isolation_level;
    private boolean in_transaction = false;
    private int stage_count=0;
    private final int desired_isolation_level;
	private Set<AutoCloseable> closes=null;
    public DefaultDataBaseService(AppContext ctx){
    	this.ctx=ctx;
    	force_rollback= TRANSACTIONS_ROLLBACK_TRANSIENT_ERRORS.isEnabled(ctx);
    	desired_isolation_level=parseLevel(ctx.getInitParameter("transaction.isolation_level"));
    }
    public final SQLContext getSQLContext() throws SQLException {
    	return getSQLContext(null);
    }
	public final SQLContext getSQLContext(String tag) throws SQLException {
		return getSQLContext(tag,ctx.getService(ConfigService.class).getServiceProperties());
	}
	/**
	 * returns a database connection for the application based on a set of Config Propertis
	 * 
	 * @return A {@link SQLContext}
	 * @throws SQLException
	 */
	public synchronized  SQLContext getSQLContext(String tag,Properties props) throws SQLException {
		if( closed ) {
			throw new SQLException("DatabaseService already closed");
		}
		String key = tag;
		if( key == null ){
			key="default";
		}
		if( bad_tags.contains(key)){
			// already had trouble with this tag.
			// assume bad for the rest of the request
			throw new SQLException("No Database connection");
		}
		SQLContext conn = map.get(key);
		if( conn != null && conn.getConnection().isClosed()){
			// looks like we got a closed connection from a pool
			// force remake
			conn = null;
			map.remove(key);
			if( tag == null ){
				connection_set=false;
			}
		}
		if (conn == null) {
			if(tag != null ||  ! connection_set ){
				// try to make one 
				try{
					conn = makeSQLContext(tag,props);
					if( conn != null ){
						if( tag == null ){
							connection_set=true;
						}
						map.put(key,conn);
					}else {
						bad_tags.add(key);
						error("Failed to make SQLContext");
					}
				}catch(SQLException e){
					bad_tags.add(key);
					error(e,"Failed to make SQLContext");
					throw e;
				}
			}else{
			   throw new SQLException("No Database connection");
			}
		}
		return conn;
	}
	/**
	 * Set the database Connection. Should be called from sub-class constructor
	 * 
	 * @param c
	 *            the Connection to use
	 * @throws SQLException
	 */
	protected synchronized void setSQLContext(SQLContext c) throws SQLException {
		connection_set=true;
		SQLContext conn = map.get("Default");
		if (conn != null) {
			// cannot call close method as subclasses add additional
			// side-effects
			try {
				conn.close();
			} catch (Exception e) {
				error(e,"Error closing old connection");
			}
		}
		map.put("Default", c);
	}
	/**
	 * default method for creating a {@link SQLContext} from InitParameters
	 * 
	 * This method is only invoked the first time an AppContext needs a Connection
	 * to reduce overhead in static pages.
	 * 
	 * @return A database Connection
	 * @throws SQLException
	 */
	protected SQLContext makeSQLContext(String tag,Properties props) throws SQLException {
		String suffix="";
		if( tag != null ){
			suffix="."+tag;
		}
		// Make a database connection
		String driver_name = props.getProperty("db_driver"+suffix,"").trim();
		try {
			// It is no longer necessary to force the driver class to be
			// registered in this way. The jar file should include 
			//META-INF/services/java.sql.Driver to mark it as a driver
			// or it can be added to the jdbc.drivers property
			// However if we do have a class name loading it should ensure
			// it is registered.
			if(driver_name!=null && ! driver_name.isEmpty()){
				Class.forName(driver_name);
			}
		} catch (Exception e) {
			error(e,"Could not load database driver: "+driver_name);
		}
		String name = props.getProperty("db_name"+suffix,"").trim();
		String user = props.getProperty("db_username"+suffix,"").trim();
		String pass = props.getProperty("db_password"+suffix,"").trim();
		String type = props.getProperty("db_type"+suffix,"").trim();
		
		FilteredProperties db_props = new FilteredProperties(props, "db_prop",tag);
		if( ! user.isEmpty()){
			db_props.setProperty("user", user);
		}
		if( ! pass.isEmpty()){
			db_props.setProperty("password", pass);
		}
		//System.out.println("ACTUAL "+name+" "+user+" "+pass+" "+type);
		Connection conn;
		if( name.length() == 0){
			error("No DB connection name");
			return null;
		}
		conn = java.sql.DriverManager.getConnection(name,db_props);
//		if( pass.length() == 0 || user.length() == 0){
//			// try a passwordless connection
//			conn = java.sql.DriverManager.getConnection(name);
//			
//		}else{
//			conn =java.sql.DriverManager.getConnection(name, user, pass);
//		}
		if( conn == null) {
			return null;
		}
		conn.setAutoCommit(true); // just in case
		if( type.contains(POSTGRESQL_TYPE) || driver_name.contains(POSTGRESQL_TYPE)){
			return new PostgresqlSQLContext(ctx,this,conn);
		}
		
		return new MysqlSQLContext(ctx,this,conn);
		

	}
	public AppContext getContext() {
		return ctx;
	}

	public synchronized void cleanup() {
		closed=true;
		closeRetainedClosables();
		if( map != null){
		for(SQLContext c : map.values()){
				if( c != null ){
					try {
						Connection connection = c.getConnection();
						if( connection != null ){
							// Make sure we don't leave a pooled connection in transaction mode.
							connection.setAutoCommit(true);
						}
						// This closes the nested connection
						c.close();
					} catch (Exception e) {
						error(e,"Error closing database connection");
					}
				}
		}
		map.clear();
		}
		connection_set=false;
	}
	/**
	 * 
	 */
	public void closeRetainedClosables() {
		if( closes != null) {
			if( ! closes.isEmpty()) {
				LoggerService serv = getContext().getService(LoggerService.class);
				if( serv != null) {
					Logger log = serv.getLogger(getClass());
					if( log != null ) {
						log.warn("Unclosed retained closables "+closes);
					}
				}
			}
			// set will be modified during loop so copy
			for(AutoCloseable c : closes.toArray(new AutoCloseable[closes.size()])) {
				try {
					c.close();
				} catch (Exception e) {
					error(e,"Error closing cached autoClosable");
				}
			}
			closes.clear();
			closes=null;
		}
	}

	public Class<DatabaseService> getType() {
		return DatabaseService.class;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#inTransaction()
	 */
	@Override
	public boolean inTransaction() {

		return in_transaction;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#startTransaction()
	 */
	@Override
	public void startTransaction() {
		if( in_transaction ){
			throw new ConsistencyError("nested calls to startTransaction");
		}
		if( TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				Connection connection = getSQLContext().getConnection();
				old_isolation_level=connection.getTransactionIsolation();
				if(old_isolation_level != getTargetIsolationLevel() && TRANSACTIONS_SERIALIZE_FEATURE.isEnabled(getContext())){
					connection.setTransactionIsolation(getTargetIsolationLevel());
				}
				connection.setAutoCommit(false);
				in_transaction=true;
				stage_count=0;
			} catch (SQLException e) {
				error(e,"Error starting transaction");
			}
		}
	}
	/**
	 * @return
	 */
	public int getTargetIsolationLevel() {
		return desired_isolation_level;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction() {
		if( in_transaction &&  TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				Connection connection = getSQLContext().getConnection();
				connection.rollback();
				in_transaction = ! connection.getAutoCommit();
			} catch (SQLException e) {
				error(e,"Error rolling back transaction");
			}
			// caches may hold data from transaction
			Repository.flushCaches(getContext());
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#commitTransaction()
	 */
	@Override
	public void commitTransaction() {
		if( in_transaction && TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				getSQLContext().getConnection().commit();
				stage_count++;
			} catch (SQLException e) {
				error(e,"Error committing transaction");
				// Make this fatal don't want to try continuing if this happens
				throw new TransactionError("Error committing transaction", e);
			}

		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#stopTranaction()
	 */
	@Override
	public void stopTransaction() {
		if( TRANSACTIONS_FEATURE.isEnabled(getContext())){
			if( ! in_transaction ){
				throw new ConsistencyError("orphan call to stopTransaction");
			}
			try {
				Connection connection = getSQLContext().getConnection();
				connection.commit();
				connection.setAutoCommit(true);
				if(old_isolation_level != getTargetIsolationLevel() && TRANSACTIONS_SERIALIZE_FEATURE.isEnabled(getContext())){
					connection.setTransactionIsolation(old_isolation_level);
				}
				in_transaction=false;
			} catch (SQLException e) {
				error(e,"Error ending transaction");
				throw new TransactionError("Error ending transaction", e);
			}
		}
		
	}
	/**
	 * Report an application error.
	 * Needs to handle the possiblity of the LoggerService not being present as
	 * we can't make it a pre-requisite here
	 * 
	 * @param errors
	 *            Text of error.
	 */
	
	final void error(String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors);
			}
		}
	}
	final void error(Throwable t,String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors,t);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#transactionStage()
	 */
	@Override
	public int transactionStage() {
		return stage_count;
	}
	
	@Override
	public void handleError(String message,SQLException e) throws DataFault {
		if( force_rollback && e instanceof SQLTransientException) {
			if( inTransaction() && transactionStage() == 0) {
				throw new ForceRollBack(message, e);
			}
		}
		throw new DataFault(message, e);
		
	}
	@Override
	public void logError(String message,SQLException e) {
		if( force_rollback && e instanceof SQLTransientException) {
			if( inTransaction() && transactionStage() == 0) {
				throw new ForceRollBack(message, e);
			}
		}
		error(e,message);
		
	}
	
	public static int parseLevel(String name) {
		
		if( name != null) {
			switch(name) {
			case "READ_UNCOMMITTED" : return Connection.TRANSACTION_READ_UNCOMMITTED;
			case "READ_COMITTED" : return Connection.TRANSACTION_READ_COMMITTED;
			case "REPEATABLE_READ" : return Connection.TRANSACTION_REPEATABLE_READ;
			case "SERIALIZABLE": return Connection.TRANSACTION_SERIALIZABLE;
			}
		}
		return Connection.TRANSACTION_SERIALIZABLE;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.CloseRegistry#addClosable(java.lang.AutoCloseable)
	 */
	@Override
	public void addClosable(AutoCloseable c) {
		if( closes == null) {
			closes = new HashSet<>();
		}
		closes.add(c);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.CloseRegistry#removeClosable(java.lang.AutoCloseable)
	 */
	@Override
	public void removeClosable(AutoCloseable c) {
		if( closes != null ) {
			closes.remove(c);
		}
		
	}
}