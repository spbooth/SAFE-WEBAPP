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
package uk.ac.ed.epcc.webapp.jdbc.config;

import java.sql.Connection;
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


/** {@link ConfigService} that adds additional properties from a database table.
 * 
 * This is a wrapper that decorates a previously installed {@link ConfigService}.
 * The name of the table to store the properties is taken from a property <b>database.properties</b>
 * from the nested service. If this property is not found then this service will just foward to the nested service.
 * 
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


public class DataBaseConfigService implements ConfigService {

    /**
	 * 
	 */
	private static final String DATABASE_PROPERTIES_TABLE_PROP = "database.properties";
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
    			
    				prop_table = props.getProperty(DATABASE_PROPERTIES_TABLE_PROP);
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
    			}else {
    				ctx.error("NO SQL Context when setting up DatabaseConfigService");
    			}
    		
    			
    		
    		}catch(Exception e){
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
   
	@Override
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

	@Override
	public Properties getServiceProperties() {
		if( forward ){
			// We are going to return incomplete results
			// so clear any cache at the top level
			return nested.getServiceProperties();
		}
		if( db_props == null ){
			
			setup();
		
			if( sql != null && prop_table != null ){
				boolean changed=false;
				db_props = new Properties(nested.getServiceProperties());
				AppContext conn = getContext();
				try {
					try {
					Connection connection = sql.getConnection();
					if( connection == null){
						conn.error("No database connection");
					}else{
						StringBuilder query = new StringBuilder();
						query.append("SELECT ");
						sql.quote(query,"Name");
						query.append(",");
						sql.quote(query,"Value");
						query.append(" FROM ");
						sql.quote(query,prop_table);
						try(Statement select = connection.createStatement();
								ResultSet res = select.executeQuery(query.toString())
								){
							while( res.next()){
								String value = res.getString(2);
								if(value != null && value.trim().length() > 0){
									db_props.setProperty(res.getString(1), value);
									changed=true;
								}
							}
						}
					}
					}catch(SQLException se) {
						sql.getService().handleError("Error getting properties", se);
					}
				} catch (Exception e) {
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

	@Override
	public AppContext getContext() {
		return ctx;
	}
	@Override
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
		
		if( sql != null && prop_table != null ){
			try{
//				LinkedList args = new LinkedList();
//				TableSpecification spec = new TableSpecification();
//				spec.setField("Name", new StringFieldType(false,null,255));
//				spec.setField("Value", new StringFieldType(false,null,255));
//				try {
//					spec.new Index("name_key", true, "Name");
//				} catch (InvalidArgument e) {
//					ctx.error(e,"Failed to create name key");
//				}
//				DataBaseHandlerService handler = ctx.getService(DataBaseHandlerService.class);
//				
//				StringBuilder create = new StringBuilder();
//				create.append(handler.createTableText(true,prop_table, spec, sql, args));
//				PreparedStatement p = sql.getConnection().prepareStatement(create.toString());
//				int pos=1;
//				for(Object o : args){
//					p.setObject(pos++, o);
//				}
//				p.executeUpdate();
				
				// first delete
				StringBuilder query = new StringBuilder();
				query.append("DELETE FROM ");
				sql.quote(query, prop_table);
				query.append(" WHERE ");
				sql.quote(query,"Name");
				query.append("=?");
				try(PreparedStatement del = sql.getConnection().prepareStatement(query.toString())){
					del.setString(1, name);
					del.executeUpdate();
				}
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
					try(PreparedStatement insert = sql.getConnection().prepareStatement(insert_query.toString())){
						insert.setString(1, name);
						insert.setString(2, value);
						insert.executeUpdate();
					}
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
	@Override
	public void cleanup() {
		nested.cleanup();
	}
	@Override
	public void addListener(ConfigServiceListener listener) {
		
		if( listeners == null ){
			listeners=new HashSet<>();
		}
		listeners.add(listener);
	}
	
	
	@Override
	public Class<ConfigService> getType() {
		return ConfigService.class;
	}

}