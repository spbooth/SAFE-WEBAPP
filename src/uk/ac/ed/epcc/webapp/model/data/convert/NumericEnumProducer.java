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

import uk.ac.ed.epcc.webapp.forms.inputs.EnumIntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.table.FieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;

/** TypeProducer that stores Enum values using their numeric ordinal value.
 * 
 * @author spb
 *
 * @param <E>
 */


public class NumericEnumProducer<E extends Enum<E>> implements TypeProducer<E,Integer> {
	  private String field;
	    private Class<E> clazz;
	    public NumericEnumProducer(Class<E> clazz,String field){
	    	this.field=field;
	    	this.clazz=clazz;
	    }
	public E find(Integer n) {
		int i=n.intValue();
		for(E e: EnumSet.allOf(clazz)){
			if( e.ordinal() == i ){
				return e;
			}
		}
		return null;
	}

	public String getField() {
		return field;
	}

	public Integer getIndex(E value) {
		return new Integer(value.ordinal());
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
	 * @see uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer#getFieldType(java.lang.Object)
	 */
	public FieldType<Integer> getFieldType(E def) {
		return new IntegerFieldType(def == null, def == null ? null : def.ordinal());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
	 */
	public Input<Integer> getInput() {
		return new EnumIntegerInput<E>(clazz);
	}
}