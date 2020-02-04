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
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DoubleFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.FloatFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.LongFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.IdMode;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;


/** A {@link ContentHandler} that parses an XML data specification created by the {@link Dumper} class
 *
 * The default mode is to try and reproduce the ids present in the dump. Optionally a {@link DumpParser} can generate a new id and attempt
 * to rewrite references. Though this is only possible for entries:
 * <ul>
 * <li> all handled by the same parser</li>
 * <li> where the fields are known to be references</li>
 * <li> where each record only occurs once </li>
 * </ul>
 * 
 * @see Dumper
 * @author spb
 *
 */
public abstract class DumpParser extends AbstractContexed implements  ContentHandler{
	private static enum State{
		Top,
		Skip,
		Record,
		Schema,
		SchemaIndex
	};
	private State state;
	private int depth;
	private Repository.Record current=null;
	private Repository res=null;
	private FieldInfo field=null;
	private boolean null_value=false;
	private Integer id=null;
	private StringBuilder sb = new StringBuilder();
	private String table_name;
	private TableSpecification spec;
	private IndexType index;
	private boolean preserve_ids=true;
	private final Map<String,Map<Integer,Integer>> id_map;
	//DataBaseHandlerService serv;
	public DumpParser(AppContext conn){
		this(conn,false);
	}
	public DumpParser(AppContext conn, boolean map_ids){
		super(conn);
		if( map_ids){
			id_map=new HashMap<>();
		}else{
			id_map=null;
		}
	}
	
	@Override
	public void characters(char[] dat, int start, int length) throws SAXException {
		for(int i=0;i<length;i++){
			sb.append(dat[i+start]);
		}
	}
	@Override
	public void endDocument() throws SAXException {
		
	}
	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		if( state == State.Record){
			if( field != null){
				// this is a field
				String text = sb.toString();
				sb.setLength(0);
				Object value=null;
				String tab = field.getReferencedTable();
				if( tab != null){
					if( field.getNullable() && null_value ) {
						current.setProperty(field.getTag(), null);
					}else {
						// Have to explicitly apply mappings
						int doc_id = Integer.parseInt(text);
						if( id_map != null ){
							Map<Integer, Integer> map = id_map.get(tab);
							if( map != null ){
								value = map.get(doc_id);
							}
						}
						if( value == null ){
							// have to assume the dump is using the same id-space as currently loaded.
							value=doc_id;
						}
						current.setProperty(field.getTag(), value);
					}
				}else{
					if( field.getNullable() && null_value ) {
						current.setProperty(field.getTag(), null);
					}else {
						try {
							field.unDump(current, text);
						} catch (Exception e) {
							throw new SAXException("undump parse error", e);
						}
					}
				}
				field=null;
			}else{
				try{
					if( current != null ){
						// end of record
						Record rec = current;

						int new_id = processRecord(id,rec);
						if( new_id != 0 && id_map != null){
							Map<Integer,Integer> map = id_map.get(res.getTag());
							if( map == null ){
								map = new HashMap<>();
								id_map.put(res.getTag(), map);
							}
							map.put(id, new_id);
						}
					}
				}catch(Exception t){
					conn.error(t,"Error commiting record");
				}finally{
					res=null;
					current=null;
					field=null;
				}
			}
		}else if (state == State.Schema){
			if( depth == 1){
				try{
					processSpecification(table_name, spec);
				}catch(Exception t){
					conn.error(t,"Error creating table");
				}finally{
					table_name=null;
					spec=null;
				}
			}
		}else if (state == State.SchemaIndex){
			if( depth == 2 ){
				state=state.Schema;
			}
		}
		if( depth > 0 ){
			depth--;
		}
		if( depth == 0 ){
			state=State.Top;
		}
	}
	/**	 handle a {@link Record} once we have parsed it
	 * @param parse_id int id parsed from file.
	 * @param rec uncommitted {@link Record} parsed from the file
	 * @return int id to record in id_map.
	 * @throws ConsistencyError
	 * @throws DataException 
	 * @throws IOException 
	 */
	public abstract int processRecord(int parse_id,Record rec) throws ConsistencyError, DataException, IOException;
	/** handle a {@link TableSpecification} once we have parsed it.
	 * 
	 * @param table
	 * @param spec
	 * @throws DataFault
	 */
	public abstract void processSpecification(String table,TableSpecification spec) throws DataFault;
	/** should we skip parsing the following {@link TableSpecification}.
	 * 
	 * @param table
	 * @return boolean
	 */
	public abstract boolean skipSpecification(String table);
	/** Should we skip the following Record.
	 * 
	 * @param id
	 * @return
	 */
	public boolean skipRecord(String name,String id) {
		return false;
	}
	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
	}
	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		
	}
	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		
	}
	@Override
	public void setDocumentLocator(Locator arg0) {
		
	}
	@Override
	public void skippedEntity(String arg0) throws SAXException {
		
	}
	@Override
	public void startDocument() throws SAXException {
		state=State.Top;
		depth=0;
	}
	@Override
	public void startElement(String arg0, String arg1, String name,
			Attributes arg3) throws SAXException {
		if( depth > 0){
			depth++;
		}
		if( state == State.Top){
			String id_str  = arg3.getValue(Dumper.ID);
			if( id_str != null ){
				depth=1; // start counting
				if( skipRecord(name,id_str)) {
					state=State.Skip;
				}else {
					state=State.Record;
					try{
						// new record
						id = Integer.parseInt(id_str);
						res=Repository.getInstance(conn, name);
						current = res.new Record();
						if( getPreserveIds()){
							// Edit existing record or use parsed id in insert.
							current.setID(id, getIdMode());
						}else{
							// If we already have a map for this parse_id assume this is an update for an existing record
							if( id_map != null ){
								Map<Integer,Integer> map = id_map.get(res.getTag());
								if( map != null ){
									Integer new_id = map.get(id);
									if( new_id != null ){
										// load existing values from database.
										current.setID(new_id.intValue(), getIdMode());
									}
								}
							}
						}
						field=null;
					}catch(Exception t){
						id=null;
						res=null;
						current=null;
						field=null;
						getLogger().error("Error making record",t);
					}
				}
			}else if( name.equals(Dumper.TABLE_SPECIFICATION)){
				depth=1; // start counting
				table_name=arg3.getValue(Dumper.NAME_ATTR);
				if( skipSpecification(table_name)){	
					state = State.Skip;
					table_name=null;
				}else{
					state = State.Schema;
					spec = new TableSpecification();
				}
			}
		}else if( state == State.Record && res != null ){
			// must be field element
			field=res.getInfo(name);
			if( field == null) {
				// try for a renamed field
				field = res.getInfo(res.dbFieldtoTag(name));
			}
			sb.setLength(0);
			null_value = arg3.getValue(Dumper.NULL_VALUE_ATTR) != null;
		}else if( state == State.Schema && spec != null){
			String type = arg3.getValue(Dumper.TYPE_ATTR);
			String ref = arg3.getValue(Dumper.REFERENCE_ATTR);
			String def = arg3.getValue(Dumper.DEFAULT_ATTR);
			if( ref != null ){
				spec.setField(name,new ReferenceFieldType(ref));
			}else if( type.equals(Dumper.INDEX_TYPE)){
				boolean unique = arg3.getValue(Dumper.UNIQUE_ATTR).equalsIgnoreCase("true");
				state = State.SchemaIndex;
				try {
					if(getContext().getBooleanParameter("dump."+table_name+"."+name+".fulltext", false)) {
						index = spec.new FullTextIndex(name);
					}else {
						index = spec.new Index(name, unique);
					}
				} catch (InvalidArgument e) {
					getLogger().error("Error making index", e);
				}
			}else{
				boolean nullable= Boolean.parseBoolean(arg3.getValue(Dumper.NULLABLE_ATTR));
				
				if( type.equals(Dumper.STRING_TYPE)){
					int max;
					String val = arg3.getValue(Dumper.MAX_ATTR);
					if( val.equalsIgnoreCase("long")) {
						max=8 * 1024 *1024;  // cleanup xsl maps long values to this as actual numeric value for mediumtext etc is implementation dep.
					}else {
						max= Integer.parseInt(val);
					}
					spec.setField(name, new StringFieldType(nullable, nullable? null : def != null ? def : "", max));
				}else if( type.equals(Dumper.BOOLEAN_TYPE)){
					spec.setField(name, new BooleanFieldType(nullable,def != null ? Boolean.valueOf(def) : false));
				}else if( type.equals(Dumper.DATE_TYPE)){
					spec.setField(name,new DateFieldType(nullable, null));
				}else if(type.equals(Dumper.INTEGER_TYPE)){
					Integer default_val = nullable? null : 0;
					if( def != null ) default_val = new Integer(def);
					spec.setField(name,new IntegerFieldType(nullable, default_val));
				}else if( type.equals(Dumper.LONG_TYPE)){
					Long default_val = nullable? null : 0L;
					if( def != null) default_val = new Long(def);
					spec.setField(name,new LongFieldType(nullable, default_val));
				}else if( type.equals(Dumper.FLOAT_TYPE)){
					Float default_val = nullable ? null : 0.0F;
					if( def != null ) default_val = new Float(def);
					spec.setField(name,new FloatFieldType(nullable, default_val));
				}else if( type.equals(Dumper.DOUBLE_TYPE)){
					Double default_val = nullable ? null : 0.0;
					if( def != null) default_val = new Double(def);
					spec.setField(name,new DoubleFieldType(nullable, default_val));
				}else if( type.equals(Dumper.BLOB_TYPE)){
					spec.setField(name, new BlobType());
				}else{
					getLogger().error("Bad specification type "+type);
				}
			}
		}else if( state == State.SchemaIndex && index != null && name.equals(Dumper.COLUMN)){
			String col_name = arg3.getValue(Dumper.NAME_ATTR);
			try {
				index.addField(col_name);
				
			} catch (InvalidArgument e) {
				getLogger().error("Error adding column", e);
			}
		}
		// ignore container elements of fields where repository 
		// lookup failed
	}
	/**
	 * @return
	 */
	protected IdMode getIdMode() {
		return IdMode.UseExistingIfPresent;
	}
	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		
	}
	/**
	 * @return the preserve_ids
	 */
	public boolean getPreserveIds() {
		return preserve_ids;
	}
	/** Set if we should preserve the ids in the dump or generate new additional ids.
	 * @param preserve_ids the preserve_ids to set
	 */
	public void setPreserveIds(boolean preserve_ids) {
		this.preserve_ids = preserve_ids;
	}
}