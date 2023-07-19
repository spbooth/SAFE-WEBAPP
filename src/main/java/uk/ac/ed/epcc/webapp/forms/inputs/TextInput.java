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

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;




public class TextInput extends ParseAbstractInput<String> {

	
	
	private boolean trim=true;
	public TextInput() {
		super();
		
	}

	@Override
	public String parseValue(String v) throws ParseException {
		if (v == null || v.length() == 0) {
			return null;
		} else {
			if( force_single ){
				v = mapToSingleLine(v);
			}
			if( getTrim()){
				v = v.trim();
			}
			return v;
		}
	}

	protected String mapToSingleLine(String v) {
		return v.replace("\n", "");
	}

	public boolean getTrim() {
		return trim;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}
	public void setText(String s) {
		try {
			setValue(s);
		} catch (TypeException e) {
			throw new TypeError(e);
		}
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() || getValue().length() == 0;
	}

	/** Adds a {@link MaxLengthValidator} to the input.
	 * 
	 * 
	 * 
	 * @param len
	 */
	public void setMaxResultLength(int len) {
		addValidator(new MaxLengthValidator(len));
	}
}