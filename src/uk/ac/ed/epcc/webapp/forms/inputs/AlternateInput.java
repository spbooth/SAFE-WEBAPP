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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
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
 * @param <I> class of sub-inputs
 *
 */


public class AlternateInput<T> extends ParseMultiInput<T,Input<T>> implements OptionalInput{
    private boolean optional=false;
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
	public T setValue(T v) throws TypeError{
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
					i.setValue(null);
				}
			}else{
				i.setValue(null);
			}
		}
		return old;
	}

	
	public T convert(Object v) throws TypeError{
		TypeError e=null;
		// normally take this form the first input
		// but try alternatives if a TypeError is thrown
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			Input<T> i = it.next();
	        try{
	        	return i.convert(v);
	        }catch(TypeError e2){
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
	
		if( i  instanceof OptionalInput){
			((OptionalInput) i).setOptional(true);
		}else{
			if( i instanceof ListInput){
				// convert to optional
				i = new OptionalListInputWrapper((ListInput)i);
			}
		}
		super.addInput(sub_key, label,i);
	}
	
	public void validate() throws FieldException {
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			Input<? extends T> i = it.next();
			// Note unless the sub-inputs are optional we get a
			// missing field exception here
			try{
			    i.validate();
				// only one input needs to be valid
				break;
			
			}catch(MissingFieldException e){
				// treat inputs as optional even if not
			}
		}
	
		if(  ! isOptional()){
		
			// need to check one of the inputs has a non null value
			if( getValue() == null ){
				
				throw new MissingFieldException();
			}
		}
	}

	public boolean isOptional() {
	  return optional;
	}

	public void setOptional(boolean opt) {
		optional=opt;
	}

	public Map<String, Object> getMap() {
		Map<String,Object> m = new HashMap<String,Object>();
		for(Iterator<Input<T>> it = getInputs(); it.hasNext();){
			Input<T> i = it.next();
			if( i instanceof ParseMapInput){
				ParseMapInput pmi = (ParseMapInput)i;
				m.putAll(pmi.getMap());
			}else if(i instanceof ParseInput){
				ParseInput pi = (ParseInput)i;
				String key = pi.getKey();
				String text = pi.getString();
				if( text != null ){
					m.put(key,text);
				}
			}else{
				throw new ConsistencyError("Input cannot parse");
			}
		}
		return m;
	}

	public boolean parse(Map<String, Object> v) throws ParseException {
		String default_value = (String) v.get(getKey()); // value corresponding to this input
		boolean result = false;
		for(Iterator<Input<T>> it = getInputs(); it.hasNext();){
			Input<T> i = it.next();
			if( i instanceof ParseMapInput){
				result |= ((ParseMapInput)i).parse(v);
			}else if( i instanceof ParseInput){
				Object val = v.get(i.getKey());
				if( val != null  && ! (val instanceof String)){
					try{
					// Non sting object
						i.setValue(i.convert(val));
					}catch(TypeError e){
						// report the error.
						// TODO should TypeError by a reported excepion
						throw new ParseException("Illegal type conversion ",e);
					}
				}else{
					String text = (String) val;
					if(text == null){
						result = true;
						text=default_value;
					}
					((ParseInput)i).parse(text);
				}
			}else{
				throw new ConsistencyError("Input cannot parse");
			}
			// we want to abort parse on the first good match
			if( i.getValue() != null ){
				return result;
			}
		}
		return result;
	
	}

}