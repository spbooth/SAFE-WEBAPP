//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.apps.Command;
import uk.ac.ed.epcc.webapp.apps.CommandLauncher;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;

/** A {@link Command} to upgrade the current database.
 * @author spb
 *
 */
public class DatabaseUpgrader extends Object implements Command {
	
	/**
	 * @param conn
	 */
	public DatabaseUpgrader(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}
	Set<String> seen = new HashSet<String>();
	
	private void process(Connection c,Statement stmt,String name) throws SQLException{
		if( seen.contains(name)){
			return;
		}
		System.out.println("processing "+name);
		// ensure table created
		DataObjectFactory fac = getContext().makeObject(DataObjectFactory.class,name);
		
		Repository res = Repository.getInstance(getContext(), name);
		String table_name =res.getTable();
		System.out.println("table is "+table_name);
		System.out.println("upgrade "+table_name+" to InnoDB");
		stmt.executeUpdate("ALTER TABLE "+table_name+" ENGINE=InnoDB");
		seen.add(name); // mark upgraded
		// process referenced fields before index
		for( String field : res.getFields()){
			FieldInfo info = res.getInfo(field);
			if( info.isReference()){
				String ref = info.getReferencedTable();
				if( ! ref.equals(name)){
					process(c,stmt,ref);
				}
			}
		}
		System.out.println("finish upgrade to "+table_name);
		
		
		System.out.println("upgrade to utf8");
		stmt.executeUpdate("ALTER TABLE "+table_name+" CHARACTER SET utf8 COLLATE utf8_unicode_ci");
		stmt.executeUpdate("ALTER TABLE "+table_name+" CONVERT TO  CHARACTER SET utf8 COLLATE utf8_unicode_ci");
		for( String field : res.getFields()){
			FieldInfo info = res.getInfo(field);
			if( info.isReference()){
				System.out.println("add foreign key "+name+"."+field);
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE ");
				res.addTable(query, true);
				String ref = info.getReferencedTable();
				query.append(" ADD FOREIGN KEY ");
				query.append(field);
				query.append("_ref_key (");
				info.addName(query, false, true);
				query.append(") REFERENCES ");		
			
				query.append(Repository.getForeignKeyDescriptor(getContext(), ref, true));
				System.out.println(query.toString());
				stmt.executeUpdate(query.toString());
			}else if( info.isNumeric()){
				System.out.println(info.getName(true)+" not index?");
			}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#run(java.util.LinkedList)
	 */
	@Override
	public void run(LinkedList<String> args) {
		
		try{
			DatabaseService db = getContext().getService(DatabaseService.class);

			SQLContext sqlContext = db.getSQLContext();
			Connection c = sqlContext.getConnection();
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SHOW TABLES");
			Statement s2 = c.createStatement();
			while( rs.next()){
				String name = rs.getString(1);
		
					System.out.println(name);
					process(c,s2,name);
				
			}

		}catch(Throwable t){
			t.printStackTrace(System.err);
			CommandLauncher.die(t);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#description()
	 */
	@Override
	public String description() {
		return "Upgrade a database to INNODB with foreign keys";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#help()
	 */
	@Override
	public String help() {
		return "";
	}
	
	public static void main(String args[]){
		AppContext c = new AppContext();
		CommandLauncher launcher = new CommandLauncher(c);
		launcher.run(DatabaseUpgrader.class, args);
	}

}