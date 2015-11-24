// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.PostgresqlSQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.Index;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
@uk.ac.ed.epcc.webapp.Version("$Id: PostgresqlCreateTableVisitor.java,v 1.10 2015/10/26 10:07:03 spb Exp $")


public class PostgresqlCreateTableVisitor implements FieldTypeVisitor {
	private final PostgresqlSQLContext ctx;
	private final StringBuilder sb;
	private final List<Object> args;
	public PostgresqlCreateTableVisitor(PostgresqlSQLContext ctx,StringBuilder sb, List<Object> args){
		this.ctx=ctx;
		this.sb=sb;
		this.args=args;
	}
	
	public void visitDateFieldType(DateFieldType dateFieldType) {
		// use timestamp field by default
		// this makes the field read as time field for the forms code, though
		// user may have wanted date not timestamp.
		
		sb.append("TIMESTAMP");
		doNull(dateFieldType);
		Date d = dateFieldType.getDefault();
		if( d != null ){
			sb.append(" DEFAULT ?");
			args.add(d);
		}
		
	}

	private void doNull(FieldType field) {
		if( !  field.canBeNull() ){
			sb.append(" NOT NULL ");
		}
	}
	public <N extends Number> void visitNumberFieldType(NumberFieldType<N> numberFieldType) {
		Class t = numberFieldType.geTarget();
		if( t == Double.class){
			sb.append("DOUBLE");
		}else if( t == Float.class){
			sb.append("FLOAT");
		}else if (t == Integer.class){
			sb.append("INT");
		}else{
			sb.append("BIGINT");
		}
		doNull(numberFieldType);
		Number def = numberFieldType.getDefault();
		if( def == null && ! numberFieldType.canBeNull()){
			return;
		}
		if( def != null ){
			// postgresql jdbc driver does not currently allow
			// substitution in create table.
			sb.append(" DEFAULT ");
			sb.append(def);
		}else{
			sb.append(" DEFAULT NULL");
		}
	}
	public void visitStringFieldType(StringFieldType stringFieldType) {
		int len = stringFieldType.getMaxLEngth();
		if( len < 256 ){
			sb.append("VARCHAR(");
			sb.append(len);
			sb.append(")");
			doNull(stringFieldType);
			String def = stringFieldType.getDefault();
			if( def == null && ! stringFieldType.canBeNull()){
				def = "";
			}
			if( def != null ){
				if( def.length()==0){
					sb.append(" DEFAULT '' ");
				}else{
					sb.append(" DEFAULT '");
					sb.append(def.replace('\'', ' '));
					sb.append("'");
				}
			}else{
				sb.append(" DEFAULT NULL");
			}
		}else{
			sb.append("text"); // cannot specify default 
		}
		
	}
	public void visitBooleanFieldType(BooleanFieldType booleanFieldType) {
		sb.append("BOOLEAN");
		doNull(booleanFieldType);
		Boolean def = booleanFieldType.getDefault();
		if( def != null ){
			sb.append(" DEFAULT ");
			sb.append(def.toString());
		}else{
			sb.append(" DEFAULT NULL");
		}
		
	}
	public void visitBlobType(BlobType blobType) {
		sb.append("bytea");
	}
	public void visitIndex(Index idx) {
		if( ! idx.getUnique()){
			// Don't seem to have non unique keys
			return;
		}
		
		boolean seen=false;
		sb.append(",\nUNIQUE ");
		
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
		sb.append(" SERIAL ");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitForeignKey(uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType)
	 */
	public void visitForeignKey(String name, ReferenceFieldType referenceField) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitFullTextIndex(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.FullTextIndex)
	 */
	public void visitFullTextIndex(FullTextIndex i) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#useIndex(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType)
	 */
	public boolean useIndex(IndexType i) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor#visitPlaceHolderFieldType(uk.ac.ed.epcc.webapp.jdbc.table.PlaceHolderFieldType)
	 */
	@Override
	public void visitPlaceHolderFieldType(PlaceHolderFieldType p) {
		
		
	}
	
}