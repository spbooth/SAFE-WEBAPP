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

import java.util.*;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** Input to select values from a Java Enum 
 * the DB will contain the Enum name and the menu the result of the toString call.
 * 
 * @author spb
 *
 * @param <E> Type of Enum to use
 */


public class EnumInput<E extends Enum<E>> extends CodeListInput<E> implements OptionalListInput<String, E>,PreSelectInput<String, E> {
    EnumSet<E> set;
    Map<String,E> lookup;
    private String unslected_text=null;
    private boolean allow_preselect=true;
    public EnumInput(EnumSet<E> set){
    	super();
    	this.set = set;
    	lookup = new HashMap<>();
    	for(E s: set){
    		lookup.put(s.name(), s);
    	}
    	addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String data) throws FieldException {
				if( data != null && ! lookup.containsKey(data)){
					throw new ValidateException("Value not permitted");
				}
				
			}
		});
    }
    public EnumInput(Class<E> clazz){
    	this(EnumSet.allOf(clazz));
    }
	@Override
	public E getItemByTag(String value) {
		if( value == null ){
			return null;
		}
		return lookup.get(value);
	}

	@Override
	public Iterator<E> getItems() {
		return set.iterator();
	}
	
	@Override
	public int getCount(){
		return set.size();
	}

	@Override
	public String getTagByItem(E item) {
		if( item == null){
			return null;
		}
		return item.name();
	}
	
	@Override
	public String getText(E item) {
		if( item == null ){
			return null;
		}
		return item.toString();
	}

	@Override
	public String getPrettyString(String val) {
		if( val == null ){
			return "No Value";
		}
		return getText(getItembyValue(val));
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput#allowPreSelect()
	 */
	@Override
	public boolean allowPreSelect() {
		return allow_preselect;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput#setPreSelect(boolean)
	 */
	@Override
	public void setPreSelect(boolean value) {
		allow_preselect=value;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput#getUnselectedText()
	 */
	@Override
	public String getUnselectedText() {
		return unslected_text;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput#setUnselectedText(java.lang.String)
	 */
	@Override
	public void setUnselectedText(String text) {
		unslected_text=text;
		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(E item) {
		return set.contains(item);
	}
	@Override
	public String convert(Object v) throws TypeException {
		if( v == null || v instanceof String) {
			return (String) v;
		}
		if( v instanceof Enum) {
			return ((Enum)v).name();
		}
		throw new TypeException(v.getClass());
	}
}