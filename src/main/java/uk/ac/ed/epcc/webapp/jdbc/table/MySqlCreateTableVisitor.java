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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.MysqlSQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.*;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;



public class MySqlCreateTableVisitor implements FieldTypeVisitor {
	public static final Feature FOREIGN_KEY_FEATURE=new Feature("foreign-key",false,"Generate foreign keys");
	public static final Feature FOREIGN_CREATE_TABLE_KEY_FEATURE=new Feature("foreign-key.create_table",true,"Automatically create parent table of foreign key if missing");

	public static final Feature FOREIGN_KEY_DELETE_CASCASE_FEATURE = new Feature("foreign-key.delete_cascase",true,"Default to DELETE CASCASE on foreign keys for references that do not allow null");
    public static final Feature FORCE_MYISAM_ON_FULLTEXT_FEATURE=new Feature("mysql.force_myisam_on_fulltext",false,"Always use MyISAM if table contains fulltext index");
    public static final Feature USE_TIMESTAMP=new Feature("mysql.use_timestamp",false,"use timestamp fields by default");
    public static final Feature USE_DATE=new Feature("mysql.use_date",true,"use date fields by default");

    private final MysqlSQLContext ctx;
	private final StringBuilder sb;
	// Keep table in memory if we can only use for unit tests
	private boolean use_memory=false;
	private boolean use_myisam=false; // older versions of mysql need this for fulltext
	private final List<Object> args;
	boolean use_date;
	boolean use_timestamp;
	private final String config_tag;
	public MySqlCreateTableVisitor(MysqlSQLContext ctx,String config_tag,StringBuilder sb, List<Object> args){
		this.ctx=ctx;
		this.sb=sb;
		this.args=args;
		AppContext conn = ctx.getContext();
		// Note don't have these start create_table.table-name as this will
		// conflict with dynamic table fields
		this.use_memory = conn.getBooleanParameter("create_table.use_memory", use_memory);
		use_date =  conn.getBooleanParameter("create_table.use_date."+config_tag, USE_DATE.isEnabled(ctx.getContext()));
		use_timestamp = conn.getBooleanParameter("create_table.use_timestamp."+config_tag, USE_TIMESTAMP.isEnabled(ctx.getContext()));
		this.config_tag=config_tag;
	}
	 
	public void visitDateFieldType(DateFieldType dateFieldType) {
		// use integer field by default
		// this makes the field read as time field for the forms code
		// SQL timestamp would work but only if not null.
		
		//Repository can use DATE fields as well but an int fields covers all bases.
		
		//sb.append("TIMESTAMP");
		if( dateFieldType.isTruncate()) {
		
			if( use_date) {
				sb.append("DATE");
				doNull(dateFieldType);
				Date d = dateFieldType.getDefault();
				if( d != null ){
					sb.append(" DEFAULT ?");
					args.add(new java.sql.Date(dateFieldType.getDefault().getTime()));
				}else {
					if( dateFieldType.canBeNull()) {
						// mysql null-date
						sb.append(" DEFAULT 0");
					}
				}
			}else {
				doNumericDate(dateFieldType);
			}
		}else {
			
			if( use_timestamp) {
				sb.append("TIMESTAMP");
				doNull(dateFieldType);
				Date d = dateFieldType.getDefault();
				if( d != null ){
					sb.append(" DEFAULT ?");
					args.add(d);
				}else {
					if( dateFieldType.canBeNull()) {
						// mysql null-date
						sb.append(" DEFAULT 0");
					}
				}
			}else {
				doNumericDate(dateFieldType);
			}
		}
		
	}

	private void doNumericDate(DateFieldType dateFieldType) {
		sb.append("BIGINT(20)");
		doNull(dateFieldType);
		Date d = dateFieldType.getDefault();
		if( d != null ){
			sb.append(" DEFAULT ?");
			args.add(dateFieldType.getDefault().getTime()/Repository.DEFAULT_RESOLUTION);
		}
	}
	
	private void doNull(FieldType field) {
		if( !  field.canBeNull() ){
			sb.append(" NOT NULL");
		}
	}
	public <N extends Number> void visitNumberFieldType(NumberFieldType<N> numberFieldType) {
		Class t = numberFieldType.geTarget();
		if( t == Double.class){
			sb.append("DOUBLE");
		}else if( t == Float.class){
			sb.append("FLOAT");
		}else if (t == Integer.class){
			sb.append("INT(11)");
		}else{
			sb.append("BIGINT(20)");
		}
		doNull(numberFieldType);
		Number def = numberFieldType.getDefault();
		if( def == null && ! numberFieldType.canBeNull()){
			return;
		}
		if( def != null ){
			sb.append(" DEFAULT ?");
			args.add(def);
		}else{
			sb.append(" DEFAULT NULL");
		}
	}
	

	public void visitStringFieldType(StringFieldType stringFieldType) {
		int len = stringFieldType.getMaxLEngth();
		if( len < 256 ){
			if( len < ctx.getContext().getIntegerParameter("table.min_varchar", 8) ){
				sb.append("CHAR(");
			}else{
				sb.append("VARCHAR(");
			}
			sb.append(len);
			sb.append(")");
			doNull(stringFieldType);
			String def = stringFieldType.getDefault();
			if( def == null && ! stringFieldType.canBeNull()){
				def = "";
			}
			if( def != null ){
				sb.append(" DEFAULT ?");
				args.add(def);
			}else{
				sb.append(" DEFAULT NULL");
			}
		}else{
			sb.append("mediumtext"); // cannot specify default 
			use_memory=false;
		}
		
	}
	public void visitBooleanFieldType(BooleanFieldType booleanFieldType) {
		sb.append("BOOLEAN");
		doNull(booleanFieldType);
		Boolean def = booleanFieldType.getDefault();
		if( def != null ){
			sb.append(" DEFAULT ?");
			args.add(def);
		}else{
			sb.append(" DEFAULT NULL");
		}
		
	}
	public void visitBlobType(BlobType blobType) {
		sb.append("LONGBLOB");
		use_memory=false;
	}
	public void visitIndex(UnaryOperator<String> name_map,Index idx) {
		if( idx.getUnique()){
			sb.append("UNIQUE ");
		}
		boolean seen=false;
		sb.append("KEY ");
		String iname=idx.getName();
		if( iname != null ){
			ctx.quote(sb,iname);
			sb.append(" ");
		}
		sb.append("(");
		for(Iterator<IndexField> it = idx.getindexNames();it.hasNext();){
			IndexField f = it.next();
			String name=f.name;
			if( name_map != null ) {
				name = name_map.apply(name);
			}
			if(seen){
				sb.append(",");
			}
			seen=true;
			ctx.quote(sb,name);
			if( f.length > 0 ) {
				sb.append("(");
				sb.append(Integer.toString(f.length));
				sb.append(")");
			}
		}
		sb.append(")");
		
	}
	public void visitAutoIncrement() {
		sb.append(" INT(11) NOT NULL auto_increment");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitForeignKey(uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType)
	 */
	public void visitForeignKey(String name,String prefix,ReferenceFieldType referenceField) {
		AppContext conn = ctx.getContext();
		// Allow per reg supression (e.g. for circular references)
		if(  FOREIGN_KEY_FEATURE.isEnabled(conn) && referenceField.wantForeighKey() ){
			String tag = referenceField.getRemoteTable();
			if(FOREIGN_CREATE_TABLE_KEY_FEATURE.isEnabled(conn)) {
				try{
					// Try to make referenced factory.
					// This is to ensure the referenced table is auto-created before the
					// current one (in case we have a foreign key)
					conn.makeObject(DataObjectFactory.class, tag);
				}catch(Exception t){
					Logger.getLogger(conn,getClass()).error("Problem making referenced facory for "+tag);
				}
			}
			DataBaseHandlerService hand = conn.getService(DataBaseHandlerService.class);
			if( ! hand.tableExists(Repository.tagToTable(conn, tag))) {
				return;
			}
			// Note this will only work if the table has already been created.
			String desc = Repository.getForeignKeyDescriptor(conn,tag,true);
			if( desc != null ){
				sb.append(prefix);
				sb.append("CONSTRAINT ");
				// This needs to be a globally unique name (also not conflict with table names)
				// So name after the referencing field
				ctx.quote(sb, config_tag+"_"+name+"_fk");
				sb.append(" FOREIGN KEY ");
				
				sb.append(" ( ");
				ctx.quote(sb, name);
				sb.append(" ) REFERENCES ");
				sb.append(desc);
				if(FOREIGN_KEY_DELETE_CASCASE_FEATURE.isEnabled(conn)) {
					if( ! referenceField.canBeNull()) {
						sb.append(" ON DELETE CASCADE");
					}else {
						sb.append(" ON DELETE SET NULL");
					}
				}
				sb.append(" ON UPDATE CASCADE");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitFullTextIndex(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex)
	 */
	public void visitFullTextIndex(UnaryOperator<String> name_map,FullTextIndex idx) {
		boolean seen=false;
		sb.append("FULLTEXT INDEX ");
		String iname=idx.getName();
		if( iname != null ){
			ctx.quote(sb,iname);
			sb.append(" ");
		}
		sb.append("(");
		for(Iterator<IndexField> it = idx.getindexNames();it.hasNext();){
			IndexField f = it.next();
			String name=f.name;
			if( name_map != null ) {
				name = name_map.apply(name);
			}
			if(seen){
				sb.append(",");
			}
			seen=true;
			ctx.quote(sb,name);
			if( f.length > 0 ) {
				sb.append("(");
				sb.append(Integer.toString(f.length));
				sb.append(")");
			}
		}
		sb.append(")");
		if( FORCE_MYISAM_ON_FULLTEXT_FEATURE.isEnabled(ctx.getContext())) {
			use_myisam=true;
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#useIndex(uk.ac.ed.epcc.webapp.jdbc.table.IndexType)
	 */
	public boolean useIndex(IndexType i) {
//		if( FOREIGN_KEY_FEATURE.isEnabled(ctx.getContext())) {
//			if( i.isRef()) {
//				// This will duplicate the automatic foreign key
//				return false;
//			}
//		}
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitPlaceHolderFieldType(uk.ac.ed.epcc.webapp.jdbc.table.PlaceHolderFieldType)
	 */
	@Override
	public void visitPlaceHolderFieldType(PlaceHolderFieldType p) {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#additions(boolean)
	 */
	@Override
	public void additions(boolean create) {
		if( create ){
			if( use_myisam) {
				sb.append(" ENGINE=MyISAM");
			}else if( use_memory) {
				sb.append(" ENGINE=MEMORY");
			}
		}
	}
	
}