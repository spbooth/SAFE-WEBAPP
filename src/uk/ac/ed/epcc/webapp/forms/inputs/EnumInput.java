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
 * the DB will contain the Enum name and the menu the result of the toString call.
 * 
 * @author spb
 *
 * @param <E> Type of Enum to use
 */


public class EnumInput<E extends Enum<E>> extends TextInput implements  ListInput<String,E>,OptionalListInput<String, E>,PreSelectInput<String, E> {
    EnumSet<E> set;
    Map<String,E> lookup;
    private String unslected_text=null;
    private boolean allow_preselect=true;
    public EnumInput(EnumSet<E> set){
    	super(true);
    	this.set = set;
    	lookup = new HashMap<>();
    	for(E s: set){
    		lookup.put(s.name(), s);
    	}
    }
    public EnumInput(Class<E> clazz){
    	this(EnumSet.allOf(clazz));
    }
	@Override
	public E getItembyValue(String value) {
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
	public String getTagByValue(String value) {
		return getTagByItem(getItembyValue(value));
	}

	@Override
	public String getText(E item) {
		if( item == null ){
			return null;
		}
		return item.toString();
	}

	@Override
	public E getItem() {
		String val = getValue();
		if( val == null ){
			return null;
		}
		return getItembyValue(val);
	}

	
	@Override
	public void setItem(E v) {
		if( v == null ){
			setValue(null);
		}
		setValue(getTagByItem(v));
	}
	
	@Override
	public String getPrettyString(String val) {
		if( val == null ){
			return "No Value";
		}
		return getText(getItembyValue(val));
	}
	@SuppressWarnings("unchecked")
	@Override
	public String convert(Object v) throws TypeError {
		if( v == null ){
			return null;
		}
		if( set.contains(v)){
			return getTagByItem((E) v);
		}
		return super.convert(v);
	}
	
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
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
	@Override
	public void validate() throws FieldException {
		super.validate();
		String value = getValue();
		if( value != null && ! lookup.containsKey(value)){
			throw new ValidateException("Value not permitted");
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(E item) {
		return set.contains(item);
	}

}