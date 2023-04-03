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
package uk.ac.ed.epcc.webapp.model.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Base64;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextCleanup;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.exception.FatalDataError;
import uk.ac.ed.epcc.webapp.jdbc.exception.NoTableException;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.LockedRecordException;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;
import uk.ac.ed.epcc.webapp.model.data.stream.BlobStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;
import uk.ac.ed.epcc.webapp.timer.TimerService;


/** <code>Repository</code> encapsulates a Database table. 
 * 
 * This class is used to hold the
 * Connection (via the <code>AppContext</code>) and to cache information about the table
 * including the name and meta-data. Actual rows are stored in <code>Record</code> objects
 * which are inner classes of <code>Repository</code> to allow them to access the cached
 * information.
 * <p>
 * Each record is assumed to have a unique integer identifier that can be used to reference the
 * appropriate table record. 
 * The {@link FieldInfo} class records information about each field in the table. This class attempts to capture 
 * where a field is used to reference the integer identifier of a different table. This can be determined automatically
 * where there is a foreign key constraint or can be specified using properties. A property
 * <b>reference.</b><em>repository-tag</em><b>.</b><em>DBfield</em> defines the name of the table the field references.
 * If a handler class is registered for the remote table this is used to create a {@link TypeProducer} for the field.
 * Alternatively a reference can be registered explicitly with the Repository by registering a {@link TypeProducer} for the field.
 * This will reduce overhead where an instance of the remote handler class is already available.   
 * <p>
 * A boolean property of the form <b>truncate.</b><em>repository-tag</em><b>.</b><em>DBfield</em> can be used to request
 * that field values be truncated to the database field length when being stored. 
 * <p>
 * At the moment the integer identifier is implemented by requiring each
 * tables to have a primary key consisting of a mysql
 * auto_increment field if the is more than one auto_increment field the first
 * is taken. This dependency on mysql specific features is encapsulated within
 * the <code>MysqlRepository</code> class and could be replaced by a different class.
 * Alternative DB back-ends can be less flexible than mysql when it comes to table and field names
 * so SQL statements should be constructed using the addUniqueName addTable and FieldInfo.addName
 * method calls as this allows the Repository class to perform name mangling via the following properties:
 * <ul>
 * <li><b>table.</b><em>tag</em> defines the table name corresponding to tag. Defaults to tag</li>
 * <li><b>rename.</b><em>table-name.field-name</em> defines the name the java code uses to refer to a database field. Defaults to field-name</li>
 * </ul>
 * The {@link #getParamTag()} method returns a String to be used in looking up configuration parameters for the
 * enclosing object. This defaults to tag but can be overridden by setting the <b>config.</b><em>tag</em> property.
 * <p>
 * If the <b>table_alias.<i>tag</i></b> property is set this is used as an alias string when constructing SQL statements.
 * This is needed because the join filter classes assume a single reference between tables. If you have multiple fields that reference the same table
 * you can register the same table under different tags with different aliases allowing multiple joins to the same table.
 * </p> 
 * New <code>Repository</code> objects are obtained using a static method.
 * <pre>
 <code>
 AppContext c;
 Repository rep = Repository.getInstance(c,tag_name);
 </code>
 </pre>
 *
 * Repositories created in this way are cached in the <code>AppContext</code> so only a single <code>Repository</code> 
 * for each tag is ever created per <code>AppContext</code>. The tag is normally the same as the database table
 * and should match the tag used to create the corresponding {@link DataObjectFactory} from the AppContext. However the tag should not 
 * be used as a table name in SQL as it is possible 
 * for table renaming and name-mangling to occur. 
 * <p>
 * Repository can implement a cache of Record data based on id. This is enabled on a table by table basis via the
 * <em>cache.<i>tag-name</i></em> property.
 * This is only intended to cache values within the lifetime of an {@link AppContext} (ie during a single request).
 * The cache is populated 
 * whenever <code>SetContents</code> is called with the record ejected whenever <code>put</code> is called. 
 * This is to ensure the cache never contains a dirty record.
 * The cache should also be flushed if the table is modified directly via an SQL update, though this is only an issue if a single request re-fetches records after a SQL update.
 
 * The <code>setID</code> checks for cached data and populates itself by copying the cached record  instead of fetching a database record.
 * 
 * The cache only holds non-dirty data so could hold normal Map objects rather than actual Records.
 * Currently this is left as a future optimisation.
 * </p>
 * Note that lookups via a {@link TypeProducer} may also	utilise this caching mechanism.
 * <p>
 * Repositories are intended to be local to a parent AppContext which should represent a
 * single thread of execution. However synchronisation is implemented within the code.
 * The cost of this should be quite low and it also seems to prevent optimisation bugs in some JVMs
 * </p>
 * The Repository class implements some additional automatic type conversions beyond those implemented in
 * JDBC.
 * <ul>
 *   <li> Boolean values can be stored as Strings taking the (<b>T/F</b> or <b>True/False</b>).</li>
 *   <li>Date time values can be stored as integers. These are always relative to the Unix epoch and default to
 *   being seconds. A different time unit can be selected by setting <b>repository.resolution.<i>table-name</i></b> to be the
 *   number of milliseconds in the desired unit.</li>
 *   <li>StreamData values are always added as binary streams so they can be stored in blob types.</li> 
 * </ul>
 * 
 * If a string attribute {@link Repository#BACKUP_SUFFIX_ATTR} is stored in the {@link AppContext}
 * then this name will be used as a table name suffix to create backup tables where deleted records will be
 * copied before being deleted in the main table. This is intended to allow
 * old data to be backed up as part of a purge of data from a live table.
 * @author spb
 * 
 */

public final class Repository implements AppContextCleanup{
	/** config property prefix to mark that text fields should truncate 
	 *  to the size of the field
	 */
	private static final String TRUNCATE_PREFIX = "truncate.";

	/** config property prefix to mark that a reference field
	 * is a unique reference where at most one record in this table
	 * links to the destination. Usually this is used in combination with
	 * a single field unique key on the source table but there is no way
	 */
	private static final String UNIQUE_PREFIX = "unique.";

	/**
	 * 
	 */
	protected static final String CONFIG_TAG_PREFIX = "config.";

	/** modes supported by {@link Record#setID}
	 * 
	 * @author spb
	 *
	 */
	public enum IdMode{
		RequireExisting,
		UseExistingIfPresent,
		IgnoreExisting
	};
	/**
	 * 
	 */
	public static final String BACKUP_SUFFIX_ATTR = "BackupSuffix";

	/**
	 * 
	 */
	public static final String REFERENCE_PREFIX = "reference.";

	/**
	 * 
	 */
	private static final String USE_ID_PREFIX = "use_id.";

	public static final String CACHE_FEATURE_PREFIX = "cache.";

	private static final int DEFAULT_RESOLUTION = 1000;

	public static final Feature REQUIRE_ID_KEY = new Feature("require.id_key", true, "Require all tables to have an integer primary key");
	// This seems to serialise in the database affecting performance.
	public static final Feature CHECK_INDEX = new Feature("repository.check_index", false, "Always read indexinfo when getting metadata (for unique keys)");

	public static final Feature AT_LEAST_ONE = new Feature("repository.at_least_one",false,"At least one field must be set when creating a record");
	
	public static final Feature READ_ONLY_FEATURE = new Feature("read-only",false,"supress (most) database writes");
	
	public static final Feature BACKUP_WITH_SELECT = new Feature("repository.backup_by_select",true,"Use select/insert when backing up a record");
	// not static not thread safe
	private final DateFormat dump_format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss SSS");
	
	/** information about indexes
	 * 
	 * @author spb
	 *
	 */
	public class IndexInfo {
		private final String name;
		private final boolean unique;
		private final ArrayList<String> cols;
		public IndexInfo(String name, boolean unique){
			this.name=name;
			this.unique=unique;
			cols = new ArrayList<>();
		}
		public String getName(){
			return name;
		}
		public boolean getUnique(){
			return unique;
		}
		public Iterable<String> getCols(){
			return Collections.unmodifiableList(cols);
		}
		void addCol(int pos, String name){
			pos--;
			while( pos > cols.size()){
				cols.add("");
			}
			cols.add(pos, name);
		}
	}
	/** Information about fields
	*/
	public class FieldInfo {
		/** field name  as stored in database*/
		private final String name;

		/** SQL type of field */
		private final int type;

		/** max display width of field */
		private final int max;

		private final boolean can_null;
		
		/** Name of the table this field references if known;
		 * 
		 */
		private String references=null;
		/** Is there a 1-1 mapping between records in the source and
		 * destination table
		 * 
		 */
		private boolean unique=false;
		/** Is there a foreign key for this reference
		 * 
		 */
		private boolean indexed=false;
		/** name of the foreign key.
		 * 
		 */
		private String key_name=null;
		/** Should values be truncated to the field length
		 * 
		 */
		private boolean truncate=false;
		/** TypeProducer for the table this field references (if known)
		 * 
		 */
		private TypeProducer producer=null;
		
		private FieldInfo(String name, int type, int max, boolean can_null) {
			this.name = name;
			this.type = type;
			this.max = max;
			this.can_null = can_null;
		}
		/** get the max display width for the field
		 * 
		 * @return int width
		 */
		public int getMax() {
			return max;
		}
		/** Can this field be null
		 * 
		 * @return boolean true if can be null.
		 */
		public boolean getNullable() {
			return can_null;
		}

		/** return column type as generated by getColumnType from {@link ResultSet}
		 * 
		 * @return int
		 */
		public int getType() {
			return type;
		}
		/** get the name of the field.
		 * We don't support quoting in this method so it can be optimised for speed. 
		 * Use addName when constructing a query where quoting may be required.
		 * 
		 * 
		 * @param qualify
		 * @return String field name
		 */
		public String getName(boolean qualify){
			if( qualify){
				return addName(new StringBuilder(), qualify, false).toString();
			}
			return name;
		}
		/** Add the field name to a StringBuilder
		 * 
		 * @param sb  StringBuilder to append to
		 * @param qualify boolean Should field name be qualified with table 
		 * @param quote boolean apply quoting for SQL fragment
		 * @return modified StringBuilder
		 */
		public StringBuilder addName(StringBuilder sb, boolean qualify, boolean quote){
			if( qualify ){
				if( quote ){
					sql.quoteQualified(sb, alias_name, name);
				}else{
					sb.append(alias_name);
					sb.append(".");
					sb.append(name);
				}
			}else{
				if(quote){
					sql.quote(sb, name);
				}else{
					sb.append(name);
				}
			}
			return sb;
		}
        public boolean isNumeric(){
        	return type==Types.DOUBLE||type==Types.FLOAT|| type==Types.INTEGER|| type==Types.BIGINT|| type == Types.REAL;
        }
        public boolean isData(){
        	return type==Types.BLOB || type == Types.VARBINARY || type == Types.LONGVARBINARY;
        }
        
        /** Get the tag that will-be/has-been used to create the referenced {@link IndexedTypeProducer}.
         * 
         * if the producer has been installed explicitly this will return null
         * because there is no guarantee the producers tag can be used to
         * construct a {@link TypeProducer}. 
         * So always use {@link #getTypeProducer()}
         * in preference to constructing via this tag.
         * 
         * @return constructor tag 
         */
        public String getReferencedTable(){
        	if( references != null ){
        		return references;
        	}
        	if( producer != null && producer instanceof IndexedTypeProducer){
        		return ((IndexedTypeProducer)producer).getInnerTag();
        	}
        	return null;
        }
        @SuppressWarnings("unchecked")
		public TypeProducer getTypeProducer(){
        	if( producer == null ){
        		if( references != null ){
        			Class<? extends IndexedProducer> clazz = getContext().getPropertyClass(IndexedProducer.class, null, references);	
        			if( clazz != null ){
        				// use lazy creation constructor
        				producer = new IndexedTypeProducer(name,getContext(),clazz,references);
        			}
        		}
        	}
        	return producer;
        }
        public boolean isString(){
        	return type==Types.CHAR || type==Types.VARCHAR|| type==Types.LONGVARCHAR;
        }
        public boolean isDate(){
        	return type==Types.DATE||type==Types.TIMESTAMP || type == Types.TIME;
        }
        public boolean isBoolean(){
        	return type==Types.BIT||type==Types.TINYINT || type==Types.BOOLEAN;
        }
        public boolean isIndexed(){
        	return indexed;
        }
        /** Is this a unique reference where there is a 1-1 mapping
         * between records in the source and destination table. 
         * 
         * @return
         */
        public boolean isUnique() {
        	return unique;
        }
        public void setUnique(boolean value) {
        	unique=value;
        }
        public String getForeignKeyName(){
        	return key_name;
        }
        private void setReference(boolean indexed,String fk,String table,boolean unique){
        	this.indexed=indexed;
        	this.key_name=fk;
        	references=table;
        	this.unique=unique;
        }
        private void setTypeProducer(TypeProducer producer){
        	if(producer.getField().equals(name)){
        		this.producer=producer;
        	}else{
        		throw new ConsistencyError("Producer field does not match");
        	}
        }
        public boolean isReference(){
        	return producer != null || references != null;
        }
        /** method to dump the field value to a canonical text
         * representation.
         * 
         * returns null of the field is null.
         * 
         * @param r
         * @return
         * @throws DataFault 
         * @throws IOException 
         */
        String dump(Record r) throws DataFault, IOException{
        	String tag = getTag();
        	if( r.getProperty(tag) == null){
        		return null;
        	}
        	if( isReference() && isNumeric()) {
        		int id = r.getIntProperty(tag, -1);
        		if(id <= 0) {
        			return null;
        		}
        		return Integer.toString(id);
        	}else if( isString()){
        		return r.getStringProperty(tag);
        	}else if( isBoolean()){
        		return Boolean.toString(r.getBooleanProperty(tag));
        	}else if( isDate()){
        		return dump_format.format(r.getDateProperty(tag));
        	}else if( isNumeric()){
        		return r.getNumberProperty(tag).toString();
        	}else if ( isData()){
        		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        		StreamData data = r.getStreamDataProperty(tag);
        		data.write(stream);
        		return Base64.getEncoder().encodeToString(stream.toByteArray());
        	}
        	return null;
        }
        /** method to set a field value from the canonical text representation
         * 
         * @param r
         * @param text
         */
        void unDump(Record r, String text) throws Exception{
        	if( text == null ){
        		return;
        	}
        	String tag = getTag();
        	if( isReference()&& isNumeric()) {
        		r.setProperty(tag,Integer.parseInt(text));
    			return;
        	}else if( isString()){
        		r.setProperty(tag, text);
        	}else if ( isBoolean()){
        		r.setProperty(tag, Boolean.parseBoolean(text));
        	}else if( isDate()){
        		try {
					r.setProperty(tag, dump_format.parse(text));
				} catch (ParseException e) {
					// try a numeric timestamp 
					r.setProperty(tag, Long.parseLong(text));
				}
        	}else if( isNumeric()){
        		try{
        			r.setProperty(tag,Integer.parseInt(text));
        			return;
        		}catch(NumberFormatException e){
        						
        		}
        		try{
        			r.setProperty(tag, Long.parseLong(text));
        			return;
        		}catch(NumberFormatException e){
        						
        		}
        		try{
        			r.setProperty(tag,Double.parseDouble(text));
        			return;
        		}catch(NumberFormatException e){
        						
        		}
        		// Try a date
        		r.setProperty(tag, dump_format.parse(text));
        	}else if( isData()){
        		ByteArrayStreamData data = new ByteArrayStreamData(Base64.getDecoder().decode(text));
        		r.setProperty(tag,data);
        	}
        }
        /** get the tag used to store this field in the record.
         * Normally the field name unless renaming taking place
         * 
         * @return
         */
		public String getTag() {
			return dbFieldtoTag(name);
		}
        // Optionally truncate the value before storage
        Object truncate(Object input) {
        	if( truncate && input != null && isString() && input instanceof String) {
        		String val = (String) input;
        		// Though the code won't use these itself a longtext field
        		// returns max-length 0 in mariadb (max length needs 32-bit unsigned)
        		if( max > 0 && val.length() > max) {
        			return val.substring(0,max);
        		}
        	}
        	if( input == null && ! getNullable()) {
        		if( isString()) {
        			//map null to empty string for non nullable fields
        			//this is to cope with legacy database fields 
        			// originally forms code did this as an option in the input
        			// but functionally it was still an empty input
        			//ongoing optional string fields should support null
        			
        			return "";
        		}
        	}
        	return input;
        }
		public boolean isTruncate() {
			return truncate;
		}
		public void setTruncate(boolean truncate) {
			this.truncate = truncate;
		}
	}
    /** Record encapsulates a Database record.
	 * It is essentially a Map container where database field names are used as
	 * the key and the field data is the value referenced.
	 * <p>
	 * In general this code is fairly flexible as to the type of objects stored in
 * the record. The type of object returned from the DB depends pretty much on
 * the DB layer and the DB schema. Note that different DB drivers will handle
 * the same schema in different ways, for example different mysql
 * drivers/versions returned Integers or Longs for the same field.
 * </p>
 * Record supports a default conversion between integer and Date properties/Fields
 * based on the Repository Resolution property. The resolution defaults to 1 second
 * but can be set using the <b>repository.resolution.<i>[table-name]</i></b> property. Provided that the
 * java side consistently uses Date or integers the java code does not need to know the
 * conversion factor explicitly.
	 * <p>
	 * Records are always created empty.
	 * They can then either be populated using a ResultSet or created from
	 * scratch using put/get methods. An existing database record can be 
	 * retrieved by using the <code>setID</code> method on an empty Record.
	 *  In all cases no changes are written to
	 * the database until the <code>commit()</code> method is called.
	 * </p>
	 * The class also supports get/set Property methods that support
	 * additional type conversions and casting. These are more useful than the 
	 * <code>put</code>/<code>get</code> calls in the <code>Map</code> interface 
	 * as the type of Object returned from the map may change if the database schema (or JDBC driver) 
	 * is changed.
	 * <p>
	 * For Example:
<pre>
<code>
     Repository res = Repository.getInstance(ctx,"my_table");
     Record rec = res.new Record();
     rec.setProperty("Number",12.0);
     rec.commit(); // create record
     rec.setProperty("Number",24.0); 
     rec.commit(); // update record
     
     Record rec2 = res.new Record();
     rec2.setID(rec.getID()); // retrieve new copy from DB by ID.
     double d = rec2.getDoubleProperty("Number"); // should be 24.0
</code>
</pre>
     *
     *In many cases the values of a database field are actually codes denoting one of a
     *finite set of objects.In this case a class implementing <code>TypeProducer</code> should be 
     *used to express this mapping. There are special <code>getProperty</code> and <code>setProperty</code>
     *calls that use this interface.
     *Java Enum types can be supported using <code>EnumProducer</code>
     * where the text value of the Enum will be stored in the database. 
     * however much of the existing code base uses sub classes of <code>BasicType</code> to implement
     * type-safe enumeration. 
     *
     *<p>
     *
	 * The intention is that this class is more database facing and that model
	 * classes contain an instance of Record rather than sub-classing it.
	 * </p>
	 * 
	 * @author spb
	 * 
	 */
	public final class Record extends HashMap<String,Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2L;

		
		private int id;

		private Set<String> dirty = null;
		/** does this Record exist in the database */
		private boolean have_id = false;

		/** are updates to this record forbidden
		 * 
		 */
		private boolean locked=false;
		
		public Record() {
			super();
		}

		/**
		 * clear all dirty state.
		 * 
		 * 
		 */
		synchronized private void clean() {
			if (dirty != null) {
				dirty.clear();
			}
		}

		/**
		 * reset to uninitialised state.
		 * 
		 */
		@Override
		public synchronized void clear() {
			if( locked) {
				throw new LockedRecordException("clear called on locked record");
			}
			deCache();
			super.clear();
			clean();
			have_id = false;
			id = 0;

		}

		@SuppressWarnings("unchecked")
		@Override
		public synchronized Object clone() {
			Record copy = (Record) super.clone();
			copy.locked=false;
			if( have_id ){
				copy.have_id=true;
				copy.id=id;
			}
			if( dirty != null){
				copy.dirty= new HashSet(dirty);
			}
			return copy;
		}

		/**
		 * commit changes made to this object to the
		 * 
		 * @return boolean true if change made
		 * @throws uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault
		 * 
		 * 
		 */
		public synchronized  boolean commit()
				throws uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault {
			if( locked ) {
				throw new DataFault("commit called on locked record "+id);
			}
			// Don't check for dirty flags here but in update
			// It should be legal to commit a new object without setting any
			// fields
			boolean changed = false;
			try {
				if (have_id) {
					// write changes to existing record
					changed = update();
					assert(id > 0);
					assert(have_id);
					// clear cache if we have modified the DB.
					if( changed && use_cache ){
						ejectCache(id);
					}
					assert(id > 0);
					assert(have_id);
				} else {
					// create new record
					int tmp  = insert(this);
					if( use_id ){
						setInitialID(tmp);
						changed = true;
						assert(id > 0);
						assert(have_id);
					}
				}
				// reset dirty flags.
				clean();

			}catch(DataFault df) {
				throw df;
			} catch (Exception e) {
				throw new DataFault("Error in commit", e);
			}
			assert(id > 0);
			assert(have_id);
			return changed;
		}
		/** Set this record to the same state as an existing Record
		 * 
		 * @param r
		 */
        @SuppressWarnings("unchecked")
	    synchronized void copy(Record r){
        	// We allow copy to backup tables.
        	if( Repository.this != r.getRepository()  ){
        		throw new ConsistencyError("copying Record from different repository");
        	}
        	clear();
        	// copy raw values without impacting dirty values
        	for(Entry<String,Object> e : r.entrySet()) {
        		super.put(e.getKey(),e.getValue());
        	}
        	if( r.have_id){
        		have_id=true;
        		id=r.id;
        	}
        	if( r.dirty != null){
        		dirty=new HashSet( r.dirty );
        	}else {
        		// putAll will have set local diry flags
        		dirty = null;
        	}
        }
        
        public void backup() throws DataFault{
        	Repository store = getBackup();
        	if( store == null ){
        		return;
        	}
        	if( BACKUP_WITH_SELECT.isEnabled(getContext())){
        		StringBuilder sb = new StringBuilder();
        		try{

        			sb.append("REPLACE INTO ");
        			store.addTable(sb, true);
        			sb.append(" SELECT * FROM ");
        			getRepository().addSource(sb, true);
        			sb.append(" WHERE ");
        			getRepository().addUniqueName(sb, true, true);
        			sb.append("=?");
        			try(PreparedStatement stmt = sql.getConnection().prepareStatement(sb.toString())){
        				stmt.setInt(1, getID());
        				stmt.executeUpdate();
        			}
        			return;
        		}catch(SQLException e){
        			sql.getService().handleError("Error in backup "+sb.toString(),e);
        		}
        	}
        	Record b = store.new Record();
        	try {
        		b.setID(getID(), IdMode.IgnoreExisting);
        	} catch (DataException e) {
        		// should not get this if require existing is false
        		throw new ConsistencyError("unexpected exception", e);
        	} 
        	b.putAll(this);
        	b.commit();

        }
        /** Get a map of the contents without the UniqueID field
         * 
         * @return Map
         */
        public Map<String,Object> getValues(){
        	Map<String,Object> res = new HashMap<>();
        	for(String key : getFields()){
        		if( ! key.equals(getUniqueIdName())){
        			Object value = get(key);
        			if( value != null ){
        				res.put(key, value);
        			}
        		}
        	}
        	return res;
        }
		/**
		 * delete the corresponding database entry and restore the Record to
		 * uninitialised state.
		 * 
		 * @throws ConsistencyError
		 * @throws DataFault 
		 */
		public synchronized void delete() throws ConsistencyError, DataFault {
			if (!have_id) {
				clear();
				return;
			}
			backup();
			try {
				Connection conn = sql.getConnection();
				if (conn == null) {
					throw new DataFault("No connection");
				}
				deCache();
				if( Repository.READ_ONLY_FEATURE.isEnabled(getContext()) || sql.isReadOnly()){
					return;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("DELETE FROM ");
				addTable(sb, false);
				sb.append(" WHERE ");
				addUniqueName(sb, false, true);
				sb.append("=");
				sb.append(getID());
				try(Statement stmt = conn.createStatement();){
					String query = sb.toString();
					int results = stmt.executeUpdate(query);
					if( DatabaseService.LOG_UPDATE.isEnabled(getContext())){
						LoggerService serv = getContext().getService(LoggerService.class);
						if( serv != null ){
							serv.getLogger(getClass()).debug("delete query is "+query);
						}
					}
					// Destroy the connection to reduce strangeness of mis-use
					boolean ok = (results == 1);
					if (ok) {
						clear();
					}
				}

			} catch (SQLException e) {
				sql.getService().handleError("SQL Exception", e);

			}
		}
		
		/**
		 * Compare with a Map of values for all the fields they have in common
		 * test for equality.
		 * 
		 * @param r
		 *            Map to compare to
		 * @return return false if any common fields are different.
		 */
		public boolean equals(Map<String,Object> r) {
			for (Iterator<String> it = getFields().iterator(); it.hasNext();) {
				String key = it.next();
				if (r.containsKey(key)) {
					Object o1 = get(key);
					Object o2 = r.get(key);
					if (o1 != null) {
						if (o2 == null || !compare(o1, o2)) {
							return false;
						}
					} else {
						if (o2 != null) {
							return false;
						}
					}
				}
			}
			return true;
		}
		/**
		 * Query a property that should contain a single Y/N character and retrun
		 * this as a boolean. A missing property is returned as true
		 * 
		 * @param name
		 *            String name of property
		 * @return boolean true for Y
		 */
		public final boolean getBooleanProperty(String name) {
			return getBooleanProperty(name, true);
		}

		/**
		 * Query a property that should contain a single Y/N character and retrun
		 * this as a boolean. A missing property is returned as the default value
		 * 
		 * 
		 * @param name
		 *            String name of property
		 * @param default_value
		 *            value to return for missing property
		 * @return boolean true for Y
		 */
		public final boolean getBooleanProperty(String name, boolean default_value) {
			return convertBoolean(getProperty(name), default_value).booleanValue();
		}
		public  final Date getDateProperty(String name) {
			Object o = getProperty(name);
			return convertDate(o);
		}
		
		/**
		 * get a property as a double value
		 * 
		 * @param name
		 *            String Property to get
		 * @return double value
		 */
		public final double getDoubleProperty(String name) {
			return getDoubleProperty(name,0.0);
		}

		public final double getDoubleProperty(String name, double default_value) {
			Number n = getNumberProperty(name);
			if (n == null) {
				return default_value;
			}
			return n.doubleValue();
		}

		/**
		 * get a property as a float value
		 * 
		 * @param name
		 *            String Property to get
		 * @return float value
		 */
		public final float getFloatProperty(String name) {
			return getFloatProperty(name, 0.0f);
		}

		public final float getFloatProperty(String name, float default_value) {
			Number n = getNumberProperty(name);
			if (n == null) {
				return default_value;
			}
			return n.floatValue();
		}

		public final int getIntProperty(String name) {
			return getIntProperty(name,-1);
		}
		
		public final int getIntProperty(String name, int default_value) {
			Number n = getNumberProperty(name);
			if (n == null) {
				return default_value;
			}
			return n.intValue();
		}
		/**
		 * get the unique id value for this Record
		 * 
		 * @return int id
		 * @throws ConsistencyError
		 *             if id not defined yet
		 */
		public final int getID() throws ConsistencyError {
			// Use value not have_id as this may be an uncommitted
			// record with id IgnoreExisting
			if (id > 0) {
				return id;
			}
			throw new ConsistencyError("Cannot get ID of uncommited object");
		}
		/**
		 * get a property as a long value
		 * 
		 * @param name
		 *            String Property to get
		 * @return int value
		 */
		public final long getLongProperty(String name) {
			return getLongProperty(name, 0L);
		}

		public final long getLongProperty(String name, long default_value) {
			Number n = getNumberProperty(name);
			if (n == null) {
				return default_value;
			}
			return n.longValue();
		}

		public final Number getNumberProperty(String name) {
			Object o = getProperty(name);
			return convertNumber(o);
		}
		
	
		/**
		 * Return the .Value associated with the specified TypeProducer
		 * @param <F> The type of value returned
		 * @param <D> Type stored in DB
		 * 
		 * @param t
		 * @return TheValue
		 */
		@SuppressWarnings("unchecked")
		public final <F,D> F getProperty(TypeProducer<F,D> t) {
			return t.find((D) getProperty(t.getField()));
		}
		/**
		 * Return the .Value associated with the specified TypeProducer
		 * @param <F> The type of value returned
		 * @param <D> Type stored in DB
		 * 
		 * @param t
		 * @param def default result
		 * @return TheValue
		 */
		@SuppressWarnings("unchecked")
		public final <F,D> F getProperty(TypeProducer<F,D> t,F def) {
			D val = (D) getProperty(t.getField());
			if( val == null){
				return def;
			}
			try {
				return t.find(val);
			} catch (Exception e) {
				getContext().error(e,"Error converting via TypeProducer");
				return def;
			}
		}
		/**
		 * Returns the value associated with this database column name
		 * 
		 * Where possible you should use one of the get<Type>Property methods as
		 * these will take care of any conversion from the underlying database
		 * object.
		 * 
		 * @param name
		 *            The name of the field
		 * @return an Object representing the value of the field
		 */
		public final Object getProperty(String name) {
			return get(name);
		}

		/**
		 * Returns the value associated with this database column name Unlike
		 * getProperty the returned value should never be null to additional error
		 * reporting is invoked if it is.
		 * 
		 * @param name
		 *            The name of the field
		 * @return an Object representing the value of the field
		 */
		public final Object getRequiredProperty(String name) {
			Object o = getProperty(name);
			if (o == null) {
				throw new ConsistencyError("Error retrieving required property "
						+ name + " from " + getClass().getName() + ":" + getID());
			}
			return o;
		}

		public final StreamData getStreamDataProperty(String name)
				throws DataFault {
			Object o = getProperty(name);
			if (o instanceof StreamData) {
				return (StreamData) o;
			}
			if( o == null ){
				return null;
			}
			if( o instanceof String){
				return new ByteArrayMimeStreamData(((String)o).getBytes());
			}
			// assume a byte array
			return new ByteArrayStreamData((byte[]) o);
		}

		/**
		 * get a property as a string.
		 * 
		 * @param name
		 *            Property to get
		 * @return String value
		 */
		public final String getStringProperty(String name)  {
			Object o = getProperty(name);
			if (o == null)
				return null;
			if (o instanceof String)
				return (String) o;
			if (o instanceof StreamData) {
				StreamData s = (StreamData) o;
				ByteArrayOutputStream dat = new ByteArrayOutputStream();
				try {
					s.write(dat);
				} catch (Exception e) {
					getContext().error(e, "error converting StreamData to string");
					return null;
				}
				return dat.toString();
			}
			if (o instanceof byte[]) {
				return new String((byte[]) o);
			}
			return o.toString();
		}

		/**
		 * get a property value as a string with a default value to return if the
		 * property is not set or the string is empty
		 * 
		 * @param name
		 * @param default_value
		 * @return String
		 */
		public final String getStringProperty(String name, String default_value) {
			String s = getStringProperty(name);
			if (s == null || s.length() == 0) {
				return default_value;
			}
			return s;
		}

	
		/**
		 * get the enclosing Repository
		 * 
		 * @return the Repository used to create this Record.
		 */
	    public Repository getRepository() {
			return Repository.this;
		}

		boolean hasID(){
			return have_id;
		}

		/**
		 * is the object in any way modified.
		 * 
		 * @return boolean
		 */
		synchronized boolean  isDirty() {
			return dirty != null && ! dirty.isEmpty();
		}

		/**
		 * is the specified field modified.
		 * 
		 * @param key
		 * @return boolean
		 */
		synchronized boolean isDirty(String key) {
			if (dirty == null) {
				return false;
			}
			return dirty.contains(key);
		}

		@Override
		public Object put(String key, Object value) {
			return put(key, value, allow_bogus_put);
		}


		synchronized private Object put(String key, Object value, boolean optional) {

//			if( (! have_id || ! allow_null_value ) && value == null ){
//				// never allow null in initial object
//				throw new UnsupportedOperationException("Null value stored in "+key);
//			}
			if( locked) {
				throw new LockedRecordException("put on locked record "+getID());
			}

			if (!hasField(key)) {
				if (optional &&  ! key.equals(id_name)) {
					// this allows us to temporarily cache values for example store the name of
					// an object and later convert to to a DB reference
					return super.put(key, value);
				}
				// this includes attempts to put new values for the ID field
				throw new UnsupportedOperationException(
						"Invalid field specified "+getTable()+"." + key + ":" + value);
			}
			// Length check for optional
			if( optional && value instanceof String ){
				FieldInfo info = getInfo(key);
				if( info.isString() ) {
					int max = info.getMax();
					if( max > 0 && ((String)value).length() > max) {
						// Skip optional strings that are known to be too long for the database
						// however an unlimited field may return a max of zero
						return get(key);
					}
				}
			}
			if( ! isDirty()){
				// once dirty it should not be in cache. I'm assuming the dirty check is slightly cheaper 
				// than deCache as there is no synchronisation 
			  deCache(); // do this before potentially modifying state to avoid race condition
			}
			value = convert(key, value);
			Object previous = super.put(key, value);
			if (!compare(previous, value)) {
				setDirty(key, true);
			}
			return previous;
		}
		/** Force contents to a non dirty value
		 * 
		 * @param key
		 * @param value
		 * @return
		 */
        private Object rawPut(String key, Object value){
        	setDirty(key,false);
        	return super.put(key,value);
        }
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.HashMap#putAll(java.util.Map)
		 */
		@Override
		public void putAll(Map<? extends String,? extends Object> m) {
			// only insert valid keys
			for (Iterator<String> it = getFields().iterator(); it.hasNext();) {
				String key = it.next();
				if (m.containsKey(key)) {
					put(key, m.get(key));
				}
			}
		}

		synchronized public Object remove(String key) {
			if (!hasField(key)) {
				// this includes attempts to put new values for the ID field
				throw new UnsupportedOperationException(
						"Invalid field specified");
			}
			Object o = super.remove(key);
			if (o != null) {
				setDirty(key, true);
			}
			return o;
		}

		/**
		 * Like putAll but does full synchronisation removing fields that are
		 * missing as well.
		 * 
		 * @param m
		 *            Map to sync to
		 */
		public void set(Map<? extends String,? extends Object> m) {
			// only insert valid keys
			for (Iterator<String> it = getFields().iterator(); it.hasNext();) {
				String key = it.next();
				if (m.containsKey(key)) {
					put(key, m.get(key));
				} else {
					remove(key);
				}
			}
		}

		
		/**
		 * populate an object from a ResultSet
		 * 
		 * @param rs
		 *            ResultSet
		 * @throws DataFault
		 * 
		 * @throws ConsistencyError
		 */
		void setContents(ResultSet rs) throws DataException
				{
			setFromResultSet(this,rs, false);
		}
		/**
		 * populate an object from a ResultSet
		 * 
		 * It seems to work if we always qualify the field names but its slower
		 * 
		 * @param rs
		 *            ResultSet
		 * @param qualify
		 *            boolean qualify the field names with the table name as
		 *            ResultSet is from a join
		 * @throws DataFault
		 * 
		 * @throws ConsistencyError
		 */
		void setContents(ResultSet rs,boolean qualify) throws DataException {
			setFromResultSet(this,rs, qualify);
		}
	
		/**
		 * mark the dirty state (does it need flushing to the DB) for a
		 * specified field
		 * 
		 * @param key
		 * @param val
		 */
		synchronized void setDirty(String key, boolean val) {
			if( val && locked) {
				throw new LockedRecordException("setDirty called on locked record");
			}
			if (dirty == null) {
				if( ! val ){
					return;
				}
				dirty = new HashSet<>();
			}
			if( val ){
				dirty.add(key);
			}else{
				dirty.remove(key);
			}
		}

		private final  void deCache() {
			if( have_id && use_cache ){
				// this may be the same object as was in the cache
				// so eject it. Not worth checking if it really is the same.
				ejectCache(id);
			}
		}
		protected void setInitialID(int id){
			this.id=id;
			// Easier to test value comparison if ID not stored in hash
			super.put(getUniqueIdName(), Integer.valueOf(id));
			have_id=true;
		}
/** 
 * 
 * This method returns a reference to itself so we can initialise the record
 * as part of a constructor.
 * 
 * @param id2
 * @return reference to self
 * @throws DataException
 */
		public Record setID(int id2) throws DataException {
			return setID(id2, IdMode.RequireExisting);
		}
		/**initialise a record using the id integer.
		 * 
		 * If an existing record is not required then this will set the id that should be used when the
		 * record is eventually inserted. Care needs to be taken to ensure the id is not created between this call and the commit.
		 * 
		 * This method returns a reference to itself so we can initialise the record
		 * as part of a constructor.
		 * 
		 * @param id2
		 * @param mode {@link IdMode}
		 * @return
		 * @throws DataException
		 */
		Record setID(int id2,IdMode mode) throws DataException {
			if (have_id) {
				throw new ConsistencyError("Resetting id of Record");
			}
			if( ! use_id){
				throw new ConsistencyError("Setting id on non indexed table");
			}
          
			if( use_cache && mode == IdMode.RequireExisting ){
				synchronized(Repository.this){
					Map<Integer,Record> cache=getCache(); 
					if (cache != null ){

						Record peer = cache.get(id2);
						if( peer != null ){
							assert(peer.id == id2 && ! peer.isDirty());
							// We have to copy fully. If the cache and this object hold any data in common
							// then we could hit problems if one instance is modified when the other should not be.
							// e.g one instance is changed but decides not to commit the other may call commit later
							copy(peer);
							assert(id == id2);
						}else{
							// set contents will store in cache.
							populate(id2, true);
						}
					}else{
						// set contents will store in cache.
						populate(id2, true);
					}
					cache=null;
				}
			}else{
				if( mode == IdMode.IgnoreExisting){
					// just remember desired id
					id = id2;
				}else{
					populate(id2, mode == IdMode.RequireExisting);
				}
			}
            assert(id == id2);
            if( id != id2 ){
            	// extra debug as assertion failed once
            	throw new ConsistencyError("Error setting Record ID "+getTag()+" "+id+"!="+id2+" cache "+use_cache);
            }
            return this;
		}
		
		private void populate(int id2, boolean require_existing) throws DataException{
			try(ResultSet set = findRecord(id2, require_existing)){
				if(set != null){
					setContents(set);
				}else{
					id=id2; // remember id we want to use.
				}
			} catch (SQLException e) {
				getContext().getService(DatabaseService.class).handleError("Error closing result set", e);
			}
		}
		/** Store data in record 
		 * 
		 * @param key
		 * @param value
		 */
        public final void setProperty(String key, Object value){
        	put(key,value);
        }
        /**
    	 * Set a property where it is acceptable for the property not to exist in the
    	 * underlying database.
    	 * 
    	 * @param name
    	 * @param value
    	 */
    	public final void setOptionalProperty(String name, Object value) {
    		put(name, value, true);
    	}

    	/**
    	 * Set the property associated with a {@link TypeProducer} 
    	 
    	 * @param <F> Type corresponding to the {@link TypeProducer}
    	 * @param <D> Type stored in Database
    	 * 
    	 * @param t
    	 *            the BasicType to set
    	 * @param v
    	 *            The Value to set.
    	 */
    	public final <F,D> void setProperty(TypeProducer<? super F,D> t, F v) {
    		D tag = t.getIndex(v);
    		if (tag != null) {
    			setProperty(t.getField(), tag);
    		} else {
    			throw new IllegalArgumentException(v.toString() + " wrong type for "
    					+ t.getField());
    		}
    	}
    	/**
    	 * Set the property associated with a {@link TypeProducer}
    	 * where it is acceptable for the property not to exist in the
    	 * underlying database
    	 * @param <F> Type corresponding to {@link TypeProducer}
    	 * @param <D> Type stored in Database
    	 * 
    	 * @param t
    	 *            the BasicType to set
    	 * @param v
    	 *            The Value to set.
    	 */
    	public final <F,D> void setOptionalProperty(TypeProducer<? super F,D> t, F v) {
    		if( v == null ) {
    			setOptionalProperty(t.getField(), null);
    			return;
    		}
    		D tag = t.getIndex(v);
    		if (tag != null) {
    			setOptionalProperty(t.getField(), tag);
    		} else {
    			throw new IllegalArgumentException(v.toString() + " wrong type for "
    					+ t.getField());
    		}
    	}

    	/**
    	 * store a Y/N value based on boolean input.
    	 * This allows single character fields to be used to store boolean values.
    	 * A true boolean field will also work as the strings will be converted appropriately
    	 * @see Repository#convertBoolean(Object, Boolean)
    	 * 
    	 * @param name
    	 *            String property to set
    	 * @param val
    	 *            boolean value
    	 */
    	public final void setProperty(String name, boolean val) {
    		if (val) {
    			setProperty(name, "Y");
    		} else {
    			setProperty(name, "N");
    		}
    	}

    	/**
    	 * set a property to a double value
    	 * 
    	 * @param name
    	 *            String property to set.
    	 * @param val
    	 */
    	public final void setProperty(String name, double val) {
    		setProperty(name, new Double(val));
    	}

    	/**
    	 * set a property to an float value
    	 * 
    	 * @param name
    	 *            String property to set.
    	 * @param val
    	 */
    	public final void setProperty(String name, float val) {
    		setProperty(name, new Float(val));
    	}

    	/**
    	 * set a property to an integer value
    	 * 
    	 * @param name
    	 *            String property to set.
    	 * @param val
    	 */
    	public final void setProperty(String name, int val) {
    		setProperty(name, new Integer(val));
    	}

    	/**
    	 * set a property to an long value
    	 * 
    	 * @param name
    	 *            String property to set.
    	 * @param val
    	 */
    	public final void setProperty(String name, long val) {
    		setProperty(name, new Long(val));
    	}

		/**
		 * output the value of a field to a prepared statement
		 * 
		 * Note that {@link StreamData} objects are added as binary streams.
		 * 
		 * @param buff
		 *            query buffer. We append additional info to this to provide
		 *            better debug messages
		 * @param stmt
		 *            PreparedStatement fo populate
		 * @param pos
		 *            statement position to set
		 * @param key
		 *            Field to output
		 * @throws SQLException
		 */
		protected final void setValue(StringBuilder buff, PreparedStatement stmt,
				int pos, String key) throws SQLException {
			
				Object value = get(key);

				buff.append(' ');
				buff.append(pos);
				buff.append(':');
				buff.append(key);
				buff.append('=');
				if (value instanceof StreamData) {
					// setObject also understands StreamData
					buff.append("file(");
					buff.append(Long.toString( ((StreamData)value).getLength() ));
					buff.append(")");
				} else {
					// needs to work with null values
					buff.append(String.valueOf(value));
					
				}
				setObject(stmt, pos, key, value);
		}

		/**
		 * update an existing record
		 * 
		 * @return boolean true if updates were made
		 * @throws ConsistencyError
		 * @throws DataFault
		 */
		synchronized private boolean update() throws ConsistencyError, DataFault {
			int pattern_count=1;

			if( READ_ONLY_FEATURE.isEnabled(ctx)|| sql.isReadOnly()){
				return false;
			}
			if (!isDirty()) {
				return false;
			}
			
			TimerService time = ctx.getService(TimerService.class);
			if( time != null ){
				time.startTimer(getTag()+"-update");
			}
			StringBuilder buff = new StringBuilder("UPDATE ");
			sql.quote(buff, table_name);
			buff.append(" SET ");
			try {

				boolean update = false;
				boolean atleastone = false;
				Set<String> fields = getFields();
				for (String key: fields) {
					FieldInfo info = getInfo(key);
					if (isDirty(key)) {
						if (atleastone) {
							buff.append(", ");
						}
						info.addName(buff, false, true);
						buff.append("=?");
						update = true;
						atleastone = true;
						pattern_count++;
					}
				}
				buff.append(" WHERE ");
				addUniqueName(buff, false, true);
				buff.append('=');
				buff.append(getID());
				boolean updated=false;
				if (update) {
					try(PreparedStatement stmt = sql.getConnection()
							.prepareStatement(buff.toString())){
						int pos = 1;
						for (String key : fields) {
							if (isDirty(key)) {
								setValue(buff, stmt, pos, key);
								pos++;
							}
						}
						if( DatabaseService.LOG_UPDATE.isEnabled(getContext())){
							LoggerService serv = getContext().getService(LoggerService.class);
							if( serv != null ){
								serv.getLogger(getClass()).debug("update query is "+buff.toString());
							}
						}
						//ctx.getService(LoggerService.class).getLogger(getClass()).debug("update :"+buff+" pos="+pos+" count="+pattern_count+" fieldcount="+fields.size()+" dirty"+dirty.size());
						assert(pos == pattern_count);

						int rows_changed = stmt.executeUpdate();

						if (rows_changed > 1 || rows_changed < 0) {
							throw new ConsistencyError(
									"incorrect number of rows changes "+rows_changed+" "+buff.toString());
						}else if( rows_changed == 0){
							LoggerService serv = getContext().getService(LoggerService.class);
							if( serv != null ){
								// Same update may have happened between read and commit. 
								serv.getLogger(getClass()).warn("No update when one was expected "+buff.toString());
							}
						}else{
							updated = true;
						}
					}
				}
				
				return updated;

			} catch (SQLException e) {
				// report the problem SQL to aid debugging
				sql.getService().handleError("SQL Exception " + buff.toString(), e);
				return false; // actually unreachable
			}finally{
				if( time != null ){
					time.stopTimer(getTag()+"-update");
				}
			}
		}

		/** Get the id of an identical (up to id) record in the database
		 * normally called on an uncommitted record so only the set fields
		 * are used to generate the query
		 * This is not efficient but can be used when merging data.
		 * 
		 * @return id 
		 * @throws ConsistencyError
		 * @throws DataFault
		 */
		public int findDuplicate() throws ConsistencyError, DataFault {
			return findDuplicate(-1,getFields(),false);
		}
		/** Get the id of an identical (up to id) record in the database
		 * normally called on an uncommitted record so only the set fields
		 * are used to generate the query. If a +ve id value is specified this will
		 * be included in the selection effectively looking for a particular record that matches
		 * the uncommitted record.
		 * This is not efficient but can be used when merging data.
		 * 
		 * @param id to match (if greater than zero)
		 * @param fields {@link Set} of field names to check.
		 * @param check_all Should all fields be checked not just the set/dirty fields.
		 * @return id of duplicate 
		 * @throws ConsistencyError
		 * @throws DataFault
		 */
		synchronized public int findDuplicate(int id, Set<String> fields,boolean check_all) throws ConsistencyError, DataFault {
			if (have_id && ! (check_all || isDirty()) && this.id==id) {
				return id;
			}
			int pattern_count=1;

			
			StringBuilder buff = new StringBuilder("SELECT ");
			addUniqueName(buff, false, true);
			buff.append(" FROM ");
			sql.quote(buff, table_name);
			buff.append(" WHERE ");
			try {
				boolean atleastone = false;
				if( id > 0 ){
					addUniqueName(buff, false, true);
					buff.append("=");
					buff.append(Integer.toString(id));
					atleastone = true;
				}
				
				
				
				for (String key: fields) {
					FieldInfo info = getInfo(key);
					if( check_all || isDirty(key) ){
						if (atleastone) {
							buff.append(" AND ");
						}
						if( containsKey(key) && get(key) != null){
							info.addName(buff, false, true);
							buff.append("=?");
							pattern_count++;
						}else{
							
							if( info.isReference()){
								// various was a reference can be missing/null
								// but mysql specific
								buff.append("COALESCE(");
								info.addName(buff, false, true);
								buff.append(",0)<=0");
							}else{
								info.addName(buff, false, true);
								buff.append(" IS NULL ");
							}
						}
						atleastone = true;
					}
				}
				
				try(PreparedStatement stmt = sql.getConnection()
						.prepareStatement(buff.toString())){
					int pos = 1;
					for (String key : fields) {
						if( isDirty(key) && containsKey(key)){
							setValue(buff, stmt, pos, key);
							pos++;
						}
					}
					if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(getContext())){
						LoggerService serv = getContext().getService(LoggerService.class);
						if( serv != null ){
							serv.getLogger(getClass()).debug("query is "+buff.toString());
						}
					}
					//ctx.getService(LoggerService.class).getLogger(getClass()).debug("update :"+buff+" pos="+pos+" count="+pattern_count+" fieldcount="+fields.size()+" dirty"+dirty.size());
					if( pos != pattern_count) {
						System.out.println("pos "+pos+" count "+pattern_count);
					}
					assert(pos == pattern_count);
					try(ResultSet rs = stmt.executeQuery()){
						if( rs.first()){
							return rs.getInt(1);
						}else{
							return 0;
						}
					}
				}

			} catch (SQLException e) {
				// report the problem SQL to aid debugging
				sql.getService().handleError("SQL Exception " + buff.toString(), e);
				return 0; // actually unreachable
			}
		}

		
		    @Override
			public String toString(){
		    	StringBuilder sb = new StringBuilder();
		    		sb.append("Record[ ");
		    		sb.append(" tag=");
		    		sb.append(getTag());
		    		sb.append(" id=");
		    		sb.append(id);
		    		for(String field : getFields()){
		    			sb.append(" ");
		    			sb.append(field);
		    			sb.append("=");
		    			Object o = get(field);
		    			if( o != null ){
		    			   sb.append(o.toString());
		    			}else{
		    				sb.append("null");
		    			}
		    			if( isDirty(field)){
		    				sb.append("[X]");
		    			}
		    			
		    		}
		    		sb.append(" ]");
		    	return sb.toString();
		    }
		    /** mark the record as read-only/locked
		     * 
		     * @throws DataFault
		     */
		    public void lock(boolean allow_dirty) throws DataFault {
		    	if( isDirty()) {
		    		if( allow_dirty) {
		    			clean();
		    		}else {
		    			throw new DataFault("lock of dirty record");
		    		}
		    	}
		    	locked=true;
		    }
		    public boolean isLocked() {
		    	return locked;
		    }

			
	}

	/** Class representing an Order clause 
	 * 
	 * @author spb
	 *
	 */
	public final class Order implements OrderClause{
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (desc ? 1231 : 1237);
			result = prime * result + ((info == null) ? 0 : info.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Order other = (Order) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (desc != other.desc)
				return false;
			if (info == null) {
				if (other.info != null)
					return false;
			} else if (!info.equals(other.info))
				return false;
			return true;
		}
		private final boolean desc;
		private final FieldInfo info;
		/**
		 * 
		 * @param desc descending order
		 * @param info {@link FieldInfo} to order by (null for primary key)
		 */
		private Order(boolean desc,FieldInfo info){
			this.desc=desc;
			this.info=info;
		}
		@Override
		public StringBuilder addClause(StringBuilder sb, boolean qualify){
			if( info != null ){
				info.addName(sb, qualify, true);
			}else{
				addUniqueName(sb, qualify, true);
			}
			if( desc){
				sb.append(" DESC");
			}
			return sb;
		}
		private Repository getOuterType() {
			return Repository.this;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Order(");
			addClause(sb, true);
			sb.append(")");
			return sb.toString();
		}
	}
	final private AppContext ctx;
	final private SQLContext sql;

	/**This is the tag used to find this repository.
	 * 
	 */
	final private String tag_name;
	/**This is the actual database table name used in SQL
	 * 
	 */
	final private String table_name;
	
	/** This is the table alias used in selects
	 * 
	 * If this is the same as table_name no alias is used
	 */
	final private String alias_name;
	/** This is the tag used in configuration parameters.
	 * This is normally the same as the creation tag but may be re-directed 
	 * to make it easy to support table rolling.
	 * 
	 */
	final private String param_name;
	
	/** Tag for the database. Only needed for applications that cross multiple databases.
	 * 
	 */
	final private String db_tag;

	/** can we use the unique id code for this table
	 * 
	 */
	protected final boolean use_id;
	/** Name of the unique ID field
	 * 
	 */
	private String id_name = null;
	private String qualified_id_name = null;

	/** is it OK to put a key that does not have a corresponding DB field */
	private boolean allow_bogus_put = false;
    /** is it OK to store a null value in a Record */
	private boolean allow_null_value = true;

	/**
	 * default number of milliseconds per tick when using an integer type to
	 * specify a date or vice versa.
	 */
	private long resolution = DEFAULT_RESOLUTION;

	private Map<String,IndexInfo> indexes=null;
	/**
	 * A map of field names to info about the field
	 * 
	 */
	private Map<String,FieldInfo> fields= null;

	/** Prepared statement used to find Record by id.
	 * 
	 * As this is so common we keep a reference to this statement.
	 */
	private PreparedStatement find_statement=null;
	/** Should this repository use a Record cache
	 * 
	 */

	private final boolean use_cache;

	/** reference to implement find caches.
	 * 
	 */
	private SoftReference<Map<Integer,Record>> cache_ref=null;

	
	/**
	 * Create a Repository object private to force the use of the Factory
	 * method.
	 * 
	 * @param c
	 *            AppContext
	 * @param tag  String tag used to find repository.
	 * @throws SQLException 
	 *            
	 */
	protected Repository(AppContext c, String tag) throws SQLException {
		super();
		ctx = c;
		db_tag = ctx.getInitParameter("db-tag."+tag,null);
		sql = ctx.getService(DatabaseService.class).getSQLContext(db_tag);
		tag_name=TableToTag(c, tag);
		param_name=c.getInitParameter( CONFIG_TAG_PREFIX+tag,tag);
		table_name = tagToTable(ctx,tag);
		// Setting this allows multiple joins to the same table by
		// having more than one tag for the same table with different aliases
		alias_name = c.getInitParameter("table_alias."+tag, table_name);
		setResolution(c.getIntegerParameter("repository.resolution."+tag_name, DEFAULT_RESOLUTION));
		use_cache = new Feature(CACHE_FEATURE_PREFIX+tag_name, false,"Use record cache for "+tag_name).isEnabled(c);
		
		use_id = REQUIRE_ID_KEY.isEnabled(c) || new Feature(USE_ID_PREFIX+tag_name,true,"Use integer id-key for table "+tag_name).isEnabled(c);
	}
	
	public void createBackupTable(String name) throws SQLException{
		Connection c = sql.getConnection();
		
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sql.quote(sb, name);
		sb.append(" LIKE ");
		addTable(sb, true);
		try(Statement stmt = c.createStatement()){
			stmt.executeUpdate(sb.toString());
		}
	}
	private Repository backup=null;
	/** create a backup table structured like this one if backups are enabled.
	 *  
	 * @return Repository or null
	 * @throws DataFault
	 */
	public Repository getBackup() throws DataFault{
		try{
			String suffix = (String) getContext().getAttribute(BACKUP_SUFFIX_ATTR);
			if( suffix == null){
				return null;
			}
			if( backup == null ){
				String backup_name = table_name+suffix;
				createBackupTable(backup_name);
				backup = getInstance(getContext(), backup_name);
			}
			return backup;
		}catch(Exception t){
			throw new DataFault("Error creating backup repository",t);
		}
	}
	public static String TableToTag(AppContext c, String tag) {
		return c.getInitParameter("tag."+tag,tag);
	}
	/** Add the primary key name of this table to a query
	 * 
	 * @param sb   StringBuilder
	 * @param qualify boolean should we qualify with table name
	 * @param quote boolean request quoting if supported
	 * @return modified StringBuilder
	 */
	public StringBuilder addUniqueName(StringBuilder sb, boolean qualify, boolean quote){
		if( qualify ){
			addAlias(sb, quote);
			sb.append(".");
		}
		return sql.quote(sb, getUniqueIdName());
	}
	/** Add the table name to a query. 
	 * 
	 * If this method is used to construct SQL statements then Repository sub-classes
	 * can implement table name mangling if required.
	 * 
	 * Use this for schema updates but use{@link #addSource(StringBuilder, boolean)} in preference
	 * for select/update statements as qualified field names will use the alias 
	 * 
	 * @see #addSource(StringBuilder, boolean)
	 * 
	 * @param sb StringBuilder
	 * @param quote request quoting if supported
	 * @return modified StringBuilder
	 */
	public StringBuilder addTable(StringBuilder sb, boolean quote) {
		if( quote ){
			return sql.quote(sb, table_name);
		}else{
			return sb.append(table_name);
		}
	}
	/** Add the table alias
	 * 
	 * @param sb
	 * @param quote
	 * @return modified {@link StringBuilder}
	 */
	public StringBuilder addAlias(StringBuilder sb, boolean quote) {
		if( quote ){
			return sql.quote(sb, alias_name);
		}else{
			return sb.append(alias_name);
		}
	}
	/** Add the table as a query source. 
	 * 
	 * Normally the same as {@link #addTable(StringBuilder, boolean)}
	 * but is allowed to add an "AS alias" clause as well
	 * 
	 * 
	 * 
	 * @param sb
	 * @param quote
	 * @return modified {@link StringBuilder}
	 */
	public StringBuilder addSource(StringBuilder sb, boolean quote) {
		addTable(sb, quote);
		if( usesAlias()) {
			sb.append(" AS ");
			addAlias(sb, quote);
		}
		return sb;
	}

	public String getTable(){
		return addTable(new StringBuilder(),false).toString();
	}
	@SuppressWarnings("unchecked")
	public IndexedTypeProducer getSelfProducer(){
    	IndexedTypeProducer producer=null;
    	Class<? extends IndexedProducer> clazz = getContext().getPropertyClass(IndexedProducer.class, null, getTag());	
    	if( clazz != null ){
    		// use lazy creation constructor
    		producer = new IndexedTypeProducer(getUniqueIdName(),ctx,clazz,getTag());		
    	}
    	return producer;
    }
	/**
	 * Performs automatic type conversions to the canonical type as specified by
	 * the database table. This is to provide automatic casting based on the DB
	 * field type beyond that supported intrinsically in JDBC e.g. between Date
	 * and integer time fields.
	 * 
	 * Note that Filters that generate their own SQL fragments will need to call
	 * this explicitly
	 * 
	 * @param key
	 *            Database field to base conversion on
	 * @param value
	 *            input object
	 * @return converted object
	 */
	public Object convert(String key, Object value) {
		//Logger log = ctx.getLogger(getClass());
		if (value == null || key == null) {
			return value;
		}
		if( value instanceof StreamData){
			// StreamData objects are always written as binary streams
			return value;
		}
		FieldInfo info = getInfo(key);
		if (info == null) {
			return value;
		}else if( info.isDate()){
			return convertDate(value);
		}else if (   info.isNumeric() ){
			
			return convertNumber(value);

		}else if ( info.isBoolean()){
			return convertBoolean(value,null);
		}else if( info.isString()){
			if( value instanceof Boolean){
				return ((Boolean) value).booleanValue() ? "Y" : "N";
			}
			// Don't coerce to string here
			return value;
		}else{
		
			return value;
		}
	}
	@SuppressWarnings("unchecked")
	public <T> T convert(Class<? extends T> target, Object value) {
		//Logger log = ctx.getLogger(getClass());
		if (value == null || target == null) {
			return (T) value;
		}
		if( Date.class.isAssignableFrom(target)){
			return (T) convertDate(value);
		}else if (  Number.class.isAssignableFrom(target) ){
			
			return (T) convertNumber(value);
		}else if( String.class.isAssignableFrom(target)){
			return (T) value.toString();
		}else if ( Boolean.class.isAssignableFrom(target)){
			return (T) convertBoolean(value,null);
		}else{
			return (T) value;
		}
	}
	/** clear the Record cache.
	 * 
	 */
	protected void clearCache(){
		if(cache_ref == null ){
			return;
		}
		synchronized(this){
		Map<Integer,Record> cache = cache_ref.get();
		if( cache != null ){
			cache.clear();
			cache=null;
		}
		cache_ref.clear();
		cache_ref=null;
		}
	}

	protected synchronized void clearFields(){
		if( fields != null) {
			fields.clear();
			fields=null;
		}
	}
	/** perform any conversions to Date supported by this Repository.
	 * This is public so external code that queries directly can use them.
	 * 
	 * @param o Input Object
	 * @return Number
	 */
	public final Date convertDate(Object o){
		if (o != null && o instanceof Number) {
			// assume unix timestamp
			return new Date(getResolution()
					* ((Number) o).longValue());
		}
		return (Date) o;
	}
	/** Perform any object conversions to Number appropriate to thisRepository.
	 * This is public so external code that queries directly can also use them
	 * 
	 * @param o input Object
	 * @return Number
	 */
	public final Number convertNumber(Object o){
		if( o == null ){
			return null;
		}
		if( o instanceof Number ){
			return (Number) o;
		}
		if ( o instanceof Date) {
			// assume unix date in seconds
			return new Long(((Date) o).getTime()
					/ getResolution());
		}
		if( o instanceof Indexed ){
			return new Long(((Indexed)o).getID());
		}
		if( o instanceof String){
			return new Double((String)o );
		}
		return (Number) o;
	}
	public final Boolean convertBoolean(Object o, Boolean def){
		if( o == null){
			return def;
		}
		if( o instanceof Boolean){
			return (Boolean) o;
		}
		if( o instanceof Number){
			return Boolean.valueOf(((Number)o).intValue() != 0);
		}
		if( o instanceof String){
			String s = (String) o;
			if( s.equalsIgnoreCase("Y") || 
			       s.equalsIgnoreCase("True")){
				return Boolean.TRUE;
			}
			if( s.equalsIgnoreCase("N") || 
				       s.equalsIgnoreCase("False")){
					return Boolean.FALSE;
				}
		}
		return def;
	}

	
	
	/** Cache a {@link TypeProducer} in the repository.
	 * This allows the basic database meta-data to be augmented by
	 * run-time information. 
	 * 
	 * @param producer
	 */
	public void addTypeProducer(TypeProducer producer){
		FieldInfo info = getInfo(producer.getField());
		if( info != null ){
			info.setTypeProducer(producer);
		}
	}
	public boolean hasTypeProducer(String field){
		FieldInfo info = getInfo(field);
		return info.getTypeProducer() != null;
		
	}
	/** Test if we have cached metadata.
	 * 
	 * @return
	 */
	boolean hasMetaData(){
		return fields != null;
	}
	/** eject a Record form the record cache.
	 * 
	 * @param id
	 */
    private  void ejectCache(int id){
    	if( ! use_cache ){
    		return;
    	}
    	synchronized(this){
    	Map<Integer,Record> cache = getCache();
    	if( cache != null ){
    		cache.remove(id);
    		cache=null;
    	}
    	}
    }
   
    /** Query cache  presence for testing.
     * 
     * @param id
     */
    boolean isCached(int id){
    	if( use_cache ){


    		synchronized(this){
    			Map<Integer,Record> cache = getCache();
    			if( cache != null ){
    				boolean result= cache.containsKey(id);
    				cache=null;
    				return result;
    			}
    		}
    	}
    	return false;
    }
	/** Get the ResultSet corresponding to a Record id
	 * 
	 * @param id record-id to find. 
	 * @param required Do we need a result
	 * @return ResultSet
	 * @throws DataException
	 */
	synchronized private ResultSet findRecord(int id,boolean required) throws DataException{
		AppContext conn=getContext();
		TimerService timer = conn.getService(TimerService.class);
		try{
			
			if( timer != null ){
			  timer.startTimer(getTag()+"-find");
			}
			if( find_statement == null ){
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT * FROM ");
				addTable(sb, true);
				sb.append(" WHERE ");
				addUniqueName(sb, false, true);
				sb.append("=?");
				find_statement = sql.getConnection().prepareStatement(
						sb.toString(), 
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
			}
			find_statement.setInt(1, id);
			ResultSet rs = find_statement.executeQuery();
			if ( ! rs.next() ){
				if( ! required){
					return null;
				}
				rs.close();
				throw new DataNotFoundException("No record with specified ID "+getTag()+":"+getUniqueIdName()+"="+id);
			}
			return rs;
		}catch(SQLException e){
			
			sql.getService().handleError("Exception in findRecord",e);
			return null; // actually unreachable
		}finally{
			if( timer != null){
				timer.stopTimer(getTag()+"-find");
			}
		}
	}
	/**
	 * get the AppContext associated with this Repository.
	 * 
	 * @return the AppContext
	 */
	public AppContext getContext() {
		return ctx;
	}

	/** return the Record cache or null
	 * Even if caching is enabled this may still return null under 
	 * heavy memory pressure.
	 * 
	 * @return
	 */
	private final Map<Integer,Record> getCache(){
		
		if( ! use_cache ){
			return null;
		}
		synchronized(this){
		if( cache_ref == null || cache_ref.get() == null){
			cache_ref = new SoftReference<>(new HashMap<Integer,Record>());
		}
	    // there is a potential race condition here as the reference may be cleared 
		// by the gc between the previous line and the next. 
		return cache_ref.get();
		}
	}
	
	public final void flushCache() {
		if( use_cache) {
			synchronized (this) {


				if( cache_ref != null) {
					Map<Integer,Record> map = cache_ref.get();
					if( map != null) {
						map.clear();
					}
				}
				cache_ref=null;
			}
		}
	}
	/**
	 * get the list of Record fields for this table in canonical order.
	 * 
	 * @return Set of field names or null if Repository invalid
	 */
	public Set<String> getFields() {
		if (fields == null) {
			setMetaData();
		}
		if( fields == null ){
			return null;
		}
		return fields.keySet();
	}
	public boolean hasField(FieldInfo info){
		return fields.containsValue(info);
	}
	public Set<String> getIndexNames(){
		if( indexes == null ){
			setIndexes();
		}
		if( indexes == null ){
			return null;
		}
		// preserve order
		return new LinkedHashSet<>(indexes.keySet());
	}
	public IndexInfo getIndexInfo(String name){
		if( indexes == null ){
			setIndexes();
		}
		if( indexes == null ){
			return null;
		}
		return indexes.get(name);
	}
	/** Get all the FieldInfo objects for this Repository
	 * 
	 * @return Collection
	 */
	public Collection<FieldInfo> getInfo(){
		if (fields == null) {
			setMetaData();
		}
		if( fields == null ){
			return null;
		}
		return fields.values();
	}

	/**
	 * get the FieldInfo object for a field null key always returns a null
	 * result
	 * 
	 * @param key
	 * @return FieldInfo
	 */
	public FieldInfo getInfo(String key) {
		if (key == null) {
			return null;
		}
		if (fields == null) {
			setMetaData();
		}
		return fields.get(key);
	}
	/** get a {@link Order} element corresponding to a key.
	 * 
	 * @param key field to order by (null for primary key).
	 * @param desc
	 * @return {@link Order}
	 */
	public Order getOrder(String key,boolean desc){
		FieldInfo info = getInfo(key);
		if( info == null ){
			return new Order(desc,null);
		}
		return new Order(desc,info);
	}
	public final SQLContext getSQLContext(){
		return sql;
	}
	/** Return the number of milliseconds per tick to use when using an integer type to
	 * specify a date or vice versa.
	 * @return the resolution
	 */
	public long getResolution() {
		return resolution;
	}

	/** Get the Tag string used to reference this Repository
	 * 
	 * @return String
	 */
	public String getTag() {
		return tag_name;
	}
	/** get the tag used to qualify configuration parameters.
	 * This is normally the same as the reference tag but can be overridden
	 * to make it easy to support rolled-tables.
	 * 
	 * @return String tag
	 */
	public String getParamTag(){
		return param_name;
	}
	/** Get the tag used to retrieve the correct database connection for this table from a
	 * {@link DatabaseService}
	 * 
	 * @return tag
	 */
	public final String getDBTag(){
		return db_tag;
	}

	

	/**
	 * get the field name of the primary key
	 * 
	 * @return String
	 */
	protected String getUniqueIdName(){
		if (id_name == null) {
			setMetaData();
		}
		return id_name;

	}
	/** Is this the primary key field.
	 * 
	 * Safer to provide a test than a query method
	 * as it can't be used to write a ad-hoc sql query.
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean isUniqueIdName(String name){
		if (id_name == null) {
			setMetaData();
		}
		return id_name.equals(name);
	}
	protected String getUniqueIdName(boolean qualify) {
		if (id_name == null) {
			setMetaData();
		}
		if( qualify ){
			return qualified_id_name;
		}
		return id_name;
	}

	/**
	 * Is the specified object a valid Field key for this table.
	 * 
	 * @param key
	 * @return boolean true if key is valid
	 */
	public boolean hasField(String key) {
		if (fields == null) {
			setMetaData();
		}
		assert(key != null);
		return fields.containsKey(key);
	}
	/** Is there a named index of the specified name for this table.
	 * 
	 * @param index
	 * @return boolean
	 */
	public boolean hasIndex(String index){
		if( indexes == null){
			setIndexes();
		}
		if( indexes == null ){
			return false;
		}
		return indexes.containsKey(index);
	}
	/** get a {@link NumberFieldExpression} for a field.
	 * 
	 * @param target desired numeric type for the field.
	 * @param key field name
	 * @return {@link NumberFieldExpression}
	 */
	public <T extends Number,X extends DataObject> NumberFieldExpression<T,X> getNumberExpression(Class<T> target,String key){
		FieldInfo info = getInfo(key);
		if( info == null || ! info.isNumeric()){
			throw new ConsistencyError("Invalid numeric field "+getTag()+"."+key);
		}
		return new NumberFieldExpression<>(target,this,key);
	}
	/** get a {@link BooleanFieldExpression} for a field
	 * 
	 * @param key field name
	 * @return {@link BooleanFieldExpression}
	 */
	public <X extends DataObject> BooleanFieldExpression<X> getBooleanExpression(String key){
	
		FieldInfo info = getInfo(key);
		if( info == null || ! info.isBoolean()){
			throw new ConsistencyError("Invalid boolean field "+getTag()+"."+key);
		}
		return new BooleanFieldExpression<>(this,key);
	}
	/** get a {@link StringFieldExpression} for a field
	 * 
	 * @param key field name
	 * @return {@link StringFieldExpression}
	 */
	public <X extends DataObject> StringFieldExpression<X> getStringExpression(String key) {
		
		FieldInfo info = getInfo(key);

		if( info == null ){
			// note all types can be treated as strings
			throw new ConsistencyError("Invalid string field "+getTag()+"."+key);
		}
		return new StringFieldExpression<>(this,key);
		
	}
	/** get a {@link Date} valued {@link FieldValue} for a field.
	 * 
	 * The underlying database field may be a time-stamp or a numeric field.
	 * 
	 * @param key field name
	 * @return {@link FieldValue}
	 */
	public <X extends DataObject> FieldValue<Date,X> getDateExpression(String key) {
		
		FieldInfo info = getInfo(key);
		if( info == null ){
			throw new ConsistencyError("Invalid date field");
		}
		if( info.isDate()){
		   return new DateFieldExpression<>(this,key);
		}
		if( info.isNumeric()){
			   return new TimestampDateFieldExpression<>(this, key);
		}
		throw new ConsistencyError("Invalid date field");
	}
	
	
	
	/** Get a {@link IndexedFieldValue} for a reference field
	 * 
	 * @param key
	 * @return IndexedFieldValue
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataObject> IndexedFieldValue getReferenceExpression(String key){
		
		FieldInfo info = getInfo(key);
		if( info == null || ! info.isNumeric() || ! info.isReference()){
			throw new ConsistencyError("Invalid reference field "+getTag()+"."+key);
		}
		TypeProducer prod = info.getTypeProducer();
		if( prod != null && prod instanceof IndexedTypeProducer){
			return new IndexedFieldValue(this,(IndexedTypeProducer)prod);
		}
		throw new ConsistencyError("Invalid reference field "+getTag()+"."+key);
	}
	public <T extends DataObject,O,D> TypeProducerFieldValue<T,O,D> getTypeProducerExpression(Class<O> type,TypeProducer<O,D> prod){
		return new TypeProducerFieldValue<>(this, type,prod);
	}
	/** Get a {@link IndexedFieldValue} for a field
	 * The field does not have to be tagged as a reference field
	 * @param key
	 * @param prod
	 * @return IndexedFieldValue
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataObject,I extends DataObject> IndexedFieldValue<T,I> getReferenceExpression(String key,IndexedProducer<I> prod){
		
		FieldInfo info = getInfo(key);
		if( info == null || ! info.isNumeric() ){
			throw new ConsistencyError("Invalid reference/numeric field "+getTag()+"."+key);
		}
		IndexedTypeProducer producer = new IndexedTypeProducer(ctx, key, prod);
		TypeProducer typeProducer = info.getTypeProducer();
		if( info.isReference() && ! typeProducer.equals(producer)){
			throw new ConsistencyError("Incompatible producer specified for field "+getTag()+"."+key+" "+typeProducer.toString()+"!="+producer.toString());
		}
		return new IndexedFieldValue<T,I>(this,producer);
		
	}

	/** Default insert operation that uses Generated Keys to 
	 * obtain the unique id. Not all DBs support this but it is a good default.
	 * 
	 * If the id value is greater than zero this is taken as a required id to be inserted.
	 * 
	 * @param r
	 * @return
	 * @throws DataFault
	 */
	protected int insert(Record r) throws DataFault {
			if( READ_ONLY_FEATURE.isEnabled(ctx) || sql.isReadOnly()){
				return -1;
			}
			TimerService time = ctx.getService(TimerService.class);
			if( time != null ){
				time.startTimer(getTag()+"-insert");
			}
	        int id;
			// Ok, now we should save the object in the database before
			// anything else happens
			StringBuilder query = new StringBuilder("INSERT INTO ");
			addTable(query, true);
			query.append(" (");
			StringBuilder query_values = new StringBuilder(") VALUES (");
			boolean atleastone = false;
			if( r.id > 0 ){
				addUniqueName(query, false, true);
				query_values.append('?');
				atleastone=true;
			}
			for (Iterator<String> it = getFields().iterator(); it.hasNext();) {
				String field =  it.next();
				FieldInfo info = getInfo(field);
                // as this is an insert we should skip null fields and allow the database default to
				// take precedence
				if( r.get(field) != null){
					if (atleastone) {
						query.append(", ");
						query_values.append(", ");
					} else {
						atleastone = true;
					}
					info.addName(query, false, true);
					query_values.append('?');


				}
			}
			query.append(query_values.toString());
			query.append(')');
			if( ! atleastone && AT_LEAST_ONE.isEnabled(ctx)){
				throw new DataFault("Insert with no values");
			}
			
			try(PreparedStatement stmt = sql.getConnection().prepareStatement(
					query.toString(), Statement.RETURN_GENERATED_KEYS)) {
				int pos = 1;
				if( r.id > 0 ){
					stmt.setInt(pos, r.id);
					pos++;
				}
				for (Iterator it = getFields().iterator(); it.hasNext();) {
					String field = (String) it.next();
					if( r.get(field) != null ){
						r.setValue(query, stmt, pos, field);
						pos++;
					}
				}
				if( DatabaseService.LOG_INSERT_FEATURE.isEnabled(getContext())){
					LoggerService serv = getContext().getService(LoggerService.class);
					if( serv != null ){
						serv.getLogger(getClass()).debug("insert query is "+query.toString());
					}
				}
				long start = System.currentTimeMillis();
				int count = stmt.executeUpdate();
				long end = System.currentTimeMillis();
				if( time != null ){
					time.stopTimer(getTag()+"-insert");
				}
				if( (end - start) > 60000L ) {
					LoggerService serv = getContext().getService(LoggerService.class);
					if( serv != null ){
						serv.getLogger(getClass()).error("long running insert query "+Long.toString(end-start)+" "+query.toString());
					}
				}
				if (count != 1) {
					throw new DataFault("Wrong count from INSERT");
				}
				if( r.id > 0 ){
					//know the id 
					return r.id;
				}
				if( use_id ){
					try(ResultSet rs = stmt.getGeneratedKeys()){
						if (rs.next()) {
							id = rs.getInt(1);
						} else {
							throw new DataFault("cannot retrieve auto_key");
						}
					}
					return id;
				}else{
					return 0;
				}
			} catch (SQLException e) {
				sql.getService().handleError("Insert exception " + query.toString(), e);
				return 0; // actually unreachable
			}
	}
	
	/**
	 * control if it is an error to set a property with no corresponding
	 * database field.
	 * 
	 * @param f
	 *            boolean
	 * @return previous value
	 */
	public boolean setAllowBogusPut(boolean f) {
		boolean old = allow_bogus_put;
		allow_bogus_put = f;
		return old;
	}
	/**
	 * control if it is an error to set a property with a null value
	 * 
	 * @param f
	 *            boolean
	 * @return previous value
	 */
	public boolean setAllowNull(boolean f) {
		boolean old = allow_null_value;
		allow_null_value = f;
		return old;
	}
	/**
	 * populate an object from a ResultSet
	 * 
	 * It seems to work if we always qualify the field names but its slower
	 * @param r 
	 * 
	 * @param rs
	 *            ResultSet
	 * @param qualify
	 *            boolean qualify the field names with the table name as
	 *            ResultSet is from a join
	 * @throws DataFault
	 * @throws DataNotFoundException 
	 * 
	 * @throws ConsistencyError
	 */
	public void setFromResultSet(Record r,ResultSet rs, boolean qualify)
			throws DataFault, DataNotFoundException {
		int id;
		synchronized(r){
		r.clear();
		try {
			/*
			 * This is more robust though its slightly faster to get fields
			 * by col number
			 */
			if( use_id ){
				String uniqueIdName = getUniqueIdName(qualify);
				id = rs.getInt(uniqueIdName);
				r.setInitialID(id);

				// for backwards compatibility

				if (id <= 0) {
					// This can happen with a join used to pre-populate a cached link
					// when the reference value is invalid
					throw new DataNotFoundException("No ID value found "+uniqueIdName+":"+id);
				}
			}else{
				id=0;
			}
			
//			Logger log=null;
//			if( ctx.isFeatureOn("log_fetch")){
//				log=ctx.getService(LoggerService.class).getLogger(getClass());
//			}

			for (Iterator<String> i = getFields().iterator(); i.hasNext();) {
				String field =  i.next();
				FieldInfo info =  fields.get(field);
				Object value=rs.getObject(info.getName(qualify));
				
				
				if (value != null) {
					if (info.isData()) {
						if (value instanceof Blob) {
							value = new BlobStreamData(ctx, (Blob) value);
						}else if( value instanceof byte[]) {
							
							value = new ByteArrayStreamData((byte[]) value);
						} else {
							throw new DataFault("Unexpected Blob/data type "
									+ value.getClass().getName());
						}
					}
//					if( log != null ){
//						log.debug(info.getName(true)+" is "+value.toString()+" "+value.getClass().getCanonicalName());
//					}
					// want these to be clean by default
					r.rawPut(field, value);
				}
			}

		} catch (SQLException e) {
			r.clear();
			getSQLContext().getService().handleError("Exception in setContents", e);
			return;
		}
		// store this object in the cache if appropriate
		if( use_cache  && use_id){
			synchronized (this) {
				Map<Integer,Record> cache;
				Integer key = id;
				if( (cache = getCache()) != null && ! cache.containsKey(key)){
					// Store copy so cached copy not changed
					cache.put(key, (Record) r.clone());
					cache=null;
				}
			}
		}
		}
	}

	/**
	 * populate the MetaData from scratch
	 * 
	 */
	synchronized private void setMetaData() {
		if( fields == null){
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM ");
			addTable(sb, true);
			sb.append(" WHERE 1=0");
			Connection c = sql.getConnection();
			try(Statement stmt=c.createStatement(); ResultSet rs = stmt.executeQuery(sb.toString())) {
				setMetaData(rs);
				setReferences(ctx,c);
				if(CHECK_INDEX.isEnabled(getContext())) {
					setIndexes();
				}
			}catch( SQLNonTransientConnectionException nt) {
				throw new FatalDataError("Connection error in setMetaData for "+getTag(),nt);
			}catch( SQLSyntaxErrorException se) {
				// This occurs when table does not exist
				throw new NoTableException(getTable(), se);
			} catch (Exception e) {
				//ctx.error(e, "Error creating MetaData for " + getTag());
				throw new DataError("Error in setMetaData for "+getTag(),e);
			}
		}
	}
	synchronized private void setIndexes(){
		DatabaseService db_serv = getContext().getService(DatabaseService.class);
		try{
			Map<String,IndexInfo> result = new LinkedHashMap<>();
			
			Connection c = db_serv.getSQLContext().getConnection();
			DatabaseMetaData md = c.getMetaData();
			try(ResultSet rs = md.getIndexInfo(null, null, table_name, false, true)){
			while( rs.next()){
				String name = rs.getString("INDEX_NAME");
				if( ! name.equals("PRIMARY")){
					boolean unique = ! rs.getBoolean("NON_UNIQUE");
					IndexInfo info = result.get(name);
					if( info == null ){
						info = new IndexInfo(name, unique);
						result.put(name, info);
					}
					int pos = rs.getInt("ORDINAL_POSITION");
					String col  = rs.getString("COLUMN_NAME");
					info.addCol(pos, col);
				}
				
			}
			}
			// Mark any fields we know to be unique
			for( IndexInfo i : result.values()) {
				if(i.unique && i.cols.size() == 1) {
					String f = i.cols.get(0);
					FieldInfo fi = getInfo(f);
					if( fi != null ) {
						fi.setUnique(true);
					}
				}
			}
			indexes=result;
		}catch(SQLException e){
			db_serv.logError("Error getting index names", e);
			throw new FatalDataError("Error getting index names",e);
		}
	}
	/** Set the table References for the fields  
	 * 
	 * @param ctx
	 * @param c
	 * @throws SQLException 
	 */
	private void setReferences(AppContext ctx, Connection c) throws SQLException{
		Logger log = ctx.getService(LoggerService.class).getLogger(getClass());
		//log.debug("SetReferences for "+getTable());
		// look for foreign keys to identify remote tables.
		DatabaseMetaData meta = c.getMetaData();
		ResultSet rs = meta.getImportedKeys(c.getCatalog(),null , table_name);
		if(rs.first()){
			//log.debug("Have foreign key");
		do{
			String field=rs.getString("FKCOLUMN_NAME");
			String table=rs.getString("PKTABLE_NAME");
			String key_name = rs.getString("FK_NAME");
			short seq = rs.getShort("KEY_SEQ");
			if( seq == 1 ){
				FieldInfo info = fields.get(field);
				if(info.isNumeric()){
					String suffix = param_name+"."+info.getName(false);
					String name = REFERENCE_PREFIX+suffix;
					table=ctx.getInitParameter(name,table); // use param in preference because of windows case mangle
					String tag = TableToTag(ctx, table);
					//log.debug("field "+field+" references "+table+"->"+tag);
					info.setReference(true,key_name,tag,ctx.getBooleanParameter(UNIQUE_PREFIX+suffix, info.isUnique()));
				}
				
			}
		}while(rs.next());
		}
		// now try explicit references set from properties
		for(FieldInfo i  : fields.values()){
			if( i.isNumeric() && i.getReferencedTable() == null ){
				//use param name for table rename
				String suffix = param_name+"."+i.getName(false);
				String tag = REFERENCE_PREFIX+suffix;
				String table=ctx.getInitParameter(tag);
				//log.debug("tag "+tag+" resolves to "+table);
				i.setReference(false,null,table,ctx.getBooleanParameter(UNIQUE_PREFIX+suffix, false));
			}
			if( i.isString()) {
				String suffix = param_name+"."+i.getName(false);
				String name = TRUNCATE_PREFIX+suffix;
				i.setTruncate(ctx.getBooleanParameter(name, false));
			}
		}
	}

	/**
	 * Use a ResultSet to populate the MetaData information
	 * 
	 * @param rs
	 * @throws SQLException
	 * @throws ConsistencyError
	 */
	private void setMetaData(ResultSet rs) throws SQLException,
			ConsistencyError {
		assert(fields == null);
		fields = new LinkedHashMap<>();
		ResultSetMetaData meta_data = rs.getMetaData();
		int md_columns = meta_data.getColumnCount();
		boolean seen_key = false;
		// Logger log = ctx.getLogger(getClass());
		for (int i = 1; i <= md_columns; i++) {
			String returned_name = meta_data.getTableName(i);
			if (returned_name.length() > 0 && !returned_name.equalsIgnoreCase(table_name)) {
				throw new ConsistencyError("Table names do not match "
						+ getTag() + "!=" + returned_name);
			}
			// if we don't know for sure assume no nulls
			boolean can_null = (meta_data.isNullable(i) == ResultSetMetaData.columnNullable);
			String name = meta_data.getColumnName(i);
			if (!seen_key && meta_data.isAutoIncrement(i)) {
				seen_key = true;
				id_name = name;
			} else {
				// log.debug("Metadata "+name+" "+meta_data.getColumnType(i));
				int columnType = meta_data.getColumnType(i);
				int columnDisplaySize = meta_data.getColumnDisplaySize(i);
				fields.put(dbFieldtoTag(name), new FieldInfo(name,
						columnType, 
						columnDisplaySize, can_null));
			}
		}
		if (use_id && !seen_key) {
			// Note we need an up-to-date mysql driver for the isAutoIncrement
			// method to work properly. otherwise default to first col and hope
			id_name = meta_data.getColumnName(1);
			fields.remove(dbFieldtoTag(id_name));
		}
		// cache the qualified form as this is used frequently
		StringBuilder sb = new StringBuilder();
		sb.append(alias_name);
		sb.append(".");
		sb.append(id_name);
		qualified_id_name = sb.toString();
	}
	/** Map the actual name of the DB field to the tag used in the code.
	 * Normally this is the identity but this method allows field renaming.
	 * 
	 */
	public String dbFieldtoTag(String name){
		return ctx.getInitParameter("rename."+table_name+"."+name, name);
	}
	
	/** Map a tag name to the actual database table.
	 * @param ctx 
	 * 
	 * @param tag
	 * @return table name
	 */
	public static String tagToTable(AppContext ctx,String tag){
		return ctx.getInitParameter("table."+tag, tag);
	}
	
	/**
	 * Add an object to a PreparedStatement using metadata info to get the
	 * desired type. This does <em>not</em> call convert implicitly.
	 * 
	 * @param stmt
	 *            PreparedStatement
	 * @param pos
	 *            position to add
	 * @param key
	 *            field to match type to or null if unspecified
	 * @param value
	 *            value to add
	 * @throws SQLException
	 */
	final void setObject(PreparedStatement stmt, int pos, String key,
			Object value) throws SQLException {
		if (value instanceof StreamData) {
			StreamData s = (StreamData) value;
			stmt.setBinaryStream(pos, s.getInputStream(), (int) s
					.getLength());
			return;
		}
		FieldInfo f = getInfo(key);
		//Logger log = ctx.getService(LoggerService.class).getLogger(getClass());
		if (f != null) {
			//log.debug("setObject "+pos+","+value+","+f.getType());
			stmt.setObject(pos, f.truncate(value), f.getType());
		} else {
			//log.debug("setObject "+pos+","+value);
			stmt.setObject(pos, value);
		}
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(long resolution) {
		this.resolution = resolution;
	}

	/** Does this repository support id fields.
	 * 
	 * Without this the table must be read only
	 * 
	 * @return boolean
	 */
	public boolean useID(){
		return use_id;
	}
	
	public boolean usesCache(){
		return use_cache;
	}
	/**
	 * 
	 * @return
	 */
	public boolean usesAlias() {
		return ! table_name.equals(alias_name);
	}
	/** Static method to result a field from a result set 
	 * with a specified target class
	 * 
	 * @param <T>
	 * @param target
	 * @param rs
	 * @param pos
	 * @return T
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T makeTargetObject(Class<T> target, ResultSet rs, int pos) throws SQLException{
		
			T result;
			if( target == Date.class){
				Timestamp timeStamp = rs.getTimestamp(pos);
				if (timeStamp != null) {
					//Use Timestamp so as not to get date and time info.
					// For safety convert to a proper java.util.date 
					result = (T) new Date(timeStamp.getTime());
					
				} else {
					result = null;
					
				}
			}else if( target == String.class){
				result= (T) rs.getString(pos);
			}else if( target == Double.class){
				result=(T) Double.valueOf(rs.getDouble(pos));
			}else if( target == Float.class){
				result= (T) Float.valueOf(rs.getFloat(pos));
			}else if( target == Integer.class){
				result= (T) Integer.valueOf(rs.getInt(pos));
			}else if( target == Long.class){
				result=(T) Long.valueOf(rs.getLong(pos));
			}else if( target == Duration.class){
				result =(T) new Duration(rs.getLong(pos),1);
			}else{
			     result= (T) rs.getObject(pos);
			}
			assert(result == null || target.isAssignableFrom(result.getClass()));
			
			return result;
	}
	/**
	 * compare two objects by their String Number or Date value Other kinds of
	 * object are assumed to be different, even if they are the same object
	 * their internal state might have changed since it was returned so err on
	 * the side of caution
	 * 
	 * @param o1
	 *            first Object
	 * @param o2
	 *            second Object
	 * @return boolean true if the same
	 */
	private static boolean compare(Object o1, Object o2) {
		if( o1 == null ){
			return o2 == null;
		}else{
			if( o2 == null ){
				return false;
			}
		}
		if (o1 instanceof Number && o2 instanceof Number) {
			return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
		}
		if( o1.getClass() == o2.getClass()){
			return o1.equals(o2);
		}
		return false;
	}

	

	

	/** Get a foreign key descriptor by tag.
	 * 
	 * @param c    AppContext
	 * @param tag  String
	 * @param quote 
	 * @return descriptor or null
	 */
	public static String getForeignKeyDescriptor(AppContext c,String tag, boolean quote){
		try{
			Repository res = getInstance(c, tag);
			if( res == null ){
				return null;
			}
			StringBuilder sb = new StringBuilder();
			res.addTable(sb, quote);
			sb.append("(");
			res.addUniqueName(sb, false, quote);
			sb.append(")");
			return sb.toString();
		}catch(Exception t){
			// by default just skip foreign key
			// probably a missing table
			return null;
		}
	}
	
	/**
	 * Factory method for Repositories this is to cache Repositories in the
	 * AppContext to reduce MetaData queries This also means that for a given
	 * AppContext there is only one Repository per table.
	 * If we wished to add additional Repository sub-classes we would need to add parameter based class selection
	 * here.
	 * @param c
	 * @param tag
	 * @return Repository
	 */
	static Repository getInstance(AppContext c, String tag) {
		Repository r = null;
		if( tag == null || tag.trim().length()==0){
			throw new ConsistencyError("Tried to create Repository with empty tag");
		}
		synchronized(c){
		Tag key = new Tag(tag);
		r = (Repository) c.getAttribute(key);
		if (r == null) {
			try{
				r = new Repository(c, tag);
				c.setAttribute(key, r);
			}catch(Exception e){
				c.error(e,"Error making repository");
				return null;
			}
		}
		}
		return r;
	}
	
	public static void flushCaches(AppContext c) {
		for( Entry e : c.getAttributes().entrySet()) {
			if( e.getKey() instanceof Tag) {
				Repository res = (Repository) e.getValue();
				res.flushCache();
			}
		}
	}
	
   /** Clear the cached repository
	 * 
	 * @param c
	 * @param tag
	 */
	public static void reset(AppContext c, String tag){
		Tag key = new Tag(tag);
		Repository res = (Repository) c.getAttribute(key);
		if( res != null ) {
			res.cleanup();
		}
		c.removeAttribute(key);
	}
	
	/** Used as AppContext attribute key for Repository.
	 * 
	 * As the class is private other classes cannot access the attribute.
	 * 
	 * @author spb
	 *
	 */
    private static final class Tag{
    	@Override
		public boolean equals(Object obj) {
    		
			return obj.getClass() == getClass() && ((Tag) obj).tag.equals(tag);
		}
		@Override
		public int hashCode() {
			return tag.hashCode();
		}
		private final String tag;
    	private Tag(String s){
    		// we match tags case insensitive so case flattened tables names will retrieve the
    		// same repository as directly generated tags.
    		// Only used in tests where we loop over all tables to create dumps.
    		tag=s.toLowerCase(Locale.ENGLISH);
    	}
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tag_name == null) ? 0 : tag_name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Repository other = (Repository) obj;
		if (tag_name == null) {
			if (other.tag_name != null)
				return false;
		} else if (!tag_name.equals(other.tag_name))
			return false;
		if (alias_name == null) {
			if (other.alias_name != null)
				return false;
		} else if (!alias_name.equals(other.alias_name))
			return false;
		return true;
	}
	@Override
	public String toString(){
		return "Repository-"+alias_name+"["+table_name+"]";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
		flushCache();
		clearFields();
		if( indexes != null) {
			indexes.clear();
			indexes=null;
		}
	    try {
			if(find_statement != null && ! find_statement.isClosed()) {
				find_statement.close();
			}
		} catch (SQLException e) {
			ctx.getService(DatabaseService.class).logError("Error closing find_Statement",e);
		}
		find_statement=null;
	}
    public static String typeName(int type) {
    	switch(type) {
    	case(Types.INTEGER): return "Integer";
    	case(Types.BIGINT): return "BigInteger";
    	case(Types.BOOLEAN): return "Boolean";
    	case(Types.TIMESTAMP): return "Timestamp";
    	case(Types.DATE): return "Date";
    	case(Types.TIME): return "Time";
    	case(Types.FLOAT): return "Float";
    	case(Types.DOUBLE): return "Double";
    	case(Types.CHAR): return "Char";
    	case(Types.VARCHAR): return "Varchar";
    	case(Types.LONGVARCHAR): return "LongVarChar";
    	case(Types.BLOB): return "Blob";
    	case(Types.VARBINARY): return "VarBianry";
    	case(Types.LONGVARBINARY): return "LongVarBinary";
    	default: return "Unknown";
    	}
    }
}