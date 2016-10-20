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
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumeratingTypeConverter;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeFilterProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
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
        private final Class<? super I> owner;
		private SQLTypeFilter(Class<? super I> owner,BasicType<T> type, Repository res, T v) {
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
		public StringBuilder addPattern(StringBuilder sb, boolean qualify) {
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
		public <X> X acceptVisitor(FilterVisitor<X, ? extends I> vis) throws Exception {
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
		public Class<? super I> getTarget() {
			return owner;
		}
		
		
    }

	public static class AcceptTypeFilter<T extends BasicType.Value, I extends DataObject>  extends AbstractAcceptFilter<I>{
		
        private final T target;
        private final BasicType<T> type;
		public AcceptTypeFilter(Class<? super I> owner,BasicType<T> type,T target){
		   super(owner);
	       this.target = target;
	       this.type=type;
	    }
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.hpcx.model.data.BasicDataObject.Filter#accept(uk.ac.hpcx.model.data.BasicDataObject)
		 */
		public boolean accept(I d) {
			return target == d.record.getProperty(type);
		}

	}
	public static class TypeSetSQLFilter<T extends BasicType.Value,I> implements PatternFilter<I>, SQLFilter<I>{
		private final Class<? super I> owner; 
		private final Set<T> set;
        private final Repository res;
        private final BasicType<T> type;
        public TypeSetSQLFilter(Class<? super I> owner, BasicType<T> type,Repository res, Set<T> set){
        	this.owner=owner;
        	this.set=set;
        	this.res=res;
        	this.type=type;
        }
		public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
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
		public <X> X acceptVisitor(FilterVisitor<X, ? extends I> vis) throws Exception {
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
		public Class<? super I> getTarget() {
			return owner;
		}
		
		
		
	}
	public static class TypeSetAcceptFilter<T extends BasicType.Value, I extends DataObject> extends AbstractAcceptFilter<I>{
		
		private final Set<T> set;
	    private final BasicType<T> type;
        public TypeSetAcceptFilter(Class<? super I> owner,     BasicType<T> type, Set<T> set){
        	super(owner);
        	this.set=set;
        	this.type=type;
        }
		public boolean accept(I d) {
			return set.contains( d.record.getProperty(type));
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

		/**
		 * Construct a Value and register it with the parent BasicType
		 * 
		 * @param tag
		 *            identifying String in database
		 * @param name
		 *            String to use when printing value
		 */
		@SuppressWarnings("unchecked")
		protected Value(BasicType parent,  String tag, String name) {
			this.tag = tag;
			this.Name = name;
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
	/** register a value in the BasicType. This should be called in the constructor of the value.
	 * 
	 * @param value
	 */
	private void register(T value) {
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


	public final void lock(){
		locked=true;
	}
	/**
	 * retrieve a Value from the registry.
	 * 
	 * @param o
	 * @return Value or null if invalid
	 */
	public T find(String o) {
		return  values.get(o);
	}

	public FieldType<String> getFieldType(T def){
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
	public String getField() {
		return field;
	}
	

	public <I extends DataObject> BaseFilter<I> getFilter(DataObjectFactory<I> fac, T val) {
		// default filter is SQL
		return getSQLFilter(fac, val);
	}
	public <I extends DataObject> BaseFilter<I> getFilter(DataObjectFactory<I> fac, T ... val) {
		LinkedHashSet<T> values = getValues(val);
		// default filter is SQL
		return getSQLFilter(fac, values);
	}

	public LinkedHashSet<T> getValues(T... val) {
		LinkedHashSet<T> values = new LinkedHashSet<T>();
		for(T i : val){
			values.add(i);
		}
		return values;
	}
	public <I extends DataObject> SQLFilter<I> getSQLFilter(DataObjectFactory<I> fac, T ... val) {
		LinkedHashSet<T> values = getValues(val);
		return getSQLFilter(fac, values);
	}
	public <I extends DataObject> SQLFilter<I> getSQLFilter(DataObjectFactory<I> fac, T val) {
		if( val == null ){
			return null;
		}
		return new SQLTypeFilter<T,I>(fac.getTarget(),this,fac.res, val);
	}
	public <I extends DataObject> AcceptFilter<I> getAcceptFilter(DataObjectFactory<I> fac, T val) {
		return new AcceptTypeFilter<T,I>(fac.getTarget(),this,val);
	}

	public <I extends DataObject> BaseFilter<I> getFilter(DataObjectFactory<I> fac,Set<T> val) {
		return getSQLFilter(fac,val);
	}
	public <I extends DataObject> SQLFilter<I> getSQLFilter(DataObjectFactory<I> fac,Set<T> val) {
		if( fac == null){
		    return new TypeSetSQLFilter<T,I>(fac.getTarget(),this,null, val);
		}else{
			return new TypeSetSQLFilter<T,I>(fac.getTarget(),this,fac.res, val);
		}
	}
	

	public <I extends DataObject> AcceptFilter<I> getAcceptFilter(DataObjectFactory<I> fac, Set<T> val) {
		return new TypeSetAcceptFilter<T,I>(fac.getTarget(),this,val);
		
	}
	

	/** Get a filter excluding a value
	 * @param <I> Type of filter
	 * @param fac Factory we are selecting for
	 * @param val
	 * @return BaseFilter
	 */
	public <I extends DataObject> BaseFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T val){
		Set<T> s = getValues(new HashSet<T>());
		s.remove(val);
		return getFilter(fac,s);
	}
	/** Get a filter excluding multiple value
	 * @param <I> Type of filter
	 * @param fac Factory we are selecting for
	 * @param val
	 * @return BaseFilter
	 */
	public <I extends DataObject> BaseFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T ... val){
		Set<T> s = getValues(new HashSet<T>());
		for( T i : val ){
			s.remove(i);
		}
		return getFilter(fac,s);
	}
	/** Get a filter excluding a value
	 * @param <I> type of filter
	 * @param fac Factory we are selecting for
	 * 
	 * @param val
	 * @return SQLFilter
	 */
	public <I extends DataObject> SQLFilter<I> getSQLExcludeFilter(DataObjectFactory<I> fac,T val){
		Set<T> s = getValues(new HashSet<T>());
		s.remove(val);
		return getSQLFilter(fac,s);
	}
	/** Get a filter excluding multiple value
	 * @param <I> type of filter
	 * @param fac Factory we are selecting for
	 * 
	 * @param val
	 * @return SQLFilter
	 */
	public <I extends DataObject> SQLFilter<I> getSQLExcludeFilter(DataObjectFactory<I> fac,T ... val){
		Set<T> s = getValues(new HashSet<T>());
		for( T i : val ){
			s.remove(i);
		}
		return getSQLFilter(fac,s);
	}
	/** Get a filter excluding a value
	 * @param <I> type of filter
	 * @param fac 
	 * 
	 * @param val
	 * @return AccceptFilter
	 */
	public <I extends DataObject> AcceptFilter<I> getAcceptExcludeFilter(DataObjectFactory<I> fac, T val){
		Set<T> s = getValues(new HashSet<T>());
		s.remove(val);
		return getAcceptFilter(fac,s);
	}
//	/** Get a filter excluding a value
//	 * 
//	 * @param val
//	 * @return BaseFilter
//	 */
//	public BaseFilter getExcludeFilter(Set<T> val){
//		return getExcludeFilter(null,val);
//	}
	public <I extends DataObject> BaseFilter<I> getExcludeFilter(DataObjectFactory<I> fac,Set<T> val){
		Set<T> s = getValues(new HashSet<T>());
		s.removeAll(val);
		return getFilter(fac,s);
	}
//	/** Get a filter excluding a set of value
//	 * 
//	 * @param val
//	 * @return SQLFilter
//	 */
//	public SQLFilter getSQLExcludeFilter(Set<T> val){
//		return getSQLExcludeFilter(null,val);
//	}
	public <I extends DataObject>SQLFilter<I> getSQLExcludeFilter(DataObjectFactory<I> fac,Set<T> val){
		Set<T> s = getValues(new HashSet<T>());
		s.removeAll(val);
		return getSQLFilter(fac,s);
	}
	/** Get a filter excluding a set of value
	 * 
	 * @param val Set
	 * @return AcceptFilter
	 */
	public <I extends DataObject> AcceptFilter<I> getAcceptExcludeFilter(DataObjectFactory<I> fac,Set<T> val){
		Set<T> s = getValues(new HashSet<T>());
		s.removeAll(val);
		return getAcceptFilter(fac,s);
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
	public Set<T> getValueSet(){
		return getValues(new LinkedHashSet<T>());
	}
	public <X extends Set<T>> X getValues(X set){
		set.addAll(values.values());
		return set;
	}

	public Class<? super T> getTarget() {
		
		return Value.class;
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
		for( T  res : values.values() ){
			if( res.getTag().equals(name) || res.getName().equals(name)){
				return res;
			}
		}
		throw new ParseException("Invalid value "+name);
	}

}