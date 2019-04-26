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
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.data.convert.EnumeratingTypeConverter;



public class TypeProducerInput<T> extends TextInput implements PreSelectInput<String,T>, OptionalListInput<String, T> {
    private final EnumeratingTypeConverter<T,String> t;
    private Set<T> item_set=null;
    private String unselected_text=null;
    private boolean pre_select=true;
    
    public void setSelectSet(Set<T> set){
    	item_set=set;
    }
    public TypeProducerInput(EnumeratingTypeConverter<T,String> prod){
    	super();// allow null values in case we are optional
    	t=prod;
    	addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String value) throws FieldException {
				try {
					if( t.find(value) == null){
						// not one of the valid types
						throw new ValidateException("Invalid input");
					}
				} catch (Exception e) {
					throw new ValidateException("Bad value");
				}
				
			}
		});
    }
    
    public EnumeratingTypeConverter<T,String> getProducer(){
    	return t;
    }
	@Override
	public T getItembyValue(String value) {

		try {
			return t.find(value);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Iterator<T> getItems() {
		if(item_set != null ){
			return item_set.iterator();
		}
		return t.getValues();
	}
	
	@Override
	public int getCount(){
		if( item_set != null ){
			return item_set.size();
		}
		Set<T> set = new HashSet<>();
		t.getValues(set);
		return set.size();
	}

	@Override
	public String getTagByItem(T item) {

		return t.getIndex(item);
		
	}

	@Override
	public String getTagByValue(String value) {
		return value;
	}

	@Override
	public String getText(T item) {
		if( item == null) {
			return null;
		}
		return item.toString();
	}

	@Override
	public T getItem() {
		String value =  getValue();
		if (value == null) {
			return null;
		}
		return getItembyValue(value);
	}

	@Override
	public void setItem(T item) {
		setValue(getTagByItem(item));
	}
	@Override
	public String getPrettyString(String val) {
		return getText(getItembyValue(val));
	}
	
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput#getUnselectedText()
	 */
	@Override
	public String getUnselectedText() {
		return unselected_text;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput#setUnselectedText(java.lang.String)
	 */
	@Override
	public void setUnselectedText(String text) {
		unselected_text=text;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput#allowPreSelect()
	 */
	@Override
	public boolean allowPreSelect() {
		return pre_select;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput#setPreSelect(boolean)
	 */
	@Override
	public void setPreSelect(boolean value) {
		pre_select=value;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(T item) {
		if( item == null ) {
			return false;
		}
		if( item_set != null ){
			return item_set.contains(item);
		}else {
			for(Iterator<T> it = t.getValues(); it.hasNext();) {
				T v = it.next();
				if( item.equals(v)) {
					return true;
				}
			}
		}
		return false;
	}

}