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

import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;




public class TextInput extends ParseAbstractInput<String> {

	private static final Pattern WHITESPACE = Pattern.compile("\\s");
	private boolean allow_null;
	private boolean trim=true;
	private boolean no_spaces=false;
	public TextInput() {
		this(false);
	}

	public TextInput(boolean allow_null) {
		super();
		this.allow_null = allow_null;
	}

	public String parseValue(String v) throws ParseException {
		if (v == null || v.length() == 0) {
			if (allow_null) {
				return null;
			} else {
				return "";
			}
		} else {
			if( force_single ){
				v = v.replace("\n", "");
			}
			if( getTrim()){
				v = v.trim();
			}
			return v;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#validate()
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		
			Object v = getValue();
			if (v != null && !(v instanceof String)) {
				throw new ValidateException("Invalid input type in TextInput "+v.getClass().getCanonicalName());
			}
			String s = (String) v;
			if (s != null && s.length() > getMaxResultLength() && getMaxResultLength() > 0) {
				throw new ValidateException("Input too long");
			}
			
			
			if( s != null && no_spaces && WHITESPACE.matcher(s).find()){
				throw new ValidateException("Input must not contain whitespace");
			}
		
	}

	public boolean getTrim() {
		return trim;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	/**
	 * @return the no_spaces
	 */
	public boolean isNoSpaces() {
		return no_spaces;
	}

	/**
	 * @param no_spaces the no_spaces to set
	 */
	public void setNoSpaces(boolean no_spaces) {
		this.no_spaces = no_spaces;
		if( no_spaces) {
			setTrim(true);
		}
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() || getValue().length() == 0;
	}

}