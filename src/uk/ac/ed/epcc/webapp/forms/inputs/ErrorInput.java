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

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** An ErrorInput is an unmodifiable input that never validates.
 * It can used to indicate that no valid selections are possible for the user
 * or that an error occurred while generating the form.
 * 
 * @author spb
 * @param <T> type of input
 *
 */


public class ErrorInput<T> implements UnmodifiableInput, Input<T> {

	
	private final String text;
	private String key;
	public ErrorInput(String text){
		this.text=text;
	}
	public String getLabel() {
		return text;
	}
	public T convert(Object v) throws TypeError {
		return null;
	}
	public String getKey() {
		return key;
	}
	public String getPrettyString(T value) {
		return null;
	}
	public String getString(T value) {
		return null;
	}
	public T getValue() {
		return null;
	}
	public void setKey(String key) {
		this.key=key;
	}
	public T setValue(T v) throws TypeError {
		return null;
	}
	@Override
	public boolean isEmpty() {
		// force validation
		return false;
	}
	public void validate() throws FieldException {
		throw new ValidateException("No legal value possible");
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#addValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void addValidator(FieldValidator<T> val) {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#removeValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void removeValidator(FieldValidator<T> val) {
		
	}

	
}