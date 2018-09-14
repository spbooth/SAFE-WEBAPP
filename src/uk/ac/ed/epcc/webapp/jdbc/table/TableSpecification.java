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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** Table Description.  
 * We use an abstract representation to make it easier to port between database back-ends.
 * 
 * We can also store optional fields. These are not created by default but might
 * be presented as options when editing the table structure.
 * @author spb
 *
 */


public class TableSpecification {
	private String primary_key;
	private final LinkedHashMap<String,FieldType> all_fields;
	private final Set<String> required_field_names;
	private LinkedHashSet<IndexType> indexes;
	private final Pattern field_pattern = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
	public TableSpecification(String key){
		this.primary_key=key;
		all_fields=new LinkedHashMap<String, FieldType>();
		required_field_names=new HashSet<String>();
		
		indexes=new LinkedHashSet<IndexType>();
	}
	public TableSpecification(){
		this("PrimaryRecordID");
	}
	public TableSpecification(TableSpecification spec){
		this.primary_key=spec.primary_key;
		all_fields=new LinkedHashMap<String, FieldType>();
		required_field_names=new HashSet<String>();
		all_fields.putAll(spec.all_fields);
		required_field_names.addAll(spec.required_field_names);
		indexes=new LinkedHashSet<IndexType>();
		for(IndexType i: spec.indexes){
			i.copy(this);
		}
	}
	public void setPrimaryKey(String name){
		if( all_fields.containsKey(name)){
			throw new ConsistencyError("Primary key changed to existing field");
		}
		primary_key=name;
		
	}
	public String getPrimaryKey(){
		return primary_key;
	}
	public void setField(String name, FieldType type){
		setField(name, type,false);
	}
	/** Does the specification contain this name
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean hasField(String name){
		return required_field_names.contains(name);
	}
	public void setField(String name, FieldType type,boolean optional){
		if( primary_key.equalsIgnoreCase(name)){
			throw new ConsistencyError("Field name matches primary key name");
		}
		if( ! field_pattern.matcher(name).matches() ){
			throw new ConsistencyError("Bad field name "+name);
		}
		all_fields.put(name, type);
		if(! optional){
			required_field_names.add(name);
		}
	}
	public void setOptionalField(String name, FieldType type){
		setField(name, type, true);
	}
	
	
	public boolean goodFieldName(String s){
		if( primary_key.equalsIgnoreCase(s)){
			return false;
		}
		return field_pattern.matcher(s).matches();
	}
	/** add specifications from a map of parameter values
	 * 
	 * @param prefix  key values start with this
	 * @param params
	 */
	public void setFromParameters(AppContext conn,String prefix,Map<String,String> params){
		for(String key : params.keySet()){
			String name=key.substring(prefix.length());
			String type=params.get(key);
			try {
				if(type.equalsIgnoreCase("double")){
					setField(name, new DoubleFieldType(true, null));
				}else if(type.equalsIgnoreCase("float")){
					setField(name, new FloatFieldType(true, null));
				}else if(type.equalsIgnoreCase("integer")){
					setField(name, new IntegerFieldType(true, null));
				}else if(type.equalsIgnoreCase("long")){
					setField(name, new LongFieldType(true, null));
				}else if(type.equalsIgnoreCase("date")){
					setField(name, new DateFieldType(true, null));
				}else if(type.equalsIgnoreCase("boolean")){
					setField(name, new BooleanFieldType(true, Boolean.FALSE));
				}else if(type.equalsIgnoreCase("required")){
					promoteOptionalField(name);
				}else if( type.startsWith("string")){
					int length = Integer.parseInt(type.substring("string".length()));
					setField(name, new StringFieldType(true, null, length));
				}else{
					setField(name, new ReferenceFieldType(type));
				}
			}catch(Exception t) {
				conn.getService(LoggerService.class).getLogger(getClass()).error("Error parsing table specification parameter "+name+"="+type,t);
			}
		}
	}
	public FieldType getField(String name){
		return all_fields.get(name);
	}
	public Set<String> getFieldNames(){
		// ordered set in the canonical order
		// we want promoted fields to still retain their original order
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for(String name : all_fields.keySet()){
			if( required_field_names.contains(name)){
				result.add(name);
			}
		}
		return result;
	}
	public Set<String> getOptionalFieldNames(){
		// ordered set in the canonical order
		// we want promoted fields to still retain their original order
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for(String name : all_fields.keySet()){
			if( ! required_field_names.contains(name)){
				result.add(name);
			}
		}
		return result;
	}
	public Iterator<IndexType> getIndexes(){
		return indexes.iterator();
	}
	/** promote an optional field to required
	 * 
	 * @param name
	 */
	public void promoteOptionalField(String name){
		if( all_fields.containsKey(name)){
			required_field_names.add(name);
		}
	}
	/** get a map of all fields in the specification.
	 * 
	 * @return
	 */
	public Map<String,FieldType> getStdFields(){
		return (Map<String, FieldType>) all_fields.clone();
	}
	
	public abstract class IndexType {
		private final String name;
		private LinkedHashSet<String> index_fields = new LinkedHashSet<String>();
		public IndexType(String name, String ...strings) throws InvalidArgument{
			this.name=name;
			for(String s : strings){
				addField(s);
			}
			indexes.add(this);
		}
		public void addField(String s) throws InvalidArgument {
			if( getFieldNames().contains(s)){
				index_fields.add(s);
			}else{
				throw new InvalidArgument("Table does not contain field "+s);
			}
		}
		public IndexType(IndexType i) {
			this.name=i.name;
			Set<String> names = getFieldNames();
			for(String s : i.index_fields){
				if( names.contains(s)){
					index_fields.add(s);
				}
			}
			indexes.add(this);
		}
		public String getName(){
			return name;
		}
		/** is this and index containing a single reference field
		 *  
		 * @return
		 */
		public boolean isRef() {
			if(index_fields.size() == 1 ) {
				for(String s : index_fields) {
					if( getField(s) instanceof ReferenceFieldType) {
						return true;
					}
				}
			}
			return false;
		}
		public Iterator<String> getindexNames(){
			return index_fields.iterator();
		}
		public abstract void accept(FieldTypeVisitor vis);
		/** Generate a copy of this index in a different specification.
		 * 
		 * @param spec
		 * @return
		 */
		public abstract IndexType copy(TableSpecification spec);
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((index_fields == null) ? 0 : index_fields.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			IndexType other = (IndexType) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (index_fields == null) {
				if (other.index_fields != null)
					return false;
			} else if (!index_fields.equals(other.index_fields))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		private TableSpecification getOuterType() {
			return TableSpecification.this;
		}
	}
	public class FullTextIndex extends IndexType{

		public FullTextIndex(FullTextIndex i) {
			super(i);
		}

		public FullTextIndex(String name, String... strings)
				throws InvalidArgument {
			super(name, strings);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType#accept(uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor)
		 */
		@Override
		public void accept(FieldTypeVisitor vis) {
			vis.visitFullTextIndex(this);
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType#copy()
		 */
		@Override
		public IndexType copy(TableSpecification spec) {
			return spec.new FullTextIndex(this);
		}

	
		
	}
	public class Index extends IndexType {
		private final boolean unique;
		public Index(String name,boolean unique, String ...strings) throws InvalidArgument{
			super(name,strings);
			this.unique=unique;
		}
		public Index(Index i) {
			super(i);
			this.unique=i.unique;
		}
		
		public boolean getUnique(){
			return unique;
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType#accept(uk.ac.ed.epcc.webapp.jdbc.table.FieldTypeVisitor)
		 */
		public void accept(FieldTypeVisitor vis) {
			vis.visitIndex(this);
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType#copy(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification)
		 */
		@Override
		public IndexType copy(TableSpecification spec) {
			return spec.new Index(this);
		}
	}
	
}