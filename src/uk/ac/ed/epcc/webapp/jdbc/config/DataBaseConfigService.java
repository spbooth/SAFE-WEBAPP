// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.CachedConfigService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.ConfigServiceListener;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;


/** ConfigService that adds additional properties from a database table.
 * 
 * There is a nasty potential circular dependency where the {@link DatabaseService} needed to
 * query the database table may need the {@link ConfigService} and may end up calling back to this class.
 * during setup. This is particularly nasty if there is a {@link CachedConfigService} in the stack as we have to make sure that
 * the cache does not end up holding state from before the DB is read or from a transient database error state.
 * 
 * 
 * @author spb
 *
 */
@PreRequisiteService({ConfigService.class,DataBaseHandlerService.class,DatabaseService.class})
@uk.ac.ed.epcc.webapp.Version("$Id: DataBaseConfigService.java,v 1.9 2015/04/08 16:12:29 spb Exp $")

public class DataBaseConfigService implements ConfigService {

    public static final String VALUE = "Value";
	public static final String NAME = "Name";
	private SQLContext sql=null;
	private AppContext ctx;
    private final ConfigService nested;
    private Properties db_props=null;
    private String prop_table=null;
  
    private Set<ConfigServiceListener> listeners=null;
    private boolean forward=false; // forward requests to nested if we are in setup.
    public DataBaseConfigService(AppContext ctx) throws SQLException {
    	this.ctx=ctx;
    	this.nested=ctx.getService(ConfigService.class);
    	assert(this.nested!=null);
    }
    /** lazy evaluation of DB connection. Late evaluation allows additional
     * higher priority config services to be layered above this one to provide the
     * db connection parameters.
     * 
     */
    private synchronized void setup() {
    	ConfigService top = ctx.getService(ConfigService.class);

    	if( sql == null && forward==false ){
    		try{
    			// The DataBaseService will try to query config parameters
    			// and may recurse back to this service if the parameters are not set
    			// in a higher priority service.
    			// set forward to ensure it queries the nested
    			// service to generate the connection.
    			//
    			// This may trigger caching at a higher level which will be missing 
    			// the DB entries so we need to clear the cache after setup
    			// 
    			// If the setup fails for any reason then we leave forward on to 
    			// disable futher action by this service.
    				
    			forward=true;
    			Properties	props = top.getServiceProperties();
    			
    			DatabaseService db_service = ctx.getService(DatabaseService.class);
    			if( db_service == null ){
    				throw new ConfigError("No DB service in DataBaseConfigService setup");
    			}
				this.sql=db_service.getSQLContext(null,props);

    			if(this.sql != null ){
    			
    				prop_table = props.getProperty("database.properties");
    				// query the existing props bundle to avoid re-calling
    				boolean auto_create = DataObjectFactory.AUTO_CREATE_TABLES_FEATURE.isEnabled(props);
    				
    				if( auto_create ){
    					
    		    		if( prop_table != null ){
    		    			// These may also call config service.
    		    			// This may return null if the service is not already registered 
    		    			// because AppContext dis-allows nested service creation
    		    			DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
    		    			if( serv != null ){
    		    				if( ! serv.tableExists(prop_table)){

    		    					TableSpecification s = new TableSpecification();
    		    					s.setField(NAME, new StringFieldType(false,null,255));
    		    					s.setField(VALUE, new StringFieldType(false,null,255));

    		    					s.new Index("name_key", true, NAME);

    		    					serv.createTable(prop_table, s);

    		    				}
    		    			}
    		    		}
    		    	}
    			}
    		
    			
    		
    		}catch(Throwable e){
    			// setup has failed
    			// try to report and carry on. 
    			// However this will leave incomplete data in any upper caches.
    			ctx.error(e, "Error setting up property table");
    			// If something went wrong don't cache anything.
    			sql=null;
    		}finally{
    			forward=false;
    		}
    	}
    }
   
	public void clearServiceProperties() {
		if( forward ){
			// This is our own clean call from setup
			// so abort here 
			return;
		}
		db_props=null;
		
		sql=null;  // force re-setup (tests may have wiped table).
		nested.clearServiceProperties();
		notifyListeners();
	}

	public Properties getServiceProperties() {
		if( forward ){
			// We are going to return incomplete results
			// so clear any cache at the top level
			return nested.getServiceProperties();
		}
		if( db_props == null ){
			
			setup();
		
			
			if( prop_table != null ){
				boolean changed=false;
				db_props = new Properties(nested.getServiceProperties());
				AppContext conn = getContext();
				try {
					Statement select = sql.getConnection().createStatement();
					StringBuilder query = new StringBuilder();
					query.append("SELECT ");
					sql.quote(query,"Name");
					query.append(",");
					sql.quote(query,"Value");
					query.append(" FROM ");
					sql.quote(query,prop_table);
					ResultSet res = select.executeQuery(query.toString());
					while( res.next()){
						String value = res.getString(2);
						if(value != null && value.trim().length() > 0){
							db_props.setProperty(res.getString(1), value);
							changed=true;
						}
					}
					res.close();
					select.close();
				} catch (SQLException e) {
					conn.error(e,"Error reading property table");
					db_props= null; // DB props are required, this may be a transient error so try again later.
					ctx.getService(ConfigService.class).clearServiceProperties();
					return nested.getServiceProperties();
				}
				if( changed ){
					notifyListeners();
				}
			}else{
				return nested.getServiceProperties();
			}
			
		}
		return new Properties(db_props);
	}

	public AppContext getContext() {
		return ctx;
	}
	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		if( forward ){
			nested.setProperty(name, value);
		}
		if( db_props == null ){
			setup();
		}
		Properties props = getServiceProperties();
		String old_value = props.getProperty(name);
		if( old_value != null && old_value.equals(value)){
			return;
		}
		
		if( prop_table != null ){
			try{
				
				// first delete
				StringBuilder query = new StringBuilder();
				query.append("DELETE FROM ");
				sql.quote(query, prop_table);
				query.append(" WHERE ");
				sql.quote(query,"Name");
				query.append("=?");
				PreparedStatement del = sql.getConnection().prepareStatement(query.toString());
				del.setString(1, name);
				del.executeUpdate();
				if( db_props != null ){
					db_props.remove(name);
				}
				if( value != null && value.trim().length() > 0){
					StringBuilder insert_query = new StringBuilder();
					insert_query.append("INSERT INTO ");
					sql.quote(insert_query, prop_table);
					insert_query.append(" (");
					sql.quote(insert_query, NAME);
					insert_query.append(",");
					sql.quote(insert_query, VALUE);
					insert_query.append(") VALUES (?,?)");
					PreparedStatement insert = sql.getConnection().prepareStatement(insert_query.toString());
					insert.setString(1, name);
					insert.setString(2, value);
					insert.executeUpdate();
					if( db_props != null ){
						db_props.setProperty(name,value);
					}
				}
			}catch(Exception e){
				getContext().error(e,"Error setting database parameter");
			}
			notifyListeners();
		}
	}
	private void notifyListeners() {
		if( listeners != null ){
			for(ConfigServiceListener l : listeners){
				l.resetConfig();
			}
		}
	}
	public void cleanup() {
		nested.cleanup();
	}
	public void addListener(ConfigServiceListener listener) {
		
		if( listeners == null ){
			listeners=new HashSet<ConfigServiceListener>();
		}
		listeners.add(listener);
	}
	
	
	public Class<ConfigService> getType() {
		return ConfigService.class;
	}

}