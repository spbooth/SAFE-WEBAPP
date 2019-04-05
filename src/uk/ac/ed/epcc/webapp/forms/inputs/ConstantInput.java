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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;

/** a read-only input that cannot be modified. For convenience in update forms it can cache an
 * object value but this should never actually be edited by the input instead the text label should be 
 * displayed. This input only validates against optional input
 * @param <V> type of value
 * 
 */


public class ConstantInput<V> implements UnmodifiableInput, Input<V> {
	private String label;

	private String key;

	private V value = null;


	public ConstantInput(String label){
		this(label,null);
	}
	public ConstantInput(String label,V value) {
		this.label = label;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getString(V val) {
		if (val == null) {
			return null;
		}
		return val.toString();
	}
	 public String getPrettyString(V val){
	    	if( val == null ){
	    		return "no value";
	    	}
	    	return getString(val);
	    }
    public final String getString(){
    	return getString(value);
    }
	public V getValue() {
		return value;
	}

	

	

	public void setKey(String key) {
		this.key = key;

	}

	public void setLabel(String s) {
		label = s;
	}

	

	@SuppressWarnings("unchecked")
	public V setValue(Object v) throws TypeError{
		V old = value;
		value = (V) v;
		return old;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate(boolean)
	 */
	public void validate() throws FieldException {
		
	}

	@SuppressWarnings("unchecked")
	public V convert(Object v) throws TypeError {
		return(V) v;
	}

	
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}

}