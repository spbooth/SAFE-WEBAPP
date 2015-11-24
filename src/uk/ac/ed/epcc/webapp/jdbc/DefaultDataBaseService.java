// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.Version;
import uk.ac.ed.epcc.webapp.config.ConfigService;
/** Default implementation of the {@link DatabaseService}
 * 
 * This gets connection parameters from the {@link ConfigService} but this is only queried
 * at the point where a database connection is required.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DefaultDataBaseService.java,v 1.12 2015/07/24 15:09:06 spb Exp $")
@PreRequisiteService(ConfigService.class)
public class DefaultDataBaseService implements DatabaseService {
	public static final Feature TRANSACTIONS_FEATURE = new Feature("database_transactions",true,"Database transactions are supproted");

	protected static final String POSTGRESQL_TYPE = "postgres";
	private static final String MYSQL_TYPE = "mysql";
	private AppContext ctx;
	// The database connection:
	private Map<String,SQLContext> map = new HashMap<String, SQLContext>();
	private Set<String> bad_tags = new HashSet<String>();
	// Have we already attempted to set the default connection
	// we remember this so that we we don't keep attempting on fail
    private boolean connection_set=false;
	
    public DefaultDataBaseService(AppContext ctx){
    	this.ctx=ctx;
    }
    public final SQLContext getSQLContext() throws SQLException {
    	return getSQLContext(null);
    }
	public final SQLContext getSQLContext(String tag) throws SQLException {
		return getSQLContext(tag,ctx.getService(ConfigService.class).getServiceProperties());
	}
	/**
	 * returns a database connection for the application based on a set of Config Proeprtis
	 * 
	 * @return A {@link SQLContext}
	 * @throws SQLException
	 */
	public synchronized  SQLContext getSQLContext(String tag,Properties props) throws SQLException {
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
					}
				}catch(SQLException e){
					bad_tags.add(key);
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
				getContext().error(e,"Error closing old connection");
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
			
			if(driver_name==null || driver_name.trim().length()==0){
				ctx.error("No database driver defined");
				return null;
			}
			Class.forName(driver_name);
		} catch (Exception e) {
			ctx.error(e,"Could not load database driver: "+driver_name);
			return null;
		}
		String name = props.getProperty("db_name"+suffix,"").trim();
		String user = props.getProperty("db_username"+suffix,"").trim();
		String pass = props.getProperty("db_password"+suffix,"").trim();
		String type = props.getProperty("db_type"+suffix,"").trim();
		Connection conn;
		if( name.length() == 0 || user.length() == 0){
			// try a passwordless connection
			conn = java.sql.DriverManager.getConnection(name);
			
		}else{
			conn =java.sql.DriverManager.getConnection(name, user, pass);
		}
		if( type.contains(POSTGRESQL_TYPE) || driver_name.contains(POSTGRESQL_TYPE)){
			return new PostgresqlSQLContext(ctx,conn);
		}
		
		return new MysqlSQLContext(ctx,conn);
		

	}
	public AppContext getContext() {
		return ctx;
	}

	public synchronized void cleanup() {
		if( map != null){
		for(SQLContext c : map.values()){
				if( c != null ){
					try {
						c.close();
					} catch (Exception e) {
						getContext().error(e,"Error closing database connection");
					}
				}
		}
		map.clear();
		}
		connection_set=false;
	}

	public Class<DatabaseService> getType() {
		return DatabaseService.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#startTransaction()
	 */
	@Override
	public void startTransaction() {
		if( TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				getSQLContext().getConnection().setAutoCommit(false);
			} catch (SQLException e) {
				getContext().error(e,"Error starting transaction");
			}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction() {
		if( TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				getSQLContext().getConnection().rollback();;
			} catch (SQLException e) {
				getContext().error(e,"Error rolling back transaction");
			}

		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#commitTransaction()
	 */
	@Override
	public void commitTransaction() {
		if( TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				getSQLContext().getConnection().commit();
			} catch (SQLException e) {
				getContext().error(e,"Error committing transaction");
			}

		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#stopTranaction()
	 */
	@Override
	public void stopTranaction() {
		if( TRANSACTIONS_FEATURE.isEnabled(getContext())){
			try {
				getSQLContext().getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				getContext().error(e,"Error ending transaction");
			}
		}

	}

	
}