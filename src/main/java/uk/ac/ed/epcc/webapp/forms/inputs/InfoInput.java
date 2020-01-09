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

/** Represents an UnmodifiableInput that takes no part in the
 * form validation it just displays informational text.
 * Similar to ConstantInput but cannot cache a value and always validates.
 *  
 * @author spb
 *
 */


public class InfoInput implements Input<String>, UnmodifiableInput{
    private final String label;
    private String key;
    public InfoInput(String text){
    	label=text;
    }
	@Override
	public String convert(Object v) throws TypeError {
		return null;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getPrettyString(String value) {
		return value;
	}

	@Override
	public String getString(String value) {
		return value;
	}

	@Override
	public String getValue() {
		return label;
	}

	@Override
	public void setKey(String key) {
		this.key=key;
	}

	@Override
	public String setValue(String v) throws TypeError {
		return label;
	}

	@Override
	public void validate() throws FieldException {
		return;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitUnmodifyableInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#addValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void addValidator(FieldValidator<String> val) {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#removeValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void removeValidator(FieldValidator<String> val) {
		
	}

}