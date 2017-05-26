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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
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
			Statement stmt = sql.getConnection().createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM ");
			sql.quote(sb, name);
			sb.append(" WHERE 1=0");
			ResultSet rs = stmt.executeQuery(sb.toString());
			rs.getMetaData();
			stmt.close();
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
    	try{
    	Set<String> result = new HashSet<String>();
    	Connection c = conn.getService(DatabaseService.class).getSQLContext().getConnection();
    	DatabaseMetaData md = c.getMetaData();
    	ResultSet rs = md.getTables(null, null, null, null);
    	if( rs.first()){
    	  do{
    		String name = rs.getString("TABLE_NAME");
    		result.add(name);
    	  }while(rs.next());
    	}
    	
    	return result;
    	}catch(SQLException e){
    		throw new DataFault("Error getting table names",e);
    	}
    }
  
    public void createTable(String name, TableSpecification orig) throws DataFault{
    	TableSpecification s = new TableSpecification(orig);
    	// look for config overrides to specification
    	String prefix ="create_table."+name+".";
    	s.setFromParameters(prefix, conn.getInitParameters(prefix));
    	Logger log = conn.getService(LoggerService.class).getLogger(getClass());
    	String text="unknown";
    	try{
    		
        	
    	
    		SQLContext c = conn.getService(DatabaseService.class).getSQLContext();
			if( c == null){
				return;
			}
			LinkedList<Object> args = new LinkedList<Object>();	
			 
			text = createTableText(name, s, c, args);
    		// This is overly noisy in junit tests
    		//log.debug("Creating table using "+text);
    		PreparedStatement stmt = c.getConnection().prepareStatement(text);
    		for( int i=0; i< args.size(); i++){
    			Object x = args.get(i);
				stmt.setObject(i+1, x);
    		}
    		stmt.executeUpdate();
    		stmt.close();
    		//TODO have SQLContext do this as some may use foreign keys
    		ConfigService serv = conn.getService(ConfigService.class);
    		for(String field_name : s.getFieldNames()){
    			FieldType type = s.getField(field_name);
    			if( type instanceof ReferenceFieldType){
    				try{
    					serv.setProperty("reference."+name+"."+field_name, Repository.TableToTag(conn,((ReferenceFieldType)type).getRemoteTable()));
    				}catch(UnsupportedOperationException e){
    					log.debug("set property not supported",e);
    					break;
    				}
    			}
    		}
    	}catch(Exception e){
    		log.error("Failed to create table using "+text,e);
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
		FieldTypeVisitor vis = c.getCreateVisitor(sb,args);
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
			FieldType field = s.getField(s2);
			if( ! ( field instanceof PlaceHolderFieldType)){
				c.quote(sb,s2);
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
				i1.accept(vis);
			}
		}
		for(String s1 : s.getFieldNames()){
			FieldType f = s.getField(s1);
			if( f instanceof ReferenceFieldType){
				// also need own seperators
				vis.visitForeignKey(s1,(ReferenceFieldType)f);
			}
		}
		sb.append(")");
 		vis.additions(true);
 		String text=sb.toString();
		return text;
	}
	public void updateTable(Repository res, TableSpecification orig) throws DataFault{
    	TableSpecification s = new TableSpecification(orig);
    	try{
    		
        	
    		Logger log = conn.getService(LoggerService.class).getLogger(getClass());
    		SQLContext c = conn.getService(DatabaseService.class).getSQLContext();
			
			 LinkedList<Object> args = new LinkedList<Object>();	
			 
			 	String text = alterTableText(res, s, c, args);
    		// This is overly noisy in junit tests
    		//log.debug("Creating table using "+text);
    		PreparedStatement stmt = c.getConnection().prepareStatement(text);
    		for( int i=0; i< args.size(); i++){
    			Object x = args.get(i);
				stmt.setObject(i+1, x);
    		}
    		stmt.executeUpdate();
    		stmt.close();
    		Repository.reset(getContext(), res.getTag());
    	}catch(Exception e){
    		throw new DataFault("Cannot create table "+res.getTable(),e);
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
		FieldTypeVisitor vis = c.getCreateVisitor(sb,args);
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

    public void deleteTable(String name) throws Exception{
    	try{
    		if( Repository.READ_ONLY_FEATURE.isEnabled(getContext())){
    			return;
    		}
    	SQLContext c = conn.getService(DatabaseService.class).getSQLContext();
		Statement stmt = c.getConnection().createStatement();
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE ");
		c.quote(sb, name);
		
		stmt.executeUpdate(sb.toString());
		stmt.close();
    	}catch(SQLException e){
    		throw new DataFault("Cannot remove table "+name,e);
    	}
    }

    public void clearDatabase() throws Exception{
    	if( CLEAR_DATABASE.isEnabled(getContext())){
    		try{

    			SQLContext c = conn.getService(DatabaseService.class).getSQLContext();
    			Connection connection = c.getConnection();
    			String db_name = connection.getCatalog();
    			Statement stmt = connection.createStatement();
    			StringBuilder sb = new StringBuilder();
    			stmt.executeUpdate("DROP DATABASE "+db_name);
    			stmt.executeUpdate("CREATE DATABASE "+db_name+" CHARACTER SET utf8 COLLATE utf8_general_ci");

    			stmt.close();
    			connection.setCatalog(db_name);
    		}catch(SQLException e){
    			throw new DataFault("Cannot clear database",e);
    		}
    	}
    }
	public void cleanup() {
		
	}

	public Class<? super DataBaseHandlerService> getType() {
		return DataBaseHandlerService.class;
	}

	
	
}