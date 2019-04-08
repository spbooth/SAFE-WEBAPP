//| Copyright - The University of Edinburgh 2017                            |
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

import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** A {@link TextInput} that protects against uploaded Html
 * @author spb
 *
 */
public class NoHtmlInput extends TextInput {
	
	/**
	 * 
	 */
	public NoHtmlInput() {
		addValidator(new NoHtmlValidator());
	}

	/**
	 * @param allow_null
	 */
	public NoHtmlInput(boolean allow_null) {
		super(allow_null);
		addValidator(new NoHtmlValidator());
	}
	boolean allowHtml=false;
	public static final Pattern HTML_PATTERN = Pattern.compile(">|<");
	public class NoHtmlValidator implements FieldValidator<String>{
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
		 */
		@Override
		public void validate(String value) throws FieldException {
			if( value != null && (! allowHtml) &&  HTML_PATTERN.matcher(value).find()){
				throw new ValidateException("The Characters > and < are not allowed");
			}
			
		}
		
	}

	/**
	 * @return the allowHtml
	 */
	public boolean isAllowHtml() {
		return allowHtml;
	}

	/**
	 * @param allowHtml the allowHtml to set
	 */
	public void setAllowHtml(boolean allowHtml) {
		this.allowHtml = allowHtml;
	}

}
