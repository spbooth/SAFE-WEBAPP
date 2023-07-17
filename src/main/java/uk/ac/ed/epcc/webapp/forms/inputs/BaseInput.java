//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.FieldValidationSet;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/**
 * @author Stephen Booth
 *
 * @param <V>
 */
public abstract class BaseInput<V> implements Input<V> {

	String key;
	protected FieldValidationSet<V> validators=new FieldValidationSet<>();

	/**
	 * 
	 */
	public BaseInput() {
		super();
	}

	@Override
	public final void addValidator(FieldValidator<V> val) {
		validators.add(val);
	}

	@Override
	public void addValidatorSet(FieldValidationSet<V> set) {
		if( set == null) {
			return;
		}
		validators.addAll(set);
	}

	@Override
	public final void removeValidator(FieldValidator<V> val) {
		validators.remove(val);
	}

	@Override
	public final String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	

	
	/**
	 * get a String representation of the value in a form that is compatible
	 * with the way the input is parsed.
	 * This provides a default implementation of a method requires by ParseInput
	 * @return String or null if no value
	 */
	public final String getString() {
	    if( isEmpty() ){
	    	return null;
	    }
		return getString(getValue());
	}
	/** get a String representation of an Object that is compatible with the way
	 * the input is parsed
	 * 
	 * @param val
	 * @return String or null if val is null
	 */
    @Override
	public String getString(V val){
    	if( val == null ){
    		return null;
    	}
    	return val.toString();
    }
    
    @Override
	public String getPrettyString(V val){
    	if( val == null ){
    		return "no value";
    	}
    	return getString(val);
    }
   
    @Override
	public final void validate() throws FieldException {
    	validateInner();
		if( isEmpty() ) {
			return;
		}
		V v = getValue();
		validate(v);
	}
    
    public final void validate(V value) throws FieldException {
    	for(FieldValidator<V> val : validators) {
			val.validate(value);
		}
    }
    /** Extension point to add validation to sub-class specific inner-state
     * 
     * @throws FieldException
     */
    protected void validateInner() throws FieldException{
    	
    }

	@Override
	public FieldValidationSet<V> getValidators() {
		return validators;
	}
}