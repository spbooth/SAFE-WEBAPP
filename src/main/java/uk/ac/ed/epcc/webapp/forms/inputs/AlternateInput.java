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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** An alternateInput is a MultiInput consisting of
 * several different inputs of the same type.
 * Each sub-input is optional and the first input with a
 * value is taken
 * 
 * This is for implementing a pull-down of common options 
 * with a text-box for general input.
 * 
 * @author spb
 * @param <T> type of input
 *
 */


public class AlternateInput<T> extends ParseMultiInput<T,Input<T>> {

	// This is the wrapped parse input to use for parsing
	private ParseInput<T> parse_input;
	
	
	public AlternateInput() {
		super();
	}

	@Override
	public T getValue() {
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			Input<T> i = it.next();
			T val = i.getValue();
			if( val != null){
				return val;
			}
		}
		return null;
	}

	@Override
	public T setValue(T v) throws TypeException{
		T old = getValue();
		boolean set=false;
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			Input<T> i = it.next();
			// just set the first one and null the others
			if( ! set ){
				try{
					i.setValue(v);
					i.validate(); // was this good for this input
					set = true;
				}catch(FieldException e){
					i.setNull();
				}
			}else{
				i.setNull();;
			}
		}
		return old;
	}

	
	@Override
	public T convert(Object v) throws TypeException{
		TypeException e=null;
		// normally take this form the first input
		// but try alternatives if a TypeError is thrown
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			Input<T> i = it.next();
	        try{
	        	return i.convert(v);
	        }catch(TypeException e2){
	        	e = e2;
	        }
		}
	    if( e != null ){
	    	throw e;
	    }
	    return null;
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.MultiInput#addInput(java.lang.String, uk.ac.ed.epcc.webapp.model.data.forms.Input)
	 */
	@Override
	public void addInput(String sub_key, String label,Input<T> i) {
		super.addInput(sub_key, label,i);
		if( i instanceof ParseInput) {
			parse_input=(ParseInput<T>) i;
		}
	}
	
	@Override
	public boolean requireAll() {
		return false;
	}

	@Override
	public T parseValue(String v) throws ParseException {
		if( parse_input != null ) {
			return parse_input.parseValue(v);
		}
		try {
			return convert(v);
		} catch (TypeException e) {
			throw new ParseException(e);
		}
	}

	@Override
	public String getString(T val) {
		if(parse_input != null) {
			return parse_input.getString(val);
		}
		return super.getString(val);
	}


	

	

}