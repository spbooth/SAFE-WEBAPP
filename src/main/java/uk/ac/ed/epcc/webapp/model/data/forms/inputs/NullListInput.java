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
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObjectIntegerInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.SQLMatcher;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;
/** This is a wrapper round some other class that implements ListInput intended to
 * add a null-value option to the list. This is for use in SQLFormFilters to allow for null fields to be searched for
 * The null-value entry corresponds to -1 so the internal input must not support that value.
 * These inputs are optional by default as this is correct for the search forms..
 * @author spb
 * @param <T> Item type of the underlying ListInput
 *
 */


public class NullListInput<T extends Indexed>   implements AutoCompleteListInput<Integer,Object>,ParseInput<Integer>, SQLMatcher<T>,HTML5Input,FormatHintInput{
    private ListInput<Integer,T> internal;
    public static final String NULLTAG="NULL-VALUE";
    public static final int NULL_VALUE=-1;
    private boolean null_value=false;
   
    public NullListInput(ListInput<Integer,T> input){
    	internal = input;
    }
    
    private boolean useAuto() {
    	return internal instanceof AutoCompleteListInput;
    }
    private AutoCompleteListInput<Integer, T> getAuto(){
    	return (AutoCompleteListInput<Integer, T>) internal;
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
		return internal.getCount()+1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getTagByItem(Object item){
		if( item == null ){
			return null;
		}
		if( item == NULLTAG){
			return NULLTAG;
		}
		return internal.getTagByItem((T)item);
	}
	@Override
	public Object getItemByTag(String tag) {
		if( tag == null ) {
			return null;
		}
		if( tag.equals(NULLTAG)) {
			return NULLTAG;
		}
		return internal.getItemByTag(tag);
	}

	@Override
	public String getTagByValue(Integer value) {
		if( value == null ){
			return null;
		}
		if( value == NULL_VALUE ) {
			return NULLTAG;
		}
		return value.toString();
	}
	@Override
	public Integer getValueByTag(String tag) {
		if( tag == null) {
			return null;
		}
		if( tag.equals(NULLTAG)) {
			return NULL_VALUE;
		}
		return Integer.parseInt(tag);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String getText(Object item) {
		if( item == null ) {
			return null;
		}
		if( item==NULLTAG){
			return NULLTAG;
		}
		return internal.getText((T)item);
	}
	@Override
	public String getPrettyString(Integer value) {
		if( value == null ) {
			return null;
		}
		if( value.intValue() == NULL_VALUE){
			return NULLTAG;
		}
		return internal.getPrettyString(value);
	}
	@Override
	public Object getItem() {
		Integer value = internal.getValue();
		if( value == null ){
			if( null_value) {
				return NULLTAG;
			}
			return null;
		}
		if( value.intValue() == NULL_VALUE){  // this should not happen as we try not to ever put the null value into internal
			return NULLTAG;
		}
		return internal.getItembyValue(value);
	}

	@Override
	public final Integer getValueByItem(Object item) throws TypeException {
		if( item == null) {
			return null;
		}
		if( item == NULLTAG) {
			return NULL_VALUE;
		}
		return internal.getValueByItem((T) item);
	}
	@Override
	public Integer setValue(Integer v) throws TypeException {
		boolean was_null_value = null_value;
		null_value = false;
		Integer prev;
		if( v == null) {
			prev = internal.setValue(null);
		}else if( v.intValue() == NULL_VALUE) {
			null_value=true;
			prev = internal.getValue();
			internal.setNull();
		}else {	
			prev = internal.setValue(v);
		}
		if( was_null_value ) {
			return NULL_VALUE;
		}
		return prev;
	}
	@Override
	public  SQLFilter getSQLFilter(Repository res, String target,
			Object form_value) {
		if( form_value == null ){
			return null;
		}
		Integer i = (Integer) form_value;
		if( i.intValue() == NULL_VALUE ){
			return new NullFieldFilter(res,target,true);
		}
	    return new 	SQLValueFilter(res,target,i);
	}
	@Override
	public Integer convert(Object v) throws TypeException {
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
		if( null_value ) {
			return NULL_VALUE;
		}
		return internal.getValue();
	}
	@Override
	public void setKey(String key) {
		internal.setKey(key);
		
	}
	@Override
	public void validate() throws FieldException {
		if( null_value || internal.getValue() == null ){
			return;
		}
		internal.validate();
	}
	@Override
	public void validate(Integer value) throws FieldException {
		if( value.equals(NULL_VALUE)){
			return;
		}
		internal.validate(value);
	}
	@Override
	public String getString() {
	    if( null_value ){
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
	public Integer parseValue(String v) throws ParseException {
		if( v == NULLTAG){
			return NULL_VALUE;
		}
		if( v == null || v.isEmpty()){
			return null;
		}
		if( internal instanceof ParseInput){
			return ((ParseInput<Integer>)internal).parseValue(v);
		}else{
			return Integer.valueOf(v);
		}
		
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#addValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void addValidator(FieldValidator<Integer> val) {
		internal.addValidator(val);
		
	}
	@Override
	public void addValidatorSet(FieldValidationSet<Integer> val) {
		internal.addValidatorSet(val);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#removeValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void removeValidator(FieldValidator<Integer> val) {
		internal.removeValidator(val);
		
	}
	@Override
	public FieldValidationSet<Integer> getValidators() {
		return internal.getValidators();
	}
	/** wrap any Selector with a compatible input
	 * 
	 */
	public static  Selector getSelector(Selector s){
		if( s == null) {
			return null;
		}
		return new Selector() {

			@Override
			public Input getInput() {
				Input input = s.getInput();
				if( input != null && input instanceof ListInput && (input instanceof IntegerInput|| input instanceof DataObjectIntegerInput)){
					return new NullListInput((ListInput) input);
				}
				return input;
			}
			
		};
				
	}
	@Override
	public void setNull() {
		null_value = false;
		internal.setNull();
		
	}
	@Override
	public String getSuggestionText(Object item) {
		if( NULLTAG.equals(item)) {
			return NULLTAG;
		}
		return getAuto().getSuggestionText((T)item);
	}
	@Override
	public int getBoxWidth() {
		return getAuto().getBoxWidth();
	}
	@Override
	public void setBoxWidth(int l) {
		if( useAuto()) {
			getAuto().setBoxWidth(l);
		}
	}
	@Override
	public boolean getSingle() {
		return true;
	}
	@Override
	public boolean useListPresentation() {
		if( useAuto()) {
			return getAuto().useListPresentation();
		}
		return true;
	}
	public final  <R> R accept(InputVisitor<R> vis) throws Exception{
		if( useListPresentation()) {
			return vis.visitListInput(this);
		}else {
			return vis.visitAutoCompleteInput(this);
		}
	}

	@Override
	public String getFormatHint() {
		if( internal instanceof FormatHintInput) {
			return ((FormatHintInput)internal).getFormatHint();
		}
		return null;
	}

	@Override
	public String getType() {
		if( internal instanceof HTML5Input) {
			return ((HTML5Input)internal).getType();
		}
		return null;
	}
}