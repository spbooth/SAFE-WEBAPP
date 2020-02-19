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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumeratingTypeConverter;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeFilterProducer;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
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
		values = new LinkedHashMap<>();
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
	@Override
	public final T find(String o) {
		return  values.get(o);
	}

	@Override
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
	@Override
	public final String getField() {
		return field;
	}
	

	

	public final LinkedHashSet<T> getValues(T... val) {
		LinkedHashSet<T> values = new LinkedHashSet<>();
		for(T i : val){
			values.add(i);
		}
		return values;
	}
	
	@Override
	public final <I extends DataObject> SQLFilter<I> getFilter(DataObjectFactory<I> fac, T val) {
		if( val == null ){
			return null;
		}
		return new SQLValueFilter<I>(fac.getTarget(), fac.res, getField(), val.getTag());
	}
	
	/** Get a filter excluding a value
	 * @param <I> type of filter
	 * @param fac Factory we are selecting for
	 * 
	 * @param val
	 * @return SQLFilter
	 */
	@Override
	public final <I extends DataObject> SQLFilter<I> getExcludeFilter(DataObjectFactory<I> fac,T val){
		return new SQLValueFilter<I>(fac.getTarget(), fac.res, getField(), val.getTag(),true);
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
	
	
	
	@Override
	public BasicTypeInput<T> getInput() {
		BasicTypeInput<T> input = new BasicTypeInput<>(this);
		return input;
	}
	public final Selector<BasicTypeInput<T>> getSelector(){
		return new Selector<BasicTypeInput<T>>() {

			@Override
			public BasicTypeInput<T> getInput() {
				
				return BasicType.this.getInput();
			}
			
		};
	}
	public final Selector<BasicTypeInput<T>> getSelector(boolean pre_select){
		return new Selector<BasicTypeInput<T>>() {

			@Override
			public BasicTypeInput<T> getInput() {
				
				BasicTypeInput<T> input = BasicType.this.getInput();
				input.setPreSelect(pre_select);
				return input;
			}
			
		};
	}
	

    @Override
	public final String getIndex(T value){
    	if( ! values.containsValue(value)){
    		return null;
    	}
    	return value.getTag();
    }
	@Override
	public Iterator<T> getValues() {
		return values.values().iterator();
	}
	public final Set<T> getValueSet(){
		return getValues(new LinkedHashSet<T>());
	}
	@Override
	public final <X extends Set<T>> X getValues(X set){
		set.addAll(values.values());
		return set;
	}

	@Override
	public Class<T> getTarget() {
		
		return (Class<T>) Value.class;
	}
	@Override
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