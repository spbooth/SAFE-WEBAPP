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
package uk.ac.ed.epcc.webapp.model.data;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;










import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.IndexInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;

/** Class to dump {@link DataObject}s to files.
 * 
 * Mostly this works at the {@link Repository} level 
 * 
 * References are followed and the dependencies dumped in a depth-first order as well so that
 * self-consistant subsets of data can be extracted.
 * 
 * polymorphic reference fields (which can point to multiple tables) and reference fields that do not have their peer table set
 * in a foreign key or config parameter cannot be followed. 
 * 
 * @author spb
 *
 */
public class Dumper implements Contexed{

	/**
	 * 
	 */
	public static final String DELETED_ATTR = "deleted";
	public static final String COLUMN = "Column";
	public static final String DOUBLE_TYPE = "Double";
	public static final String FLOAT_TYPE = "Float";
	public static final String LONG_TYPE = "Long";
	public static final String INTEGER_TYPE = "Integer";
	public static final String DATE_TYPE = "Date";
	public static final String BOOLEAN_TYPE = "Boolean";
	public static final String STRING_TYPE = "String";
	public static final String BLOB_TYPE ="Blob";
	public static final String INDEX_TYPE="Index";
	public static final String REFERENCE_ATTR = "reference";
	public static final String DEFAULT_ATTR ="default"; // This can't be generated easily but this is the attribute the parser understands
	public static final String TYPE_ATTR = "type";
	public static final String MAX_ATTR = "max";
	public static final String NULLABLE_ATTR = "nullable";
	public static final String UNIQUE_ATTR = "unique";
	public static final String NAME_ATTR = "name";
	public static final String NULL_VALUE_ATTR = "null";
	public static final String ID = "id";
	public static final String TABLE_SPECIFICATION = "TableSpecification";
	public Dumper(AppContext conn,SimpleXMLBuilder builder) {
		super();
		this.conn = conn;
		this.builder=builder;
		seen = new HashMap<String, Set<Integer>>();
		specifications = new HashMap<>();
	}

	private final AppContext conn;
    private final SimpleXMLBuilder builder;
    /** records that have already been processed so should
     * not be dumped if referenced again.
     */
    private final Map<String,Set<Integer>>seen;
    /** A map of externally generated specifications These are optional but can augment
     * the dump
     */
    private final Map<String,TableSpecification> specifications;
    private boolean dump_null_values=false;
    private boolean verbose_diff=true;
    
	public AppContext getContext() {
		return conn;
	}
	public void dump(DataObject obj) throws DataFault, IOException{
		dump(obj.record);
	}
	
	public void addSpecification(String table, TableSpecification spec){
		specifications.put(table, spec);
	}
	/** add an entry to the record of "seen" records
	 * @param res {@link Repository} if not null dump this schema when new table encountered
	 * @param tag
	 * @param id if greater than zero record is marked as seen (otherwise just table marked as seen)
	 * @return true if this a new record
	 */
	private boolean register(Repository res, String tag, int id){
		
		// Force keys to lower case as tags might be generated from table names rather than construction tags
		// and windows mysql maps tags to lower case
		String key = tag.toLowerCase(Locale.ENGLISH);
		Set<Integer> dumped = seen.get(key);
		if( dumped == null ){
			if( res != null ){ 
				dumpSchema(res);
			}
			dumped = new HashSet<Integer>();
			seen.put(key, dumped);
		}
		if( id <= 0 || dumped.contains(id)){
			// already dumped this or invalid
			return false;
		}
		// record before dumping in case of circular deps.
		dumped.add(id);
		return true;
	}
	/** Has a specified record already been processed.
	 * 
	 * @param tag
	 * @param id
	 * @return
	 */
	public boolean beenSeen(String tag,int id) {
		String key = tag.toLowerCase(Locale.ENGLISH);
		Set<Integer> dumped = seen.get(key);
		if( dumped == null ){
			return false;
		}
		return dumped.contains(id);
	}
	/** Mark a record as "seen" so it will be excluded if encountered in the dump
	 * process.
	 * 
	 * @param tag
	 * @param id
	 */
	public void markSeen(String tag,int id){
		register(null,tag,id);
	}
	protected void dump(Repository.Record rec) throws DataFault, IOException{
		Repository res = rec.getRepository();
		int id = rec.getID();
		String tag = res.getTag();
		if( register(res, tag, id)){

			SimpleXMLBuilder sb = builder.getNested();
			sb.open(tag);
			sb.attr(ID, Integer.toString(id));
			sb.clean("\n");
			for(FieldInfo field : res.getInfo()){
				String name = field.getName(false);
				if( field.isReference()){
					TypeProducer prod = field.getTypeProducer();
					if( prod != null){
						Object val = rec.getProperty(prod);
						if( val instanceof DataObject) {
							DataObject remote = (DataObject) val;
							if( remote != null ){
								// write dependencies before record
								dump(remote.record);

							}
						}
					}
				}
				String dat;

				dat = field.dump(rec);
				if( dat != null ){
					sb.open(name);
					sb.clean(dat);
					sb.close();
					sb.clean("\n");
				}else if( dump_null_values) {
					sb.open(name);
					sb.attr(NULL_VALUE_ATTR, "true");
					sb.close();
				}


			}
			sb.close();
			sb.clean("\n");
			sb.appendParent();
		}
	}
	/** Compare a {@link Record} with a baseline.
	 * 
	 * If the records are the same, record is just marked as seen.
	 * If they differ dump the fields that have changed.
	 * 
	 * @param rec
	 * @param orig
	 */
	
	private boolean compare(String a, String b) {
		if( a == null ) {
			return b==null;
		}
		if( b == null ) {
			return false;
		}
		return a.equals(b);
	}
	protected boolean dumpDiff(Repository.Record rec,Repository.Record baseline) throws DataFault, IOException{
		if( rec == null) {
			// assume this means the record has been deleted
			int id = baseline.getID();
			String tag = baseline.getRepository().getTag();
			builder.open(tag);
			builder.attr(ID, Integer.toString(id));
			builder.attr(DELETED_ATTR, "true");
			builder.close();
			return true;
		}
		Repository res = rec.getRepository();
		int id = rec.getID();
		String tag = res.getTag();
		boolean differs=false;

		SimpleXMLBuilder sb = builder.getNested();
		sb.open(tag);
		sb.attr(ID, Integer.toString(id));
		sb.clean("\n");
		for(FieldInfo field : res.getInfo()){
			String name = field.getName(false);
			String modified = field.dump(rec);
			String baseline_value = field.dump(baseline);
			boolean field_differs = ! compare(modified, baseline_value);
			if( field_differs) {
				differs=true;
			}
			if( field_differs || verbose_diff) {
				if( modified != null ){
					sb.open(name);
					sb.clean(modified);
					sb.close();
					sb.clean("\n");
				}else if( field_differs || dump_null_values){
					// Always mark a null if the field changed
					sb.open(name);
					sb.attr(NULL_VALUE_ATTR, "true");
					sb.close();
				}
			}
		}
		sb.close();
		sb.clean("\n");
		if( differs) {
			// add diff to dump
			sb.appendParent();
		}
		markSeen(tag, id);
		return differs;
	}
	public void dumpAll(Repository res) throws ConsistencyError, DataException, IOException{
		StringBuilder query= new StringBuilder();
		query.append("SELECT * from ");
		res.addTable(query, true);
		// force the order to cover for database differences.
		query.append(" ORDER BY ");
		res.addUniqueName(query, false, true);
		AppContext conn = getContext();
		try{
		PreparedStatement stmt = conn.getService(DatabaseService.class).getSQLContext().getConnection().prepareStatement(
				query.toString(), ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()){
			Record record = res.new Record();
			record.setContents(rs);
			dump(record);
		}
		}catch(SQLException e){
			throw new DataFault("Bad query", e);
		}
	}
	public void dumpSchema(Repository res){
		TableSpecification spec = specifications.get(res.getTable());
		SimpleXMLBuilder sb = builder.getNested();
		sb.open(TABLE_SPECIFICATION);
		sb.attr(NAME_ATTR, res.getTag());
		sb.clean("\n");
		for(FieldInfo field : res.getInfo()){
			String name = field.getName(false);
			sb.open(name);
			String ref = field.getReferencedTable();
			if(ref != null ){
				sb.attr(REFERENCE_ATTR, Repository.tagToTable(getContext(), ref));
			}else{
				if( field.isString()){
					sb.attr(TYPE_ATTR,STRING_TYPE);
				}else if( field.isBoolean()){
					sb.attr(TYPE_ATTR,BOOLEAN_TYPE);
				}else if( field.isDate()){
					sb.attr(TYPE_ATTR, DATE_TYPE);
				}else if( field.getType() == Types.INTEGER){
					sb.attr(TYPE_ATTR,INTEGER_TYPE);
				}else if( field.getType() == Types.BIGINT){
					sb.attr(TYPE_ATTR,LONG_TYPE);
				}else if( field.getType() == Types.FLOAT || field.getType() == Types.REAL){
					sb.attr(TYPE_ATTR,FLOAT_TYPE);
				}else if( field.getType() == Types.DOUBLE){
					sb.attr(TYPE_ATTR,DOUBLE_TYPE);
				}else if(field.getType() == Types.BINARY || field.getType() == Types.VARBINARY || field.getType() == Types.LONGVARBINARY){
					sb.attr(TYPE_ATTR, BLOB_TYPE);
				}else{
					throw new ConsistencyError("Unknown field type "+field.getType()+" for "+res.getTable()+"."+name);
				}
				sb.attr(NULLABLE_ATTR,Boolean.toString(field.getNullable()));
				if( field.isString()){
					sb.attr(MAX_ATTR,Integer.toString(field.getMax()));
				}
				if( spec != null ){
					// If we have a cached specification look for a default value from that
					// Convert to the correct representation for the actual database.
					FieldType type = spec.getField(name);
					if( type != null ){
						Object def = type.getDefault();
						if( def != null ){
							sb.attr(DEFAULT_ATTR, res.convert(name, def).toString());
						}
					}
				}
			}
			sb.close();
			sb.clean("\n");
		}
		for(String index : res.getIndexNames()){
			IndexInfo info = res.getIndexInfo(index);
			sb.open(index);
			sb.attr(TYPE_ATTR,"Index");
			sb.attr(UNIQUE_ATTR,Boolean.toString(info.getUnique()));
			for(Iterator<String> it = info.getCols(); it.hasNext(); ){
				String col = it.next();
				if( col != null && col.trim().length() > 0){
					sb.open(COLUMN);
					sb.attr(NAME_ATTR,col);
					sb.close();
				}
			}
			sb.close();
			sb.clean("\n");
			
		}
		sb.close();
		sb.clean("\n");
		sb.appendParent();
	}
	/**
	 * @return the dump_null_values
	 */
	public boolean dumpNullValues() {
		return dump_null_values;
	}
	public void setDumpNullValues(boolean dump) {
		dump_null_values=dump;
	}
	/**
	 * @return the verbose_diff
	 */
	public boolean verboseDiff() {
		return verbose_diff;
	}
	/**
	 * @param verbose_diff the verbose_diff to set
	 */
	public void setVerboseDiff(boolean verbose_diff) {
		this.verbose_diff = verbose_diff;
	}

}