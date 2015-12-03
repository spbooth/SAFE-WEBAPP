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

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input to select a target class from the set of definitions from the configuration service.
 * This defaults to the set of <b>classdef</b> definitions {@link AppContext#getPropertyClass(Class,String)}
 * The target class is further constrained to be assignable to a specific type.
 * For security reasons its important that this class cannot be forced to generate a class other than
 * than those specified.
 * @author spb
 *
 * @param <T>
 */


public class ClassInput<T> implements ListInput<String,Class<? extends T>>{
    private final AppContext c;
    private final String prefix;
    private String name;
    private String key;
    private Map<String,Class<? extends T>> reg;
    
    @SuppressWarnings("unchecked")
	public ClassInput(AppContext c,Class<T> target,boolean allow_non_instantiable, String p){
    	this.c=c;
    	prefix=p+".";
    	reg = new HashMap<String,Class<? extends T>>();
    	
    	Map<String,String> params = c.getInitParameters(prefix);
    	for(String name : params.keySet()){
    		String tag = name.substring(prefix.length());
    		try {
				Class cand = Class.forName(params.get(name));
				if( target.isAssignableFrom(cand)){
					if( allow_non_instantiable || (! cand.isInterface() && ! Modifier.isAbstract(cand.getModifiers()))){
						reg.put(tag, cand);
					}
				}
			} catch (ClassNotFoundException e) {
				// just skip
				c.error(e,"Class "+params.get(name)+" from parameter "+name+" not found");
			}
    	}
    }
    public ClassInput(AppContext c, Class<T> target){
    	this(c,target,false,"classdef");
    }
	public Class<? extends T> getItembyValue(String value) {
		if( value == null ){
			return null;
		}
		return reg.get(value);
	}

	public Iterator<Class<? extends T>> getItems() {
		return reg.values().iterator();
	}
	
	public int getCount(){
		return reg.size();
	}

	public String getTagByItem(Class<? extends T> item) {
		for(String key : reg.keySet()){
			if( item == reg.get(key)){
				return key;
			}
		}
		return null;
	}

	public String getTagByValue(String value) {
		return value;
	}

	public String getText(Class<? extends T> item) {
		String tag = getTagByItem(item);
		return c.getInitParameter("classinput.text."+prefix+tag, tag);
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

	public Class<? extends T> getItem() {
		return reg.get(name);
	}

	public void setItem(Class<? extends T> item) {
		name=getTagByItem(item);
		
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

}