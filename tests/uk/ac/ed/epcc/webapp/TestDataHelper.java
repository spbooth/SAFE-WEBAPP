//| Copyright - The University of Edinburgh 2015                            |
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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;



import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

/**
 * The TestDataHelper is a utility class for testing against a database.
 * 
 * If you have an application, for example a webapp-based application
 * which you want to test, but a test required the database to be in a
 * certain state for the test to pass then this class maybe of use.
 * 
 * It operates on a simply premise, that you have a second database,
 * called the data sets database,  very similar to the database
 * which your application uses, called the test database, but which
 * contains all the test data, grouped in set called test data sets.
 * The name of the tesdata database should be defined the
 * test.properties file. For example,
 * 
 * testdata.datasets_database=webapp_testdata
 * 
 * Below is an example of which shows that the test called
 * 'testMakePerson' requires on table called 'person' to be
 * identical to the test data table 'person', and contain the data
 * set 'Bob'.
 * 
 * +----------------+---------------+-------------+------------------+
 * | TestName       | TestTableName | DataSetName | DataSetTableName |
 * +----------------+---------------+-------------+------------------+
 * | testMakePerson | person        | Bob         | person           |
 * +----------------+---------------+-------------+------------------+
 * 
 * The at the start of the test 'testMakePerson', you'll see a line like
 * 
 * TestDataHelper.loadDataSetsForTest('testMakePerson');
 * 
 * which loads the data set.
 * 
 * The test database, defined by the testdata.test_database property
 * in the test.properties file
 * 
 * testdata.test_database=webapp
 * 
 * When a data set is loaded the test database is drop and recreated.
 * This is very dangerous as you are completely destroying a database.
 * For this reason there is a restriction which required that the
 * database be a local one.
 * 
 * Before you can run a test which uses the TestDataHelper you'll need to
 * load up the data sets database. Below is an example of loading a data
 * sets database from within within mysql:
 * 
 * mysql> use webapp_testdata;
 * Database changed
 * mysql> source WEB-INF\tests\test_data.sql
 * 
 * It is also possible to use the TestDataHelper to define new data
 * sets when you developing tests. The following line of code can be added
 * to your test temporarily during development to take a snapshot of the
 * test database and save the tables and datasets to the data sets database.
 * The name of the data set in this example is "Eddie".
 * 
 * TestDataHelper.saveDataSetsForTest(getName(), "Eddie");
 * 
 * You need finer control over the data set as it is saved, for
 * example, to specify which tables are saved and what they are saved as.
 * Below show how the test data table 'person' is saved to the data
 * sets database as 'person.1', and it's content are tagged as
 * the dataset 'eddie'.
 * 
 * TestDataHelper.saveDataSet("person", "Eddie", "person.1");
 * 
 * When you've extended the data sets database it need to be saved to
 * the file system for other user of the test harness. 'mysqldump' could
 * be used for this. For example from the commandline:
 * 
 * $ mysqldump -u root -p webapp_testdata > WEB-INF/tests/test_data.sql
 * 
 * TODO Check for and handle the a table name clash.
 * If there is a existing table in the data sets database which
 * has a different schema create a new table definition with a clever name.
 * 
 * TODO Smart test data sets saving.
 * Know which data sets are already loaded and only add the new
 * data sets data to the tables, but store all the required data sets in
 * the test to data sets mapping table.
 */
public class TestDataHelper {

	/** The Constant DATABASE_PASSWORD_PROPERTY. */
	private static final String DATABASE_PASSWORD_PROPERTY = "testdata.password";

	/** The Constant DATABASE_USERNAME_PROPERTY. */
	private static final String DATABASE_USERNAME_PROPERTY = "testdata.username";

	/** The Constant TEST_DATABASE_NAME_PROPERTY. */
	private static final String TEST_DATABASE_NAME_PROPERTY = "testdata.test_database";
	
	/** The Constant DATASETS_DATABASE_NAME_PROPERTY. */
	private static final String DATASETS_DATABASE_NAME_PROPERTY = "testdata.datasets_database";
	
	private static final String DATASET_DRIVER_PROPERTY = "testdata.driver";
	/** The Constant MAPPING_TABLE. */
	private static final String MAPPING_TABLE = "TestDataSets";
	
	/**
	 * The Class DatabaseHelper is a utilty class used internally in
	 * TestDataHelper to manage the test database and the datasets database.
	 */
	protected static class DatabaseHelper {

		/** The database instance name. */
		private String mInstance; 
		
		/** The database's name. */
		private String mName; 
		
		/** The database's connection. */
		private Connection mConnection;

		/**
		 * Instantiates a new database helper.
		 * 
		 * @param hostName the host name
		 * @param portNumber the port number
		 * @param databaseName the database name
		 * @param userName the user name
		 * @param password the password
		 * 
		 * @throws SQLException the SQL exception
		 */
		public DatabaseHelper(String hostName, String portNumber,
				String databaseName, String userName, String password)
		throws SQLException {
			this("jdbc:mysql://" + hostName + ":" + portNumber+ "/", databaseName, userName, password);

		}
		
		/**
		 * Instantiates a new database helper.
		 * 
		 * @param databaseInstance the database instance
		 * @param databaseName the database name
		 * @param userName the user name
		 * @param password the password
		 * 
		 * @throws SQLException the SQL exception
		 */
		public DatabaseHelper(String databaseInstance, String databaseName, 
				String userName, String password) throws SQLException {
			setInstance(databaseInstance);
			setName(databaseName);
			setConnection(DriverManager.getConnection(databaseInstance+databaseName, userName,
					password));

		}

		/**
		 * Instantiates a new database helper.
		 * 
		 * @param databaseName the database name
		 * @param userName the user name
		 * @param password the password
		 * 
		 * @throws SQLException the SQL exception
		 */
		public DatabaseHelper(String databaseName, String userName,
				String password) throws SQLException {
			this(databaseName.substring(0, databaseName.lastIndexOf('/')),
				 databaseName.substring(databaseName.lastIndexOf('/') + 1,databaseName.length()),
				 userName, password);			

		}

		/**
		 * Instantiates a new database helper.
		 * 
		 * @param connection the connection
		 * 
		 * @throws SQLException the SQL exception
		 */
		public DatabaseHelper(Connection connection) throws SQLException {
			setConnection(connection);

		}

		/**
		 * Gets the colums names for a table.
		 * 
		 * @param tableName the table name
		 * 
		 * @return the colums names for a table
		 * 
		 * @throws SQLException the SQL exception
		 */
		public String[] getColumsForTable(String tableName) throws SQLException {
			DatabaseMetaData databaseMetaData = getConnection().getMetaData();
			ResultSet rs = databaseMetaData.getColumns(getConnection()
					.getCatalog(), getName(), tableName, "%");
			// Get the number of columns...
			rs.last();
			int numberOfColumns = rs.getRow();
			// Have to put back the row pointer to where it was initially
			rs.first();

			String[] columsNames = new String[numberOfColumns];
			for (int i = 0; i < numberOfColumns; i++) {
				columsNames[i] = rs.getString(4);
				rs.next();
			}
			return columsNames;
		}

		/**
		 * Gets the tables names for a database.
		 * 
		 * @return the tables names for a database
		 * 
		 * @throws SQLException the SQL exception
		 */
		public String[] getTablesForDatabase() throws SQLException {
			DatabaseMetaData databaseMetaData = getConnection().getMetaData();
			ResultSet rs = databaseMetaData.getTables(getConnection()
					.getCatalog(), getName(), "%", null);

			// Get the number of tables...
			rs.last();
			int numberOfTables = rs.getRow();
			// Have to put back the row pointer to where it was initially
			rs.first();

			String[] tableNames = new String[numberOfTables];
			for (int i = 0; i < numberOfTables; i++) {
				tableNames[i] = rs.getString("TABLE_NAME");
				rs.next();
			}
			return tableNames;
		}

		/**
		 * Gets the primary keys for a table.
		 * 
		 * @param tableName the table name
		 * 
		 * @return the primary keys
		 * 
		 * @throws SQLException the SQL exception
		 */
		public String[] getPrimaryKeys(String tableName) throws SQLException {
			DatabaseMetaData databaseMetaData = getConnection().getMetaData();
			ResultSet rs = databaseMetaData.getPrimaryKeys(getConnection()
					.getCatalog(), getName(), tableName);

			// Get the number of tables...
			rs.last();
			int numberOfPrimaryKeys = rs.getRow();
			// Have to put back the row pointer to where it was initially
			rs.first();

			String[] primarykeys = new String[numberOfPrimaryKeys];
			for (int i = 0; i < numberOfPrimaryKeys; i++) {
				primarykeys[i] = rs.getString("COLUMN_NAME");
				rs.next();
			}
			return primarykeys;
		}
		
		/**
		 * Gets the unique keys.
		 * 
		 * @param tableName the table name
		 * 
		 * @return the unique keys
		 * 
		 * @throws SQLException the SQL exception
		 */
		public HashMap<String, ArrayList<String>> getUniqueKeys(String tableName) throws SQLException 
		{
			DatabaseMetaData databaseMetaData = getConnection().getMetaData();
			// (nix) This is quite a wried one. There's a bug with my jdbc driver whereby
			// if I don't call getPrimaryKeys() before getIndexInfo(), getIndexInfo() 
			// returns nothing.
			ResultSet rs = databaseMetaData.getPrimaryKeys(getConnection()
					.getCatalog(), getName(), tableName);			
			rs = databaseMetaData.getIndexInfo(
					getConnection().getCatalog(), getName(), tableName, true, false);

			// Get the number of tables...
			rs.last();
			int numberOfRows = rs.getRow();
			// Have to put back the row pointer to where it was initially
			rs.first();

			HashMap<String, ArrayList<String>> uniqueKeys = 
				new HashMap<String, ArrayList<String>>();	
		
			for (int i = 0; i < numberOfRows; i++) {
				String index = rs.getString("INDEX_NAME");
				String column = rs.getString("COLUMN_NAME");
				if (uniqueKeys.containsKey(index)) {
					ArrayList<String> columns = uniqueKeys.get(index);
					columns.add(column);
				} else {
					ArrayList<String> columns = new ArrayList<String>();
					columns.add(column);
					uniqueKeys.put(index,columns);
				}
				rs.next();
			}
			return uniqueKeys;
		}
		

		/**
		 * Gets the SQL column definition.
		 * 
		 * @param tableName the table name
		 * @param columnName the column name
		 * 
		 * @return the column definition
		 * 
		 * @throws SQLException the SQL exception
		 */
		public String getColumnDefinition(String tableName, String columnName) throws SQLException
		{
			DatabaseMetaData databaseMetaData = getConnection().getMetaData();
			ResultSet rs = databaseMetaData.getColumns(getConnection()
					.getCatalog(), getName(), tableName, columnName);
			if (rs.next()) {
				String type = rs.getString("TYPE_NAME");
				int size = rs.getInt("COLUMN_SIZE");
				boolean nullable = rs.getBoolean("NULLABLE");
				String defaultValue = rs.getString("COLUMN_DEF");

				String columnDefinition = type
						+ (size > 0 ? "(" + size + ") " : "")
						+ (!nullable ? "NOT NULL " : "")
						+ ((defaultValue != null && defaultValue.trim().length() > 0) 
								? "DEFAULT "+ defaultValue + " " : "");
				return columnDefinition;
			} else {
				return null;
			}
		}

		/**
		 * removes all the data for all the database's tables, but does not
		 * delete the tables.
		 * 
		 * @throws SQLException the SQL exception
		 */
		public void clearAllTables() throws SQLException {
			String[] tableNames = getTablesForDatabase();
			for (int i = 0; i < tableNames.length; i++) {
				String query = "TRUNCATE TABLE `" + tableNames[i]+"`";
				Statement statement = getConnection().createStatement();
				int rowsUpdated = statement.executeUpdate(query);
				System.out.println("NDB Truncated " + rowsUpdated + " rows from "
						+ tableNames[i]);
			}
		}

		/**
		 * Drops the database.
		 * 
		 * @throws SQLException the SQL exception
		 */
		public void dropDatabase() throws SQLException {
			String query = "DROP DATABASE  " + getName();
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(query);
			System.out.println("NDB Dropped " + getName());

		}

		/**
		 * Recreates the database.
		 * 
		 * @throws SQLException the SQL exception
		 */
		public void createDatabase() throws SQLException {
			String query = "CREATE DATABASE  " + getName();
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(query);

			query = "USE " + getName();
			statement = getConnection().createStatement();
			statement.executeUpdate(query);
			System.out.println("NDB Created " + getName());
		}

		/**
		 * Makes a carbon copy of a table.
		 * 
		 * @param fromTable the from table
		 * @param tableName the table name
		 * 
		 * @throws SQLException the SQL exception
		 */
		public void duplicateTableFrom(String fromTable, String tableName) throws SQLException
		{
			String query = "CREATE TABLE  `" + tableName + "` LIKE " + fromTable +"";
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(query);
			System.out.println("NDB Duplicated " + tableName + " from " + fromTable);
		}

		/**
		 * Returns true if a table exists.
		 * 
		 * @return true, if exists
		 */
		public boolean databaseExists() {
			try {
				return !getName().equals("") && getName().equals(getConnection().getCatalog());
				
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
				
			}
			
		}
		
		/**
		 * Returns true if a table exists.
		 * 
		 * @param tableName the table name
		 * 
		 * @return true, if exists
		 * @throws SQLException 
		 * 
		 */
		public boolean tableExists(String tableName) throws SQLException {
				DatabaseMetaData databaseMetaData = getConnection().getMetaData();
				ResultSet rs;
					rs = databaseMetaData.getTables(getConnection()
							.getCatalog(), getName(), tableName, null);
				return (rs.next()) && tableName.equalsIgnoreCase(rs.getString(3));
			

		}
		
		/**
		 * Gets the table definition.
		 * 
		 * @param tableName the table name
		 * 
		 * @return the table definition
		 * 
		 * @throws SQLException the SQL exception
		 */
		public String getTableDefinition(String tableName) throws SQLException {
			String query = "SHOW CREATE TABLE `" + tableName+"`";
			Statement statement = getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query);
			rs.next();
			return rs.getString("Create Table");
			
		}

		/**
		 * Convert to test table to a data set table. This involves adding
		 * a column to store the DataSetName, and dropping any AUTO_INCREMENT
		 * definitions.
		 * 
		 * @param tableName the table name
		 * 
		 * @throws SQLException the SQL exception
		 */
		public void convertToTestTable(String tableName) throws SQLException {

			// Drop in the DataSetName column
			StringBuffer query =
				new StringBuffer("ALTER TABLE  `" + tableName + "` DROP COLUMN DataSetName");
			
			// Handle the primary and unique keys
			HashMap<String, ArrayList<String>> uniqueKeys = getUniqueKeys(tableName);
			for (String uniqueKey: uniqueKeys.keySet()) {	
				ArrayList<String> keys = uniqueKeys.get(uniqueKey);
				if (uniqueKey.equals("PRIMARY")) {					
					query.append(", DROP PRIMARY KEY");
					query.append(", ADD PRIMARY KEY (");
				} else {					
					query.append(", DROP KEY `"+uniqueKey+"`");
					query.append(", ADD UNIQUE KEY (");
				}
				for (int i = 0; i < keys.size(); i++) {
					String key = keys.get(i);
					if (!key.equals("DataSetName")) {
						query.append("`"+key+"`,");
					}
				}
				query.setCharAt(query.length()-1, ')');
			}
		
			// Set the primary key to be an auto_increment field
			String[] primaryKeys = getPrimaryKeys(tableName);	
			if (primaryKeys.length == 2) {				
				String primaryKey = primaryKeys[1];					
				String primaryKeyColumnDefinition = getColumnDefinition(
						tableName, primaryKey);
				query.append(", MODIFY COLUMN " + primaryKey + " "
						+ primaryKeyColumnDefinition + "AUTO_INCREMENT");
			}
			
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(query.toString());

		}

		/**
		 * Convert to data set table to a test table.  This involves removing
		 * a column to store the DataSetName, and adding AUTO_INCREMENT to the
		 * primary key column.
		 * 
		 * @param tableName the table name
		 * 
		 * @throws SQLException the SQL exception
		 */
		public void convertToDataSetTable(String tableName) throws SQLException {
			
			// Add in the DataSetName column
			StringBuffer query = 
				new StringBuffer("ALTER TABLE  `" + tableName +
						"` ADD COLUMN DataSetName varchar(32) NOT NULL FIRST");
			
			// Handle the primary and unique keys
			// Handle the primary and unique keys
			HashMap<String, ArrayList<String>> uniqueKeys = getUniqueKeys(tableName);
			for (String uniqueKey: uniqueKeys.keySet()) {	
				ArrayList<String> keys = uniqueKeys.get(uniqueKey);
				if (uniqueKey.equals("PRIMARY")) {					
					query.append(", DROP PRIMARY KEY");
					query.append(", ADD PRIMARY KEY (");
				} else {					
					query.append(", DROP KEY "+uniqueKey);
					query.append(", ADD UNIQUE KEY (");
				}
				for (int i = 0; i < keys.size(); i++) {
					String key = keys.get(i);
					query.append("`"+key+"`,");
				}
				query.append("`DataSetName`)");
				
			}
		
			// Remove primary keys AUTO_INCRUMENT
			String[] primaryKeys = getPrimaryKeys(tableName);	
			if (primaryKeys.length == 1) {				
				String primaryKey = primaryKeys[0];							
				String primaryKeyColumnDefinition = getColumnDefinition(
						tableName, primaryKey);
				query.append(", MODIFY COLUMN " + primaryKey + " "+ primaryKeyColumnDefinition);

			}
			
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(query.toString());
		}

		/**
		 * Sets the instance.
		 * 
		 * @param mInstance the new instance
		 */
		protected void setInstance(String mInstance) {
			this.mInstance = mInstance;
		}

		/**
		 * Gets the single instance of DatabaseHelper.
		 * 
		 * @return single instance of DatabaseHelper
		 */
		protected String getInstance() {
			return mInstance;
		}

		/**
		 * Sets the database's name.
		 * 
		 * @param name the name
		 */
		protected void setName(String name) {
			this.mName = name;
		}

		/**
		 * Gets the database's name.
		 * 
		 * @return the mName
		 */
		public String getName() {
			return mName;
		}
		
		/**
		 * Gets the database's full name.
		 * 
		 * @return the mName
		 */
		public String getFullName() {
			return mInstance+mName;
		}

		/**
		 * Sets the the database's connection.
		 * 
		 * @param connection the connection
		 */
		protected void setConnection(Connection connection) {
			this.mConnection = connection;
		}

		/**
		 * Gets the the database's connection.
		 * 
		 * @return the mConnection
		 */
		public Connection getConnection() {
			return mConnection;
		}

	}

	
	/** The test database. */
	protected static DatabaseHelper sTestDatabase;
	
	/** The data sets database. */
	protected static DatabaseHelper sDataSetsDatabase;

	/** The Constant sDatabaseInstance. */
	protected static final String sDatabaseInstance="jdbc:mysql://localhost/";
	
	public static boolean clearTestDatabase() {

		try {
			DatabaseHelper dataSetsDatabaseHelper = getDataSetsDatabaseHelper();
			if (dataSetsDatabaseHelper != null && dataSetsDatabaseHelper.databaseExists()) {

				//It would be nice if I checked if the same tables
				// were already loaded for the previous test, that we i could just truncate
				// the tables and re-copy over the data and i wouldn't have to drip the
				// database and re-create all the tables. A thought!
				// One of the problems though is comparing the tables between the two databases.
				// There's and extra row and different index between the so a
				// straight string compare won't work, sigh.

				DatabaseHelper testDatabaseHelper = getTestDatabase();
				if (testDatabaseHelper.databaseExists()) {
					// Check that it's not the same name as the data sets
					// database. (I did that I swear!))
					if (dataSetsDatabaseHelper.getName().equals(testDatabaseHelper.getName())) {
						return false;
						
					}
					testDatabaseHelper.dropDatabase();
					
				}
				testDatabaseHelper.createDatabase();
				return true;

			} else {
				System.out.println("Aborted droping test database, as the data sets database does not exist.");
				return false;

			}
		} catch (SQLException e) {
			System.out.println("Failed to drop test database:"+e.getMessage());
			return false;
			
		}

	}
		
	/**
	 * Load data sets for a test named testname.
	 * 
	 * @param testname the test to load data for.
	 * 
	 * @return true, if load data sets for test
	 * 
	 **/
	public static boolean loadDataSetsForTest(String testname) {		
		return loadDataSetsForTest(testname,true);
	}
	/** Load data sets for a test named testname.
	 * 
	 * @param testname the test to load data for.
	 * @param allow_empty value to return if no data sets found for this test
	 * @return rue, if load data sets for test
	 */
	public static boolean loadDataSetsForTest(String testname, boolean allow_empty) {		
		if (clearTestDatabase()) {
			
			try {
				System.out.println("Loading data for "+testname);
				String dataSetsQuery = new String(
						"SELECT TestTableName, DataSetName, DataSetTableName from "+getTestDataSetMappingTableName()+
						" WHERE TestName=?");
				// "pre-compile" SQL query
				PreparedStatement dataSetsStatement = getDataSetsDatabaseHelper().getConnection()
						.prepareStatement(dataSetsQuery);
	
				// set query variables
				dataSetsStatement.setString(1, testname);
	
				// execute it
				ResultSet rs = dataSetsStatement.executeQuery();
				boolean result=allow_empty;
				while (rs.next()) {
					// get results
					String testTableName = rs.getString(1);
					String dataSetName = rs.getString(2);
					String dataSetTableName = rs.getString(3);
					if (dataSetTableName != null) {
						loadDataSet(testTableName, dataSetTableName, dataSetName);
						
					} else {
						loadDataSet(testTableName, testTableName, dataSetName);
						
					}
					//save(testTableName,dataSetName);
					result=true;
				}
				return result;
			} catch (SQLException e) {
				System.out.println("Failed to load data set for test "+testname);
				return false;
			} catch (Exception e) {
				System.out.println("Failed to load data set for test "+testname);
				return false;
			}
			
		} else {
			return false;
			
		}
				
	}
	
	/**
	 * Load data set named 'dataSetName' from
	 * table 'dataSetTableName' to test table 'testTableName'.
	 * 
	 * @param testTableName the test table name
	 * @param dataSetName the data set name
	 * @param dataSetTableName the data set table name
	 * 
	 * @return true, if load data set
	 * 
	 */
	public static boolean loadDataSet(String testTableName, String dataSetTableName, String dataSetName) {
		try {
			// First make sure there a table defined
			if (!getTestDatabase().tableExists(testTableName)) {
				getTestDatabase().duplicateTableFrom(
						getDataSetsDatabaseHelper().getName() + "."
								+ dataSetTableName, testTableName);
				getTestDatabase().convertToTestTable(testTableName);
			}

			// would like to add a check here to see if the table definition is
			// valid. It's
			// not as easy as it looks as string compare on a
			// "SHOW CREATE TABLE"
			// wouldn't work.

			if (dataSetName != null) {
				String[] columsNames = getTestDatabase().getColumsForTable(
						testTableName);
				String query = "INSERT INTO " + getTestDatabase().getName()
						+ "." + testTableName + " SELECT ";
				for (int i = 0; i < columsNames.length - 1; i++) {
					query += "`" + columsNames[i] + "`, ";
				}
				query += "`" + columsNames[columsNames.length - 1] + "` ";
				query += "FROM " + getDataSetsDatabaseHelper().getName() + "."
						+ dataSetTableName + " " + "WHERE DataSetName='"
						+ dataSetName + "'";
				Statement statement = getTestDatabase().getConnection()
						.createStatement();
				int rowsUpdated = statement.executeUpdate(query);
				System.out.println("NDB Loaded " + rowsUpdated + " rows from "
						+ dataSetTableName + ", (" + dataSetName + ")");
			}
			return true;
			
		} catch (SQLException e) {
			System.out.println("Failed to load data set "+dataSetName+" from table " + testTableName);
			e.printStackTrace();
			return false;
			
		} catch (Exception e) {
			System.out.println("Failed to load data set "+dataSetName+" from table " + testTableName);
			e.printStackTrace();
			return false;
			
		}
		
	}
	
	/**
	 * Save data sets for a test named testname.
	 * 
	 * @param testname the test to save data for.
	 * @param dataSetName the data set to save
	 * @param tag 
	 * @param limit maximum number of entries from each table to copy
	 * @throws SQLException 
	 * 
	 */
	public static void saveDataSetsForTest(String testname, String dataSetName,String tag,int limit) throws SQLException {
		String tablesQuery = new String("SHOW TABLES IN "+getTestDatabase().getName());
		// "pre-compile" SQL query
		PreparedStatement tablesStatement = 
			getDataSetsDatabaseHelper().getConnection().prepareStatement(tablesQuery);
	
		// execute it
		ResultSet rs = tablesStatement.executeQuery();
		while (rs.next()) {
			// Save the table entries in the DataSets database table
			String tableName = rs.getString(1);		
			String dataSetTableName = tableName;
			if( tag != null){
				dataSetTableName=dataSetTableName+tag;
			}
			saveDataSet(tableName, dataSetTableName, dataSetName,null,limit);
			
			// Add an entry to the TESTS_DATA_SETS table for the DataSet.
			String updateTestsTableQuery = new String(
					"INSERT INTO "+getDataSetsDatabaseHelper().getName()+"."+getTestDataSetMappingTableName()+
							" VALUES ('"+testname+"', '"+tableName+"', '"+dataSetName+"', '"+dataSetTableName+"')");				
			PreparedStatement updateTestsTableStatement = getDataSetsDatabaseHelper().getConnection()
					.prepareStatement(updateTestsTableQuery);
			updateTestsTableStatement.execute();
			
		}
	}
		
	/**
	 * Save data set as 'dataSetName' from test table 'testTableName'
	 * to data set table 'dataSetTableName'.
	 * 
	 * @param testTableName the test table name
	 * @param dataSetName the data set name
	 * @param dataSetTableName the data set table name
	 * @throws SQLException 
	 * 
	 */
	public static void saveDataSet(String testTableName, String dataSetTableName, String dataSetName) throws SQLException {
		saveDataSet(testTableName, dataSetTableName, dataSetName,null,0);
	}
	public static void saveDataSet(String testTableName, String dataSetTableName, String dataSetName,String where,int limit) throws SQLException {
		// First make sure there a table defined		
		if (!getDataSetsDatabaseHelper().tableExists(dataSetTableName)) {
			getDataSetsDatabaseHelper().duplicateTableFrom(
					getTestDatabase().getName()+"."+testTableName, 
					dataSetTableName);
			getDataSetsDatabaseHelper().convertToDataSetTable(dataSetTableName);
		}
		
		// TOTO (nix) Add a check here to see if the table definition is valid. It's 
		// not as easy as it looks as string compare on a "SHOW CREATE TABLE" 
		// wouldn't work. 		
				
		if (dataSetName != null) {
			String[] columsNames = getTestDatabase().getColumsForTable(testTableName);
			String query = "INSERT INTO " + getDataSetsDatabaseHelper().getName() + "."+ dataSetTableName + 
				" SELECT  '"+dataSetName+"', ";
			for (int i = 0; i < columsNames.length - 1; i++) {
				query += "`" + columsNames[i] + "`, ";
			}
			query += "`" + columsNames[columsNames.length - 1] + "` ";
			query += "FROM " + getTestDatabase().getName() + "."+ testTableName + " ";
			if( where != null ){
				query += " WHERE "+where;
			}
			if( limit > 0 ){
				query += " LIMIT "+limit;
			}
			Statement statement = getTestDatabase().getConnection()
					.createStatement();
			int rowsUpdated = statement.executeUpdate(query);
			System.out.println("NDB Saved " + rowsUpdated + " rows from "
					+ testTableName + " to "+dataSetTableName+", (" + dataSetName+")");
		}
		
	}
	
	/**
	 * Save data sets for a test named 'testname' into the data set mapping table only.
	 * Use this API where you've already added you data sets for another test and
	 * simply want to re-use the data set for a further test.
	 * 
	 * @param testname the test to save data for.
	 * @param dataSetName the data set to save
	 * @throws SQLException 
	 * 
	 */
	public static void addDataSetMappingsForTest(String testname, String dataSetName) throws SQLException {
		String tablesQuery = new String("SHOW TABLES IN "+getTestDatabase().getName());
		// "pre-compile" SQL query
		PreparedStatement tablesStatement = 
			getDataSetsDatabaseHelper().getConnection().prepareStatement(tablesQuery);
	
		// execute it
		ResultSet rs = tablesStatement.executeQuery();
		while (rs.next()) {
			String testTableName = rs.getString(1);		
			String dataSetTableName = testTableName;			
			addDataSetMappingForTest(testname, testTableName, dataSetTableName, dataSetName);
			
		}
	}

	/**
	 * Adds the data set mapping for a test.
	 * 
	 * @param testname the testname
	 * @param dataSetName the data set name
	 * @param testTableName the test table name
	 * @param dataSetTableName the data set table name
	 * 
	 * @throws SQLException the SQL exception
	 */
	public static void addDataSetMappingForTest(String testname, String testTableName, String dataSetTableName, String dataSetName) 
		throws SQLException
	{
		
		// Add an entry to the TESTS_DATA_SETS table for the DataSet.
		String updateTestsTableQuery = new String(
				"INSERT INTO "+getDataSetsDatabaseHelper().getName()+"."+getTestDataSetMappingTableName()+
						" VALUES ('"+testname+"', '"+testTableName+"', '"+dataSetName+"', '"+dataSetTableName+"')");				
		PreparedStatement updateTestsTableStatement = getDataSetsDatabaseHelper().getConnection()
				.prepareStatement(updateTestsTableQuery);

		System.out.println("NDB Statement"+updateTestsTableQuery.toString());	
		updateTestsTableStatement.execute();
			
	}
	
	/**
	 * Removes the data set index for test.
	 * 
	 * @param testname the testname 
	 * @throws SQLException 
	 * 
	 */
	public static void removeDataSetMappingsForTest(String testname) throws SQLException {

		

		String dataSetsQuery = new String(
				"DELETE FROM "+ getTestDataSetMappingTableName()
						+ " WHERE TestName=?");
		
		// "pre-compile" SQL query
		PreparedStatement dataSetsStatement = getDataSetsDatabaseHelper()
				.getConnection().prepareStatement(dataSetsQuery);

		// set query variables
		dataSetsStatement.setString(1, testname);

		// execute it
		int numberOfDeletedEntries = dataSetsStatement.executeUpdate();
		
		System.out.println("NDB Deleted "+numberOfDeletedEntries+" data set entries form the data sets mapping table.");
	
	}
	
	/**
	 * Removes the data set for test.
	 * 
	 * @param testname the testname
	 * @throws SQLException 
	 *
	 */
	public static void removeDataSetsForTest(String testname) throws SQLException {
		
		String dataSetsQuery = new String(
				"SELECT DataSetTableName, DataSetName from "+getTestDataSetMappingTableName()+
				" WHERE TestName=?");
		// "pre-compile" SQL query
		PreparedStatement dataSetsStatement = getDataSetsDatabaseHelper().getConnection()
				.prepareStatement(dataSetsQuery);

		// set query variables
		dataSetsStatement.setString(1, testname);

		// execute it
		ResultSet rs = dataSetsStatement.executeQuery();
		while (rs.next()) {
			// get results
			String dataSetTableName = rs.getString(1);
			String dataSetName = rs.getString(2);
			removeDataSet(dataSetTableName, dataSetName);
				
					
		}		
		removeDataSetMappingsForTest(testname);
		
	}
	
	/**
	 * Removes the data set.
	 * @param dataSetTableName 
	 * 
	 * @param dataSetName the data set name
	 * @throws SQLException 
	 */
	public static void removeDataSet(String dataSetTableName, String dataSetName) throws SQLException {
		
		if (dataSetName != null) {
			
			String query = "DELETE FROM " + getDataSetsDatabaseHelper().getName() + "."+ dataSetTableName + 
				" WHERE DataSetName='"+dataSetName+"'";
			
			Statement statement = getTestDatabase().getConnection().createStatement();
			int rowsUpdated = statement.executeUpdate(query);
			System.out.println("NDB Deleted " + rowsUpdated + " rows from data sets database table "
					+ dataSetTableName + " (DataSet=" + dataSetName + ")");
		}
		
	}
	
	/**
	 * Removes the data set mapping for test.
	 * 
	 * @param testname the testname
	 * @param dataSetTableName the data set table name
	 * @param dataSetName the data set name
	 * 
	 * @throws SQLException the SQL exception
	 */
	public static void removeDataSetMappingForTest(String testname, String dataSetTableName, String dataSetName) 
		throws SQLException
	{
		

		String dataSetsQuery = new String(
				"DELETE FROM "+ getTestDataSetMappingTableName()+ 
				" WHERE TestName=?"+
				" AND DataSetName=?" +
				" AND DataSetTableName=?");
		
		// "pre-compile" SQL query
		PreparedStatement dataSetsStatement = getDataSetsDatabaseHelper()
				.getConnection().prepareStatement(dataSetsQuery);

		// set query variables
		dataSetsStatement.setString(1, testname);
		dataSetsStatement.setString(2, dataSetName);
		dataSetsStatement.setString(3, dataSetTableName);
		
		// execute it
		int numberOfDeletedEntries = dataSetsStatement.executeUpdate();
		
		System.out.println("NDB Deleted "+numberOfDeletedEntries+" data set entries form the data sets mapping table.");
		
	}
	
	/**
	 * Uses the contents of a file to populate a <code>Properties</code> object.
	 * 
	 * @param file the file to read
	 * 
	 * @return A <code>Properties</code> object containing all the properties 
	 * specified in the file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Properties readFileAsProperties(File file) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		return properties;
	}

	/**
	 * Read the contents of a file into a string.
	 * 
	 * @param file the file to read
	 * 
	 * @return the contents of the file into a string.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String readFileAsString(File file) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
		
	}
	
	
	
	
	/**
	 * Write the string to the file.
	 * 
	 * @param file the file to be written to
	 * @param output String to output
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeFile(File file, String output) throws IOException {		
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(output);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Compare the line of expected to ensure each on is found in the actual 
	 * result but don't worry about the order of the lines.
	 * 
	 * @param actual the actual string 
	 * @param expected the expected lines
	 * 
	 * @return true, if successful
	 */
	public static boolean compareUnordered(String actual, String expected) {			
		for(String string: expected.split("\n")) {			
			if (!actual.contains(string)) {
				return false;
			}			
		}	
		return true;
		
	}


	/**
	 * Diffs two strings line by line
	 * 
	 * @param fromString the from string
	 * @param toString the to string
	 * 
	 * @return the string
	 */
	public static String diff(String fromString, String toString) {
		StringBuffer result = new StringBuffer();
		// Break up dense xml for easier diffing.
		String[] origionalArray = fromString.replace(">\\s*<", ">\n<").split("\r?\n");
		String[] newArray = toString.replace(">\\s*<",">\n<").split("\r?\n");
		
        Diff diff = new Diff(origionalArray, newArray); 
      
        for (Object object :diff.diff()) {
        	Difference difference = (Difference)object;
        
        	int as = difference.getAddedStart();
        	int ae = difference.getAddedEnd();
        	int ds = difference.getDeletedStart();
        	int de = difference.getDeletedEnd();
           
        	if (difference.getDeletedEnd() == -1) {
            	result.append(
            			ds+"a"+
            			(as+1)+","+
            			(ae+1)
            			+"\n");
            	for (int i = as; i <= ae; i++) {
            		result.append("> "+newArray[i]+"\n");
            	}
            	
        	} else if (ae == -1) {
            	result.append(
            			(as)+
            			"d"+
            			(ds+1)+","+
            			(de+1)
            			+"\n");
            	for (int i = ds; i <= de; i++) {
            		result.append("< "+origionalArray[i]+"\n");
            	}            	
        		
        	} else {
            	result.append(ds+1);
            	if (ds != de) {
                	result.append(","+(de+1));
            	}
            	result.append("c");
            	result.append(as+1);
            	if (as != ae) {
                	result.append(","+(ae+1));
            	}
            	result.append("\n");

            	for (int i = difference.getDeletedStart(); i <= difference.getDeletedEnd(); i++) {
            		result.append("< "+origionalArray[i]+"\n");
            	}
            	result.append("---\n");
            	for (int i = difference.getAddedStart(); i <= difference.getAddedEnd(); i++) {
            		result.append("> "+newArray[i]+"\n");
            	}
        	}
        	
        }
        return result.toString();
	}
	
	/**
	 * Redirect System.out to an file int he temp dir so iit can read later.
	 */
	private static PrintStream stdOut = System.out;	
	private static File redirectedStdOut;
	
	public static void redirectStdOut() throws IOException {			
		if (redirectedStdOut == null) {
			String redirectionFileName = ".redirected-of-stdout.txt";	
			String tmpDirName = System.getProperty("java.io.tmpdir");
			if (tmpDirName != null) {
				File tmpDir = new File(tmpDirName);
				if (tmpDir.exists() && tmpDir.isDirectory() && 
						tmpDir.canWrite()) {
					redirectionFileName = tmpDir.getAbsolutePath() + 
						File.separator + redirectionFileName;
					
				}		
			}
			redirectedStdOut = new File(redirectionFileName);	
		}	
		System.setOut(new PrintStream(redirectedStdOut));
		
	}
	
	public static void resetStdOut() {
		redirectedStdOut.delete();
		System.setOut(stdOut);
	}

	public static String readStdOut() throws IOException {
		return  readStream(new FileInputStream(redirectedStdOut));
	}
	
	/**
	 * Redirect System.err to an file int he temp dir so iit can read later.
	 */
	private static PrintStream stdErr = System.err;	
	private static File redirectedStdErr;
	
	public static void redirectStdErr() throws IOException {	
		if (redirectedStdErr == null) {
			String redirectionFileName = ".redirected-of-stdout.txt";	
			String tmpDirName = System.getProperty("TEMP");
			if (tmpDirName != null) {
				File tmpDir = new File(tmpDirName);
				if (tmpDir.exists() && tmpDir.isDirectory() && 
						tmpDir.canWrite()) {
					redirectionFileName = tmpDir.getAbsolutePath() + 
						File.separator + redirectionFileName;
					
				}		
			}
			redirectedStdErr = new File(redirectionFileName);	
		}	
		System.setErr(new PrintStream(redirectedStdErr));
	}
	
	public static void resetStdErr() {
		redirectedStdErr.delete();
		System.setErr(stdErr);
	}

	public static String readStdErr() throws IOException {
		return  readStream(new FileInputStream(redirectedStdErr));
	}
	
	/**
	 * Reads all the available data from an InputStream into a String.
	 * @param stream InputStream
	 * 
	 * @return a string of the data read form the stream.
	 * @throws IOException 
	 */
	public static String readStream(InputStream stream) throws IOException {
	
		
		StringBuffer string = new StringBuffer();
		int c;
		while((c=stream.read()) > 0) {
			string.append((char)c);
		}
		return string.toString();
	}
	
	/**
	 * Sets the test database.
	 * 
	 * @param testDatabase the test database
	 * 
	 */
	protected static void setTestDatabase(DatabaseHelper testDatabase) {
		sTestDatabase = testDatabase;
	}

	/**
	 * Gets the test database, instantiating if necessary.
	 * 
	 * @return the mTestDatabase	 * 
	 * @throws SQLException 
	 */
	protected static DatabaseHelper getTestDatabase() {
		if (sTestDatabase == null) {			
			try {
				Properties properties = System.getProperties();
					Class.forName("com.mysql.jdbc.Driver").newInstance();
				
				setTestDatabase(new DatabaseHelper(
						sDatabaseInstance, 
						properties.getProperty(TEST_DATABASE_NAME_PROPERTY), 
						properties.getProperty(DATABASE_USERNAME_PROPERTY), 
						properties.getProperty(DATABASE_PASSWORD_PROPERTY)));
			
			} catch (InstantiationException e) {
				System.out.println("Failed to instanciate database driver file. Check the 'test.properties' file is correct ");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.out.println("Failed to instanciate database driver file. Check the 'test.properties' file is correct ");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Failed to instanciate database driver file. Check the 'test.properties' file is correct ");
				e.printStackTrace();
			} catch (SQLException e) {
				System.out.println("Failed to create connection to data sets database\n" +
						"Check the 'test.properties' file is correct. The properties:\n" +
						TEST_DATABASE_NAME_PROPERTY+ ", "+
						DATABASE_USERNAME_PROPERTY+ ", "+
						DATABASE_PASSWORD_PROPERTY+ " should be set.");
				e.printStackTrace();
				return null;
				
			}
		}
		return sTestDatabase;
	}

	/**
	 * Sets the data sets database.
	 * 
	 * @param testDataDatabase the test data database
	 */
	protected static void setDataSetsDatabase(DatabaseHelper testDataDatabase) {
		sDataSetsDatabase = testDataDatabase;
	}

	/**
	 * Gets the data sets database, instantiating if necessary.
	 * 
	 * @return the mTestDataDatabase
	 */
	protected static DatabaseHelper getDataSetsDatabaseHelper() {
		if (sDataSetsDatabase == null) {
			try {
				Properties properties = System.getProperties();
				Class.forName(properties.getProperty(DATASET_DRIVER_PROPERTY, "com.mysql.jdbc.Driver")).newInstance();
				DatabaseHelper databaseHelper = new DatabaseHelper(
						sDatabaseInstance, 
						properties.getProperty(DATASETS_DATABASE_NAME_PROPERTY), 
						properties.getProperty(DATABASE_USERNAME_PROPERTY), 
						properties.getProperty(DATABASE_PASSWORD_PROPERTY));
				if (databaseHelper != null) {
					setDataSetsDatabase(databaseHelper);
				}
				
			} catch (InstantiationException e) {
				System.out.println("Failed to instanciate database driver file. Check the 'test.properties' file is correct ");
				return null;
				
			} catch (IllegalAccessException e) {
				System.out.println("Failed to instanciate database driver file. Check the 'test.properties' file is correct ");
				return null;
				
			} catch (ClassNotFoundException e) {
				System.out.println("Failed to instanciate database driver file. Check the 'test.properties' file is correct ");
				return null;
				
			} catch (SQLException e) {
				System.out.println("Failed to create connection to data sets database. " +
						"Check the 'test.properties' file is correct. The properties: " +
						DATASETS_DATABASE_NAME_PROPERTY+ ", "+
						DATABASE_USERNAME_PROPERTY+ ", "+
						DATABASE_PASSWORD_PROPERTY+ " should all be set.");
				return null;
			}
		}
		return sDataSetsDatabase;
	}

	/**
	 * Gets the name of the data sets index table.
	 * 
	 * @return the name of the table storing the which data sets are require by which test.
	 */
	protected static String getTestDataSetMappingTableName() {
		return MAPPING_TABLE;
		
	}

}