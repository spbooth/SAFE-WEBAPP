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

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.MysqlSQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.model.data.Repository;



public class MySqlCreateTableVisitor implements FieldTypeVisitor {
	public static final Feature FOREIGN_KEY_FEATURE=new Feature("foreign-key",false,"Generate foreign keys");
	private final MysqlSQLContext ctx;
	private final StringBuilder sb;
	private final List<Object> args;
	public MySqlCreateTableVisitor(MysqlSQLContext ctx,StringBuilder sb, List<Object> args){
		this.ctx=ctx;
		this.sb=sb;
		this.args=args;
	}
	 
	public void visitDateFieldType(DateFieldType dateFieldType) {
		// use integer field by default
		// this makes the field read as time field for the forms code
		// SQL timestamp would work but only if not null.
		
		//Repository can use DATE fields as well but an int fields covers all bases.
		
		//sb.append("TIMESTAMP");
		sb.append("BIGINT(20)");
		doNull(dateFieldType);
		Date d = dateFieldType.getDefault();
		if( d != null ){
			sb.append(" DEFAULT ?");
			args.add(dateFieldType.getDefault().getTime()/1000);
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
	}
	public void visitIndex(Index idx) {
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
		for(Iterator<String> it = idx.getindexNames();it.hasNext();){
			String name=it.next();
			if(seen){
				sb.append(",");
			}
			seen=true;
			ctx.quote(sb,name);
		}
		sb.append(")");
		
	}
	public void visitAutoIncrement() {
		sb.append(" INT(11) NOT NULL auto_increment");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitForeignKey(uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType)
	 */
	public void visitForeignKey(String name,ReferenceFieldType referenceField) {
		if( FOREIGN_KEY_FEATURE.isEnabled(ctx.getContext())){
			String tag = referenceField.getRemoteTable();
			// Note this will only work if the table has already been created.
			String desc = Repository.getForeignKeyDescriptor(ctx.getContext(),tag,true);
			if( desc != null ){
				sb.append(",\n");
				sb.append("FOREIGN KEY ");
				ctx.quote(sb, name+"_ref_key");
				sb.append(" ( ");
				ctx.quote(sb, name);
				sb.append(" ) REFERENCES ");
				sb.append(desc);
				sb.append(" ON UPDATE CASCADE");
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitFullTextIndex(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex)
	 */
	public void visitFullTextIndex(FullTextIndex idx) {
		boolean seen=false;
		sb.append("FULLTEXT INDEX ");
		String iname=idx.getName();
		if( iname != null ){
			ctx.quote(sb,iname);
			sb.append(" ");
		}
		sb.append("(");
		for(Iterator<String> it = idx.getindexNames();it.hasNext();){
			String name=it.next();
			if(seen){
				sb.append(",");
			}
			seen=true;
			ctx.quote(sb,name);
		}
		sb.append(")");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#useIndex(uk.ac.ed.epcc.webapp.jdbc.table.IndexType)
	 */
	public boolean useIndex(IndexType i) {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitPlaceHolderFieldType(uk.ac.ed.epcc.webapp.jdbc.table.PlaceHolderFieldType)
	 */
	@Override
	public void visitPlaceHolderFieldType(PlaceHolderFieldType p) {
		
		
	}
	
}