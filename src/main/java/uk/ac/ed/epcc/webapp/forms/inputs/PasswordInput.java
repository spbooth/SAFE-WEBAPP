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
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/**
 * Input for a password field. Works the same as a TextInput but is displayed
 * differently in a form
 * 
 * @author spb
 * 
 */


public class PasswordInput extends TextInput implements AutoCompleteHint {

	private int minimum_length=0;
	private String autocomplete=null;

	/**
	 * 
	 */
	public PasswordInput() {
		super();
		setBoxWidth(32);
		addValidator(new FieldValidator<String>() {
			
			@Override
			public void validate(String data) throws FieldException {
				if( minimum_length > 0 && data.length() < minimum_length){
					throw new ValidateException("Password is too short must be at least "+minimum_length+" characters");
				}
				
			}
		});
	}


	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitPasswordInput(this);
	}
	
	public void setMinimumLength(int min){
		this.minimum_length=min;
	}
	public int getMinimumLength(){
		return minimum_length;
	}


	@Override
	public String getAutoCompleteHint() {
		return autocomplete;
	}
	public void setAutoCompleteHint(String val) {
		autocomplete=val;
	}
}