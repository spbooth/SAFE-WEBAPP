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
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** Input to select a objects dynamically constructed from the configuration service 
 * This defaults to the set of <b>class.</b> definitions {@link AppContext#makeObject(Class, String)}
 * The target object is further constrained to be assignable to a specific type.
 * For security reasons its important that this class cannot be forced to generate a class other than
 * than those specified.
 * @author spb
 *
 * @param <T> type of object constructed
 */


public class ConstructedObjectInput<T> extends SimpleListInput<T>{
    private final AppContext c;
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
    	addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String data) throws FieldException {
				if( ! map.containsKey(data)) {
					throw new ValidateException("Tag "+data+" does not resolve to target class");
				}
				
			}
		});
    	
    }
	@Override
	public T getItemByTag(String value) {
		if( value == null  || value.isEmpty()){
			return null;
		}
		return c.makeObject(clazz, value); 
	}

	@Override
	public Iterator<T> getItems() {
		Set<T> set = new LinkedHashSet<>();
		for(String tag : reg.keySet()){
			set.add(c.makeObject(clazz, tag));
		}
		return set.iterator();
	}

	@Override
	public int getCount(){
		return reg.size();
	}
	@Override
	public String getTagByItem(T item) {
		if( item == null){
			return null;
		}
		if( item instanceof Tagged) {
			return ((Tagged)item).getTag();
		}
		for(String key : reg.keySet()){
			if( item.getClass() == reg.get(key)){
				return key;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(T item) {
		if( item == null) {
			return false;
		}
		if( clazz.isAssignableFrom(item.getClass())){
			if( item instanceof Tagged){
				return reg.containsKey(((Tagged)item).getTag());
			}
			return reg.containsValue(item.getClass());
		}
		return false;
	}
	public  boolean isEmpty() {
		// Don't try to  generate an item for this check
		// as an invalid value that resolves to an illegal class
		// will generate an exception
		// Also don't want to construct an item as part of this check
		String v = getValue();
		return v == null || v.isEmpty();
	}
}