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
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.sql.*;
import java.util.*;

import uk.ac.ed.epcc.webapp.*;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** Handler class for database level (as opposed to table level)
 * operations
 * 
 * Currently this implementation is tied to MySQL. Make this class abstract and introduce a sub-type
 * to work with other DB implementations.
 * 
 * @author spb
 *
 */


public class DataBaseHandlerService implements Contexed, AppContextService<DataBaseHandlerService>{
	public static final Feature CLEAR_DATABASE = new Feature("clear_database", false, "Is the clear database feature enabled");
	public static final Feature COMMIT_ON_CREATE = new Feature("database.commit_on_create_table", false, "Add explicit commit on table modify (will be effective commit anyway)");
	
	// Hook for tests if this attribute is in the AppContext then properties are not added
	public static final String NO_PROPS_ATTR = "NoPropertiesAdded";
    private AppContext conn;
    public DataBaseHandlerService(AppContext c){
    	conn=c;
    }
    
    public AppContext getContext(){
    	return conn;
    }
    public boolean tableExists(String name){
    	try {
			SQLContext sql = conn.getService(DatabaseService.class).getSQLContext();
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM ");
			sql.quote(sb, name);
			sb.append(" WHERE 1=0");
			try(Statement stmt = sql.getConnection().createStatement();
					ResultSet rs = stmt.executeQuery(sb.toString())){
				rs.getMetaData();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
    }
    /** Get a Set of all known table names.
     * 
     * @return Set<String>
     * @throws DataException
     */
	public Set<String> getTables() throws DataException{
		DatabaseService service = conn.getService(DatabaseService.class);
    	try{
    	Set<String> result = new HashSet<>();
    	
		Connection c = service.getSQLContext().getConnection();
    	DatabaseMetaData md = c.getMetaData();
    	try(ResultSet rs = md.getTables(null, null, null, new String[] {"TABLE"})){
    		if( rs.first()){
    			do{
    				String name = rs.getString("TABLE_NAME");
    				result.add(name);
    			}while(rs.next());
    		}
    	}
    	return result;
    	}catch(SQLException e){
    		service.handleError("Error getting table names",e);
    		return null; // acutally unreachable
    	}
    }
  
	/** Create a database table based on a {@link TableSpecification}
	 * 
	 * The {@link TableSpecification} can be augmented by properties
	 * starting with <b>create_table.<i>table-name</i></b> using
	 * {@link TableSpecification#setFromParameters(AppContext, String, java.util.Map)}.
	 * 
	 * In addition fields can be renamed by setting <b>create_table.rename_field.<i>table</i>.<i>field</i>=<i>new-field</i></b>
	 * This is usually only necessary for unit tests and will have to be combined with <b>rename.<i>new-field</i>=<i>field</i></b>
	 * so the {@link Repository} maps the field back to the expected name. 
	 * 
	 * 
	 * @param name
	 * @param orig
	 * @throws DataFault
	 */
    public void createTable(String name, TableSpecification orig) throws DataFault{
    	TableSpecification s = new TableSpecification(orig);
    	// look for config overrides to specification
    	String prefix ="create_table."+name+".";
    	s.setFromParameters(conn,prefix, conn.getInitParameters(prefix));
    	Logger log = getLogger();
    	String text="unknown";
    	DatabaseService db_service = conn.getService(DatabaseService.class);
    	try{
    		
    		
			SQLContext c = db_service.getSQLContext();
			if( c == null){
				return;
			}
			LinkedList<Object> args = new LinkedList<>();	
			 
			text = createTableText(name, s, c, args);
    		// This is overly noisy in junit tests
			//log.debug("Creating table using "+text);
			try(PreparedStatement stmt = c.getConnection().prepareStatement(text)){
				for( int i=0; i< args.size(); i++){
					Object x = args.get(i);
					Repository.setObject(stmt,i+1, x);
				}
				stmt.executeUpdate();
			}
    		//TODO have SQLContext do this as some may use foreign keys
			if( ! conn.hasAttribute(NO_PROPS_ATTR)) {
				ConfigService serv = conn.getService(ConfigService.class);
				Properties prop = serv.getServiceProperties();
				for(String field_name : s.getFieldNames()){
					FieldType type = s.getField(field_name);
					if( type instanceof ReferenceFieldType){
						try{
							
							String prop_name = "reference."+name+"."+field_name;
							if( prop.getProperty(prop_name) == null ) {
								// don't replace an existing value
								serv.setProperty(prop_name, Repository.TableToTag(conn,((ReferenceFieldType)type).getRemoteTable()));
							}
						}catch(UnsupportedOperationException e){
							log.debug("set property not supported",e);
							break;
						}
					}
				}
			}
    	}catch(SQLException e) {
    		db_service.handleError("Failed to create table using "+text,e);
    	}catch(Exception e){
    		log.error("Error creating table",e);
    		throw new DataFault("Cannot create table "+name,e);
    	}
    }

	/** produce the SQL to create a table.
	 * @param name
	 * @param s
	 * @param c
	 * @param args
	 * @return
	 */
    public String createTableText(String name, TableSpecification s,
			SQLContext c, List<Object> args) {
    	return createTableText(false, name, s, c, args);
    }
	public String createTableText(boolean check_exists,String name, TableSpecification s,
			SQLContext c, List<Object> args) {
		StringBuilder sb = new StringBuilder();
		FieldTypeVisitor vis = c.getCreateVisitor(name,sb,args);
		sb.append("CREATE TABLE ");
		if( check_exists){
			sb.append(" IF NOT EXISTS ");
		}
		c.quote(sb, name);
		sb.append(" ( ");
		c.quote(sb,s.getPrimaryKey());
		vis.visitAutoIncrement();
		sb.append(",\n");
		for(String s2: s.getFieldNames()){
			String field_name = rename(name, s2);
			FieldType field = s.getField(s2);
			if( ! ( field instanceof PlaceHolderFieldType)){
				c.quote(sb,field_name);
				sb.append(" ");
				field.accept(vis);
				sb.append(",\n");
			}
		}
		sb.append("PRIMARY KEY (");
		c.quote(sb,s.getPrimaryKey());
		sb.append(")");
		for(Iterator<IndexType> it=s.getIndexes(); it.hasNext();){
			
			IndexType i1 = it.next();
			if( vis.useIndex(i1)){
				sb.append(",\n");
				i1.accept(n -> rename(name,n),vis);
			}
		}
		for(String s1 : s.getFieldNames()){
			FieldType f = s.getField(s1);
			if( f instanceof ReferenceFieldType){
				// also need own seperators
				vis.visitForeignKey(s1,",\n",(ReferenceFieldType)f);
			}
		}
		sb.append(")");
 		vis.additions(true);
 		String text=sb.toString();
		return text;
	}

	protected String rename(String name, String s2) {
		return getContext().getInitParameter("create_table.rename_field."+name+"."+s2, s2);
	}
	public void updateTable(Repository res, TableSpecification orig) throws DataFault{
    	TableSpecification s = new TableSpecification(orig);
    	DatabaseService service = conn.getService(DatabaseService.class);
    	try{
    		
    		if( COMMIT_ON_CREATE.isEnabled(conn)) {
    			// table creation implicitly commits the transaction anyway
        		// this lets the db_server know a commit has taken place so the transaction count
        		// is correct.
    			service.commitTransaction(); 
    		}
    		Logger log = getLogger();
    		
			SQLContext c = service.getSQLContext();
			
			 LinkedList<Object> args = new LinkedList<>();	
			 
			 	String text = alterTableText(res, s, c, args);
    		// This is overly noisy in junit tests
    		//log.debug("Creating table using "+text);
			 	try(PreparedStatement stmt = c.getConnection().prepareStatement(text)){
			 		for( int i=0; i< args.size(); i++){
			 			Object x = args.get(i);
			 			stmt.setObject(i+1, x);
			 		}
			 		stmt.executeUpdate();
			 	}
    		Repository.reset(getContext(), res.getTag());
    	}catch(SQLException se) {
    		service.handleError("Cannot update table "+res.getTable(), se);
    	}catch(Exception e){
    		throw new DataFault("Cannot update table "+res.getTable(),e);
    	}
    }

	
	/** produce the SQL to create a table.
	 * @param res
	 * @param s
	 * @param c
	 * @param args
	 * @return
	 */
	public String alterTableText(Repository res, TableSpecification s,
			SQLContext c, List<Object> args) {
		StringBuilder sb = new StringBuilder();
		FieldTypeVisitor vis = c.getCreateVisitor(res.getParamTag(),sb,args);
		sb.append("ALTER TABLE ");
		res.addTable(sb, true);
		boolean seen=false;
		for(String s2: s.getFieldNames()){
			if( ! res.hasField(s2)){
				if( seen ){
					sb.append(",\n");
				}else{
					seen=true;
				}
				sb.append(" ADD ");
				c.quote(sb,s2);
				sb.append(" ");
				s.getField(s2).accept(vis);
			}
		}
		vis.additions(false);
 		String text=sb.toString();
		return text;
	}

	public String addFieldText(Repository res, String name, FieldType f,SQLContext c, List<Object> args) {
		StringBuilder query = new StringBuilder();
		FieldTypeVisitor vis = c.getCreateVisitor(res.getParamTag(),query,args);
		query.append("ALTER TABLE ");
		res.addTable(query, true);
		query.append(" ADD ");
		c.quote(query,name);
		query.append(" ");
		f.accept(vis);
		if( f instanceof ReferenceFieldType) {
			vis.visitForeignKey(name,", ADD " , (ReferenceFieldType)f);
		}
		vis.additions(false);
		return query.toString();
	}
	public String addFkText(Repository res, String name, ReferenceFieldType f,SQLContext c, List<Object> args) {
		StringBuilder query = new StringBuilder();
		FieldTypeVisitor vis = c.getCreateVisitor(res.getParamTag(),query,args);
		query.append("ALTER TABLE ");
		res.addTable(query, true);
		vis.visitForeignKey(name,"ADD " , f);
		vis.additions(false);
		return query.toString();
	}
	public void addField(Repository res, String name, FieldType f) throws DataFault {
		DatabaseService service = conn.getService(DatabaseService.class);
    	try{
    		
    		if( COMMIT_ON_CREATE.isEnabled(conn)) {
    			// table creation implicitly commits the transaction anyway
        		// this lets the db_server know a commit has taken place so the transaction count
        		// is correct.
    			service.commitTransaction(); 
    		}
    		Logger log = getLogger();
    		
			SQLContext c = service.getSQLContext();
			
			 LinkedList<Object> args = new LinkedList<>();	
			 
			 	String text = addFieldText(res, name,f, c, args);
    		// This is overly noisy in junit tests
    		//log.debug("Creating table using "+text);
			 	try(PreparedStatement stmt = c.getConnection().prepareStatement(text)){
			 		for( int i=0; i< args.size(); i++){
			 			Object x = args.get(i);
			 			Repository.setObject(stmt,i+1, x);
			 		}
			 		stmt.executeUpdate();
			 	}
    		Repository.reset(getContext(), res.getTag());
    	}catch(SQLException se) {
    		service.handleError("Cannot add field "+res.getTable(), se);
    	}catch(Exception e){
    		throw new DataFault("Cannot add field "+res.getTable(),e);
    	}
	}
	public void addFk(Repository res, String name, ReferenceFieldType f) throws DataFault {
		DatabaseService service = conn.getService(DatabaseService.class);
		try{

			if( COMMIT_ON_CREATE.isEnabled(conn)) {
				// table creation implicitly commits the transaction anyway
				// this lets the db_server know a commit has taken place so the transaction count
				// is correct.
				service.commitTransaction(); 
			}
			Logger log = getLogger();

			SQLContext c = service.getSQLContext();

			LinkedList<Object> args = new LinkedList<>();	

			String text = addFkText(res, name,f, c, args);
			try(PreparedStatement stmt = c.getConnection().prepareStatement(text)){
				for( int i=0; i< args.size(); i++){
					Object x = args.get(i);
					Repository.setObject(stmt,i+1, x);
				}
				stmt.executeUpdate();
			}
			Repository.reset(getContext(), res.getTag());
		}catch(SQLException se) {
			service.handleError("Cannot add field "+res.getTable(), se);
		}catch(Exception e){
			throw new DataFault("Cannot add field "+res.getTable(),e);
		}
	}
    public void deleteTable(String name) throws Exception{
    	DatabaseService service = conn.getService(DatabaseService.class);
    	try{
    		if( Repository.READ_ONLY_FEATURE.isEnabled(getContext())){
    			return;
    		}
    	
		SQLContext c = service.getSQLContext();
		
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE ");
		c.quote(sb, name);
		try(Statement stmt = c.getConnection().createStatement()){
			stmt.executeUpdate(sb.toString());
		}
    	}catch(SQLException e){
    		service.handleError("Cannot remove table "+name,e);
    	}
    }

    public void clearDatabase() throws Exception{
    	if( CLEAR_DATABASE.isEnabled(getContext())){
    		DatabaseService service = conn.getService(DatabaseService.class);
    		try{

    			
				SQLContext c = service.getSQLContext();
    			Connection connection = c.getConnection();
    			String db_name = connection.getCatalog();
    			try(Statement stmt = connection.createStatement()){
    				StringBuilder sb = new StringBuilder();
    				stmt.executeUpdate("DROP DATABASE "+db_name);
    				stmt.executeUpdate("CREATE DATABASE "+db_name+" CHARACTER SET utf8 COLLATE utf8_general_ci");
    			}
    			connection.setCatalog(db_name);
    		}catch(SQLException e){
    			service.handleError("Cannot clear database",e);
    		}
    	}
    }
	public void cleanup() {
		
	}

	public Class<? super DataBaseHandlerService> getType() {
		return DataBaseHandlerService.class;
	}

	
	
}