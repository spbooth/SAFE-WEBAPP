// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.convert;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.EnumInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;

/** A TypeProducer for producing Enum values from a String field
 * 
 * @author spb
 *
 * @param <E>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: EnumProducer.java,v 1.5 2014/09/15 14:30:30 spb Exp $")

public class EnumProducer<E extends Enum<E>> implements TypeProducer<E,String>,EnumeratingTypeConverter<E,String> {
    private String field;
    private Class<E> clazz;
    public EnumProducer(Class<E> clazz,String field){
    	this.field=field;
    	this.clazz=clazz;
    }
	public E find(String o) {
		if( o == null){
			return null;
		}
		try{
		  return Enum.valueOf(clazz, o);
		}catch(Exception e){
			return null;
		}
	}

	public String getField() {
		return field;
	}
	public FieldType<String> getFieldType(E def){
		int len=1;
		for( E e : EnumSet.allOf(clazz)){
			String tag = e.name();
			if( tag.length() > len){
				len = tag.length();
			}
		}
		return new StringFieldType(def==null,def==null ? null : def.name(), len);
	}

	public String getIndex(E value) {
		return value.name();
	}
	public Iterator<E> getValues() {
		return EnumSet.allOf(clazz).iterator();
	}
	public <X extends Set<E>> X getValues(X set) {
		set.addAll(EnumSet.allOf(clazz));
		return set;
	}
	public Class<E> getTarget() {
		return clazz;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
	 */
	public Input<String> getInput() {
		return new EnumInput<E>(clazz);
	}
	public String toString(){
		return "EnumProducer:"+clazz.getSimpleName();
	}

}