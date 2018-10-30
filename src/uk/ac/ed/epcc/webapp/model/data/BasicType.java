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
/*
 * Created on 21-Dec-2004 by spb
 *
 */
package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumeratingTypeConverter;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeFilterProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.BasicTypeInput;

/**
 * BasicType
 * 
 * A base class for building type-safe enumeration classes for use as Status and
 * type fields for BasicDataObject sub-classes. This class should be exended as
 * a static Subclass for the for parent class and instantiated as a static
 * Singleton. BasicType is a container that holds all the possible values of the
 * enumeration using the Value inner class. These are created using the
 * makeValue method.
 * <p>
 * BasicType also holds the field name of the Database field it is representing
 * This allows us to have BasicDataObject methods that understand about the
 * BasicType class
 * <p>
 * Since the existence of true Enum types in Java 5 these can be used 
 * instead in conjunction with the {@link EnumProducer} class.
 * 
 * @author spb
 * @param <T> Type of Value class
 * 
 * 
 */

public abstract class BasicType<T extends BasicType.Value> implements TypeProducer<T,String>,EnumeratingTypeConverter<T,String>, TypeFilterProducer<T,String> {


   
	
	public static class SQLTypeFilter<T extends BasicType.Value,I> implements PatternFilter<I>, SQLFilter<I>{
    	private final T target;
		private final Repository res;
        private final BasicType<T> type;
        private final Class<I> owner;
		private SQLTypeFilter(Class<I> owner,BasicType<T> type, Repository res, T v) {
			this.owner=owner;
			this.type=type;
			this.res=res;
			target = v;
		}

		
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.hpcx.model.data.BasicDataObject.Filter#condition()
		 */
		public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb, boolean qualify) {
			if (res == null ) {
				sb.append(type.getField());
				sb.append("= ? ");
			} else {
				FieldInfo info = res.getInfo(type.getField());
				if( info == null ){
					throw new ConsistencyError("No field "+type.getField());
				}
				info.addName(sb, qualify, true);
				sb.append("= ?");
			}
			return sb;
		}


		
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			list.add(new PatternArg(res,type.getField(),target.getTag()));
			return list;
		}


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X,I> vis) throws Exception {
			return vis.visitPatternFilter(this);
		}


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
		 */
		public void accept(I o) {
			
		}


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#getType()
		 */
		public Class<I> getTarget() {
			return owner;
		}
		
		public String toString() {
			return "SQLTypeFilter("+type.getField()+"="+target.getName()+")";
		}
		
    }

	
	public static class TypeSetSQLFilter<T extends BasicType.Value,I> implements PatternFilter<I>, SQLFilter<I>{
		private final Class<I> owner; 
		private final Set<T> set;
        private final Repository res;
        private final BasicType<T> type;
        public TypeSetSQLFilter(Class<I> owner, BasicType<T> type,Repository res, Set<T> set){
        	this.owner=owner;
        	this.set=set;
        	this.res=res;
        	this.type=type;
        }
		public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify) {
			boolean seen=false;
			sb.append(" ( ");
			for(int i=0; i< set.size(); i++){
			   if(! seen ){
				   seen=true;
			   }else{
				   sb.append(" OR ");
			   }
			   if( res == null ){
				   sb.append( type.getField()); 
			   }else{
				   res.getInfo(type.getField()).addName(sb, qualify, true);
			   } 
			   sb.append("=? " );
			}
			sb.append(" ) ");
			return sb;
		}

		
	
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			for( T val : set){
				list.add(new PatternArg(res,type.getField(),val.getTag()));
			}
			return list;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, I> vis) throws Exception {
			return vis.visitPatternFilter(this);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
		 */
		public void accept(I o) {
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#getType()
		 */
		public Class<I> getTarget() {
			return owner;
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("TypeSetSQLFilter(");
			boolean seen=false;
			for(T val : set){
				   if(! seen ){
					   seen=true;
				   }else{
					   sb.append(" OR ");
				   }
				   sb.append( type.getField()); 
				   sb.append("=" );
				   sb.append(val.getName());
				}
			sb.append(")");
			return sb.toString();
		}
		
		
	}
	
	/**
	 * Value inner class representing a possible value of the enclosing
	 * <code>BasicType</code>. Each <code>BasicType</code> subclass should have its own <code>Value</code> class
	 * derived from <code>BasicType.Value</code>.
	 * <p>
	 * We use the pseudo-inner-class pattern for <code>Value</code> rather than a true inner class to
	 * avoid problems with generics. The final <code>Value</code> subclass can still be a true inner class but will need
	 * to pass its outer class pointer when calling the super constructor.
	 * 
	 * @author spb
	 * 
	 */
	public abstract static class Value {

		private String tag; // how status is stored in database

		private String Name; // printable version of Name

		
		/** Construct a Value without registration.
		 * This allows sub-classes to construct static Values in
		 * composites that are registered in the type when the composite is added to the parent type. 
		 * @param tag
		 * @param name
		 */
		protected Value(String tag,String name){
			this.tag = tag;
			this.Name = name;
		}
		/**
		 * Construct a Value and register it with the parent BasicType
		 * 
		 * Normally this is the only constructor to be called by sub-classes
		 * to ensure that all {@link Value}s are registered.
		 * 
		 * @param tag
		 *            identifying String in database
		 * @param name
		 *            String to use when printing value
		 */
		@SuppressWarnings("unchecked")
		protected Value(BasicType parent,  String tag, String name) {
			this(tag,name);
			parent.register(this);

		}
		/**
		 * get the printable version of this Value
		 * 
		 * @return String value of name
		 */
		public String getName() {
			return Name;
		}

		/**
		 * get the database value corresponding top this Value
		 * 
		 * @return String database tag
		 */
		public String getTag() {
			return tag;
		}

		@Override
		public final String toString() {
			// TypeProducerInput uses toString for the name
			return getName();
		}
		@Override
		public boolean equals(Object obj) {
			if( obj == null ){
				return false;
			}
			if( obj == this){
				return true;
			}
			if( obj.getClass() != getClass()){
				return false;
			}
			Value peer = (Value)obj;
			if( peer.tag.equals(tag)){
				return true;
			}
			return false;
		}
		@Override
		public int hashCode() {
			return tag.hashCode();
		}

	}

	private LinkedHashMap<String,T> values = null;

	private String field = null;
	
	private boolean locked=false;

	/**
	 * Create the BasicType object
	 * 
	 * @param field
	 *            String the Database field name we encode for.
	 */
	protected BasicType(String field) {
		super();
		values = new LinkedHashMap<String,T>();
		this.field = field;
	}

	/** create a new BasicType as duplicate of an existing type but
	 * unlocked. This is intended to allow a sub-class to create an
	 * extended list without polluting the BasicType in the parent class.
	 * 
	 * @param parent
	 */
	protected BasicType(BasicType<T> parent){
		this(parent.getField());
		for(T v : parent.getValueSet()){
			register(v);
		}
	}
	/** register a value in the BasicType. This should normally be called in the constructor of the value.
	 * 
	 * @param value
	 */
	protected void register(T value) {
		if( locked){
			throw new ConsistencyError("Register called after lock "+value.getTag()+" in "+getClass().getCanonicalName());
		}
		if( values.containsKey(value.getTag())){
			throw new ConsistencyError("Duplicate tag "+value.getTag()+" in "+getClass().getCanonicalName());
		}
		if( ! getTarget().isAssignableFrom(value.getClass())){
			throw new ConsistencyError("Register called on invalid class "+getTarget().getCanonicalName()+" not assignable from "+value.getClass().getCanonicalName());
		}
		// store value in parent type.
		values.put(value.getTag(), value);
	}


	/** lock the type preventing futher additions
	 * 
	 */
	public final void lock(){
		locked=true;
	}
	/** has the type been locked
	 * 
	 * @return
	 */
	public boolean isLocked(){
		return locked;
	}
	/**
	 * retrieve a Value from the registry.
	 * 
	 * @param o
	 * @return Value or null if invalid
	 */
	public final T find(String o) {
		return  values.get(o);
	}

	public final FieldType<String> getFieldType(T def){
		int len=1;
		for( String tag : values.keySet()){
			if( tag.length() > len){
				len = tag.length();
			}
		}
		return new StringFieldType(def==null,def==null ? null : def.getTag(), len);
	}
	/**
	 * get the database field name we encode for
	 * 
	 * @return String
	 */
	public final String getField() {
		return field;
	}
	

	

	public final LinkedHashSet<T> getValues(T... val) {
		LinkedHashSet<T> values = new LinkedHashSet<T>();
		for(T i : val){
			values.add(i);
		}
		return values;
	}
	
	public <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac, T val) {
		if( val == null ){
			return null;
		}
		return new SQLTypeFilter<T,I>(fac.getTarget(),this,fac.res, val);
	}
	

	
	public <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac,Set<T> val) {
		return new TypeSetSQLFilter<T,I>(fac.getTarget(),this,fac.res, val);
	}
	
	public <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac,T ... val) {
		return new TypeSetSQLFilter<T,I>(fac.getTarget(),this,fac.res,getValues(val) );
	}
	/**
	 * @param val
	 * @return
	 */
	public final Set<T> getExcludeValues(T... val) {
		Set<T> s = getValueSet();
		for( T i : val ){
			s.remove(i);
		}
		return s;
	}
	/** Get a filter excluding a value
	 * @param <I> type of filter
	 * @param fac Factory we are selecting for
	 * 
	 * @param val
	 * @return SQLFilter
	 */
	public <I extends DataObject> SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T val){
		Set<T> s = getValues(new HashSet<T>());
		s.remove(val);
		return getFilter(fac,s);
	}
	/** Get a filter excluding multiple value
	 * @param <I> type of filter
	 * @param fac Factory we are selecting for
	 * 
	 * @param val
	 * @return SQLFilter
	 */
	public <I extends DataObject> SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T ... val){
		return getFilter(fac,getExcludeValues(val));
	}
	

	public <I extends DataObject>SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,Set<T> val){
		Set<T> s = getValues(new HashSet<T>());
		s.removeAll(val);
		return getFilter(fac,s);
	}
	
	public BasicTypeInput<T> getInput() {
		BasicTypeInput<T> input = new BasicTypeInput<T>(this);
		input.setOptional(true);
		return input;
	}

    public final String getIndex(T value){
    	if( ! values.containsValue(value)){
    		return null;
    	}
    	return value.getTag();
    }
	public Iterator<T> getValues() {
		return values.values().iterator();
	}
	public final Set<T> getValueSet(){
		return getValues(new LinkedHashSet<T>());
	}
	public final <X extends Set<T>> X getValues(X set){
		set.addAll(values.values());
		return set;
	}

	public Class<T> getTarget() {
		
		return (Class<T>) Value.class;
	}
	public String toString(){
		return getClass().getSimpleName()+"."+field;
	}
	
	/** Simple parse method that matches tag or name.
	 * 
	 * @param name
	 * @return
	 * @throws ParseException 
	 */
	public T parse(String name) throws ParseException{
		// look for tag first
		T result = find(name);
		if( result != null){
			return result;
		}
		// look for name
		for( T  res : values.values() ){
			if( res.getName().equals(name)){
				return res;
			}
		}
		throw new ParseException("Invalid value "+name);
	}

}