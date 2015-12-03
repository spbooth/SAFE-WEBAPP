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
import java.util.regex.PatternSyntaxException;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;


/** Input for a regular expression pattern.
 * 
 * @author spb
 *
 */
public class RegexpInput extends TextInput implements TagInput{

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.TextInput#validate(boolean)
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		String s = getValue();
		if (s != null) {
			try {
				Pattern.compile(s);
			} catch (PatternSyntaxException e) {
				throw new ParseException("Invalid regular expression");
			}
		}
	}

	public String getTag() {
		return "(Regular expression)";
	}

}