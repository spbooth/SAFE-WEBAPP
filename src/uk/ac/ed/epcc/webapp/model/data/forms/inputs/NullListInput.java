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

import java.util.Iterator;
import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.SQLMatcher;
/** This is a wrapper round some other class that implements ListInput intended to
 * add a null-value option to the list. This is for use in SQLFormFilters to allow for null fields to be searched for
 * The null-value entry corresponds to -1 so the internal input must not support that value.
 * These inputs are optional by default as this is correct for the search forms..
 * @author spb
 * @param <T> Item type of the underlying ListInput
 *
 */


public class NullListInput<T extends Indexed>   implements ListInput<Integer,Object>,ParseInput<Integer>, SQLMatcher,OptionalInput{
    private ListInput<Integer,T> internal;
    public static final String NULLTAG="NULL-VALUE";
    public static final int NULL_VALUE=-1;
    private boolean optional=true;
    public NullListInput(ListInput<Integer,T> input){
    	internal = input;
    }
	@Override
	public Object getItembyValue(Integer value) {
		if( value == null ){
			return null;
		}
		if(value.intValue() == NULL_VALUE){
			return NULLTAG;
		}
		return internal.getItembyValue(value);
	}

	@Override
	public Iterator<Object> getItems() {
		LinkedList<Object> list = new LinkedList<>();
		list.add(NULLTAG);
		for(Iterator<T> it = internal.getItems(); it.hasNext();){
			list.add(it.next());
		}
		return list.iterator();
	}
	@Override
	public int getCount(){
		return internal.getCount();
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getTagByItem(Object item) {
		if( item == null ){
			return null;
		}
		if( item == NULLTAG){
			return ""+NULL_VALUE;
		}
		return internal.getTagByItem((T)item);
	}

	@Override
	public String getTagByValue(Integer value) {
		if( value == null ){
			return null;
		}
		return value.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getText(Object item) {
		if( item==NULLTAG){
			return NULLTAG;
		}
		return internal.getText((T)item);
	}
	@Override
	public String getPrettyString(Integer value) {
		if( value.intValue() == NULL_VALUE){
			return NULLTAG;
		}
		return internal.getPrettyString(value);
	}
	@Override
	public Object getItem() {
		Integer value = internal.getValue();
		if( value == null ){
			return null;
		}
		if( value.intValue() == NULL_VALUE){
			return NULLTAG;
		}
		return internal.getItembyValue(value);
	}

	@Override
	public void setItem(Object item) {
		if( item == null ){
			setValue(null);
			return;
		}
		if( item == NULLTAG){
			setValue(NULL_VALUE);
			return;
		}
		setValue(((Indexed)item).getID());
	}
	@Override
	public Integer setValue(Integer v) throws TypeError {
			return internal.setValue(v);		
	}
	@Override
	public  SQLFilter getSQLFilter(Class clazz,Repository res, String target,
			Object form_value) {
		if( form_value == null ){
			return null;
		}
		Integer i = (Integer) form_value;
		if( i.intValue() == NULL_VALUE ){
			return new NullFieldFilter(clazz,res,target,true);
		}
	    return new 	SQLValueFilter(clazz,res,target,i);
	}
	@Override
	public Integer convert(Object v) throws TypeError {
		return internal.convert(v);
	}
	@Override
	public String getKey() {
		return internal.getKey();
	}
	@Override
	public String getString(Integer value) {
		if( value == null ){
			return "";
		}
		if( value == NULL_VALUE){
			return NULLTAG;
		}
		return internal.getString(value);
	}
	@Override
	public Integer getValue() {
		Integer i = internal.getValue();
	    return i;
	}
	@Override
	public void setKey(String key) {
		internal.setKey(key);
		
	}
	@Override
	public void validate() throws FieldException {
		if( internal.getValue() == null || internal.getValue() == NULL_VALUE){
			return;
		}
		internal.validate();
	}
	@Override
	public String getString() {
	    if( internal.getValue() == NULL_VALUE){
	    	return NULLTAG;
	    }
	    if( internal.getValue() == null ){
	    	return "";
	    }
	    if( internal instanceof ParseInput){
		   return ((ParseInput)internal).getString();
	    }else{
	    	return internal.getValue().toString();
	    }
	}
	@Override
	public void parse(String v) throws ParseException {
		if( v == NULLTAG){
			internal.setValue(NULL_VALUE);
		}
		if( v == null || v == ""){
			internal.setValue(null);
		}
		if( internal instanceof ParseInput){
			((ParseInput)internal).parse(v);
		}else{
			internal.setValue(Integer.valueOf(v));
		}
		
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	@Override
	public boolean isOptional() {
		return optional;
	}
	@Override
	public void setOptional(boolean opt) {
		optional=opt;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(Object item) {
		if( item == null) {
			return false;
		}
		if(  item.equals(NULLTAG)){
			return true;
		}
		return internal.isValid((T) item);
	}
	
	

}