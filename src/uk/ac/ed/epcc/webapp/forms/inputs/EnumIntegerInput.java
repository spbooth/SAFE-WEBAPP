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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input to select values from a Java Enum 
 * the DB will contain the Enum ordinal and the menu the result of the toString call.
 * 
 * @author spb
 *
 * @param <E> Type of Enum to use
 */


public class EnumIntegerInput<E extends Enum<E>> extends IntegerInput implements  ListInput<Integer,E> {
    
	EnumSet<E> set;
    Map<Integer,E> lookup;
    public EnumIntegerInput(EnumSet<E> set){
    	this.set = set;
    	lookup = new HashMap<Integer,E>();
    	for(E s: set){
    		lookup.put(s.ordinal(), s);
    	}
    }
    public EnumIntegerInput(Class<E> clazz){
    	this(EnumSet.allOf(clazz));
    }
	public E getItembyValue(Integer value) {
		if( value == null ){
			return null;
		}
		return lookup.get(value);
	}

	public Iterator<E> getItems() {
		return set.iterator();
	}

	public int getCount(){
		return set.size();
	}
	public String getTagByItem(E item) {
		if( item == null){
			return null;
		}
		return Integer.toString(item.ordinal());
	}

	public String getTagByValue(Integer value) {
		return getTagByItem(getItembyValue(value));
	}

	public String getText(E item) {
		if( item == null ){
			return null;
		}
		return item.toString();
	}

	public E getItem() {
		Integer val = getValue();
		if( val == null ){
			return null;
		}
		return getItembyValue(val);
	}

	
	public void setItem(E v) {
		if( v == null ){
			setValue(null);
			return;
		}
		setValue(v.ordinal());
	}
	
	@Override
	public String getPrettyString(Integer val) {
		if( val == null ){
			return "No Value";
		}
		return getText(getItembyValue(val));
	}
	@SuppressWarnings("unchecked")
	@Override
	public Integer convert(Object v) throws TypeError {
		if( v == null ){
			return null;
		}
		if( set.contains(v)){
			return ((Enum)v).ordinal();
		}
		return super.convert(v);
	}
	
	@Override
	public void validate() throws FieldException {
		super.validate();
		Integer val = getValue();
		if( isOptional() && val == null ){
			return;
		}
		if( ! lookup.containsKey(val)){
			throw new ValidateException("Not one of the valid choices");
		}
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(E item) {
		return set.contains(item);
	}

}