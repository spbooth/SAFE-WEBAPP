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


public class EnumProducer<E extends Enum<E>> implements TypeProducer<E,String>,EnumeratingTypeConverter<E,String> {
    private String field;
    private Class<E> clazz;
    public EnumProducer(Class<E> clazz,String field){
    	this.field=field;
    	this.clazz=clazz;
    }
	@Override
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

	@Override
	public String getField() {
		return field;
	}
	@Override
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

	@Override
	public String getIndex(E value) {
		return value.name();
	}
	@Override
	public Iterator<E> getValues() {
		return EnumSet.allOf(clazz).iterator();
	}
	@Override
	public <X extends Set<E>> X getValues(X set) {
		set.addAll(EnumSet.allOf(clazz));
		return set;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
	 */
	@Override
	public Input<String> getInput() {
		return new EnumInput<>(clazz);
	}
	@Override
	public String toString(){
		return "EnumProducer:"+clazz.getSimpleName();
	}

}