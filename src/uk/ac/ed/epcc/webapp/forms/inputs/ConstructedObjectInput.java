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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Tagged;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input to select a objects dynamically constructed from the configuration service 
 * This defaults to the set of <b>class.</b> definitions {@link AppContext#makeObject(Class, String)}
 * The target object is further constrained to be assignable to a specific type.
 * For security reasons its important that this class cannot be forced to generate a class other than
 * than those specified.
 * @author spb
 *
 * @param <T>
 */


public class ConstructedObjectInput<T> implements ListInput<String,T>{
    private final AppContext c;
    private String name;
    private String key;
    private Class<T> clazz;
    private Map<String,Class> reg;
    
    /** Construct input for all dynamically created types assignable to target
     * 
     * @param c
     * @param target
     */
	public ConstructedObjectInput(AppContext c,Class<T> target){
    	this(c,target,c.getClassMap(target));
    }
	/** Constructor for filtered values.
	 * map should be a sub-map of that returned by {@link AppContext#getClassMap(Class)}
	 * @param c
	 * @param target
	 * @param map
	 */
	public ConstructedObjectInput(AppContext c,Class<T> target,Map<String,Class> map){
    	this.c=c;
    	clazz=target;
    	reg = map;
    	
    	
    }
	public T getItembyValue(String value) {
		if( value == null ){
			return null;
		}
		return c.makeObject(clazz, value); 
	}

	public Iterator<T> getItems() {
		Set<T> set = new LinkedHashSet<T>();
		for(String tag : reg.keySet()){
			set.add(c.makeObject(clazz, tag));
		}
		return set.iterator();
	}

	public int getCount(){
		return reg.size();
	}
	public String getTagByItem(T item) {
		if( item == null){
			return null;
		}
		for(String key : reg.keySet()){
			if( item.getClass() == reg.get(key)){
				return key;
			}
		}
		return null;
	}

	public String getTagByValue(String value) {
		return value;
	}

	public String getText(T item) {
		String tag = getTagByItem(item);
		return tag;
	}

	public String convert(Object v) throws TypeError {
		if( v instanceof String ){
			return (String) v;
		}
		return null;
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(String value) {
		return value;
	}

	public String getString(String value) {
		return value;
	}

	public String getValue() {
		return name;
	}

	public void setKey(String key) {
		
		this.key=key;
	}

	public String setValue(String v) throws TypeError {
		String old=name;
		name=v;
		return old;
	}

	public void validate() throws FieldException {
		if( name != null && reg.containsKey(name)){
			return;
		}
		
			if( name == null ){
				throw new MissingFieldException();
			}
			throw new ValidateException("Invalid selection "+name);
	}

	public T getItem() {
		String value = getValue();
		if( value == null) {
			return null;
		}
		return c.makeObject(clazz, value);
	}

	public void setItem(T item) {
		name=getTagByItem(item);
		
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(T item) {
		if( clazz.isAssignableFrom(item.getClass())){
			if( item instanceof Tagged){
				return reg.containsKey(((Tagged)item).getTag());
			}
			return reg.containsValue(item.getClass());
		}
		return false;
	}

}